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

import com.cloud.distribution.domain.PayType;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.PayTypeRepository;
import com.cloud.distribution.repository.search.PayTypeSearchRepository;
import com.cloud.distribution.service.dto.PayTypeCriteria;

import com.cloud.distribution.service.dto.PayTypeDTO;
import com.cloud.distribution.service.mapper.PayTypeMapper;

/**
 * Service for executing complex queries for PayType entities in the database.
 * The main input is a {@link PayTypeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PayTypeDTO} or a {@link Page} of {@link PayTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PayTypeQueryService extends QueryService<PayType> {

    private final Logger log = LoggerFactory.getLogger(PayTypeQueryService.class);


    private final PayTypeRepository payTypeRepository;

    private final PayTypeMapper payTypeMapper;

    private final PayTypeSearchRepository payTypeSearchRepository;

    public PayTypeQueryService(PayTypeRepository payTypeRepository, PayTypeMapper payTypeMapper, PayTypeSearchRepository payTypeSearchRepository) {
        this.payTypeRepository = payTypeRepository;
        this.payTypeMapper = payTypeMapper;
        this.payTypeSearchRepository = payTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PayTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PayTypeDTO> findByCriteria(PayTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<PayType> specification = createSpecification(criteria);
        return payTypeMapper.toDto(payTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PayTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PayTypeDTO> findByCriteria(PayTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<PayType> specification = createSpecification(criteria);
        final Page<PayType> result = payTypeRepository.findAll(specification, page);
        return result.map(payTypeMapper::toDto);
    }

    /**
     * Function to convert PayTypeCriteria to a {@link Specifications}
     */
    private Specifications<PayType> createSpecification(PayTypeCriteria criteria) {
        Specifications<PayType> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), PayType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PayType_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), PayType_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), PayType_.status, Status_.id));
            }
        }
        return specification;
    }

}
