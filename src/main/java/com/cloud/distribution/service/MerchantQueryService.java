package com.cloud.distribution.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cloud.distribution.domain.Merchant;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.MerchantRepository;
import com.cloud.distribution.repository.search.MerchantSearchRepository;
import com.cloud.distribution.service.dto.MerchantCriteria;

import com.cloud.distribution.service.dto.MerchantDTO;
import com.cloud.distribution.service.mapper.MerchantMapper;

/**
 * Service for executing complex queries for Merchant entities in the database.
 * The main input is a {@link MerchantCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MerchantDTO} or a {@link Page} of {@link MerchantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MerchantQueryService extends QueryService<Merchant> {

    private final Logger log = LoggerFactory.getLogger(MerchantQueryService.class);


    private final MerchantRepository merchantRepository;

    private final MerchantMapper merchantMapper;

    private final MerchantSearchRepository merchantSearchRepository;

    public MerchantQueryService(MerchantRepository merchantRepository, MerchantMapper merchantMapper, MerchantSearchRepository merchantSearchRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
        this.merchantSearchRepository = merchantSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MerchantDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MerchantDTO> findByCriteria(MerchantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Merchant> specification = createSpecification(criteria);
        return merchantMapper.toDto(merchantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MerchantDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MerchantDTO> findByCriteria(MerchantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Merchant> specification = createSpecification(criteria);
        final Page<Merchant> result = merchantRepository.findAll(specification, page);
        return result.map(merchantMapper::toDto);
    }

    /**
     * Function to convert MerchantCriteria to a {@link Specifications}
     */
    private Specifications<Merchant> createSpecification(MerchantCriteria criteria) {
        Specifications<Merchant> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Merchant_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Merchant_.name));
            }
            if (criteria.getLevel() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLevel(), Merchant_.level));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), Merchant_.userId));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Merchant_.address));
            }
            if (criteria.getMobile() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMobile(), Merchant_.mobile));
            }
            if (criteria.getMerchantTypeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getMerchantTypeId(), Merchant_.merchantType, MerchantType_.id));
            }
            if (criteria.getMerchantAuditStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getMerchantAuditStatusId(), Merchant_.merchantAuditStatus, MerchantAuditStatus_.id));
            }
            if (criteria.getMerchantStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getMerchantStatusId(), Merchant_.merchantStatus, MerchantStatus_.id));
            }
            if (criteria.getParentId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getParentId(), Merchant_.parent, Merchant_.id));
            }
        }
        return specification;
    }

}
