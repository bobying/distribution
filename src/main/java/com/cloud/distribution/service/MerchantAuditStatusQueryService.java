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

import com.cloud.distribution.domain.MerchantAuditStatus;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.MerchantAuditStatusRepository;
import com.cloud.distribution.repository.search.MerchantAuditStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantAuditStatusCriteria;

import com.cloud.distribution.service.dto.MerchantAuditStatusDTO;
import com.cloud.distribution.service.mapper.MerchantAuditStatusMapper;

/**
 * Service for executing complex queries for MerchantAuditStatus entities in the database.
 * The main input is a {@link MerchantAuditStatusCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MerchantAuditStatusDTO} or a {@link Page} of {@link MerchantAuditStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MerchantAuditStatusQueryService extends QueryService<MerchantAuditStatus> {

    private final Logger log = LoggerFactory.getLogger(MerchantAuditStatusQueryService.class);


    private final MerchantAuditStatusRepository merchantAuditStatusRepository;

    private final MerchantAuditStatusMapper merchantAuditStatusMapper;

    private final MerchantAuditStatusSearchRepository merchantAuditStatusSearchRepository;

    public MerchantAuditStatusQueryService(MerchantAuditStatusRepository merchantAuditStatusRepository, MerchantAuditStatusMapper merchantAuditStatusMapper, MerchantAuditStatusSearchRepository merchantAuditStatusSearchRepository) {
        this.merchantAuditStatusRepository = merchantAuditStatusRepository;
        this.merchantAuditStatusMapper = merchantAuditStatusMapper;
        this.merchantAuditStatusSearchRepository = merchantAuditStatusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MerchantAuditStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MerchantAuditStatusDTO> findByCriteria(MerchantAuditStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<MerchantAuditStatus> specification = createSpecification(criteria);
        return merchantAuditStatusMapper.toDto(merchantAuditStatusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MerchantAuditStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MerchantAuditStatusDTO> findByCriteria(MerchantAuditStatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<MerchantAuditStatus> specification = createSpecification(criteria);
        final Page<MerchantAuditStatus> result = merchantAuditStatusRepository.findAll(specification, page);
        return result.map(merchantAuditStatusMapper::toDto);
    }

    /**
     * Function to convert MerchantAuditStatusCriteria to a {@link Specifications}
     */
    private Specifications<MerchantAuditStatus> createSpecification(MerchantAuditStatusCriteria criteria) {
        Specifications<MerchantAuditStatus> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), MerchantAuditStatus_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MerchantAuditStatus_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), MerchantAuditStatus_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), MerchantAuditStatus_.status, Status_.id));
            }
        }
        return specification;
    }

}
