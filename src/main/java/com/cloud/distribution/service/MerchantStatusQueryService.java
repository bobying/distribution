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

import com.cloud.distribution.domain.MerchantStatus;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.MerchantStatusRepository;
import com.cloud.distribution.repository.search.MerchantStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantStatusCriteria;

import com.cloud.distribution.service.dto.MerchantStatusDTO;
import com.cloud.distribution.service.mapper.MerchantStatusMapper;

/**
 * Service for executing complex queries for MerchantStatus entities in the database.
 * The main input is a {@link MerchantStatusCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MerchantStatusDTO} or a {@link Page} of {@link MerchantStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MerchantStatusQueryService extends QueryService<MerchantStatus> {

    private final Logger log = LoggerFactory.getLogger(MerchantStatusQueryService.class);


    private final MerchantStatusRepository merchantStatusRepository;

    private final MerchantStatusMapper merchantStatusMapper;

    private final MerchantStatusSearchRepository merchantStatusSearchRepository;

    public MerchantStatusQueryService(MerchantStatusRepository merchantStatusRepository, MerchantStatusMapper merchantStatusMapper, MerchantStatusSearchRepository merchantStatusSearchRepository) {
        this.merchantStatusRepository = merchantStatusRepository;
        this.merchantStatusMapper = merchantStatusMapper;
        this.merchantStatusSearchRepository = merchantStatusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MerchantStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MerchantStatusDTO> findByCriteria(MerchantStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<MerchantStatus> specification = createSpecification(criteria);
        return merchantStatusMapper.toDto(merchantStatusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MerchantStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MerchantStatusDTO> findByCriteria(MerchantStatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<MerchantStatus> specification = createSpecification(criteria);
        final Page<MerchantStatus> result = merchantStatusRepository.findAll(specification, page);
        return result.map(merchantStatusMapper::toDto);
    }

    /**
     * Function to convert MerchantStatusCriteria to a {@link Specifications}
     */
    private Specifications<MerchantStatus> createSpecification(MerchantStatusCriteria criteria) {
        Specifications<MerchantStatus> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), MerchantStatus_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MerchantStatus_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), MerchantStatus_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), MerchantStatus_.status, Status_.id));
            }
        }
        return specification;
    }

}
