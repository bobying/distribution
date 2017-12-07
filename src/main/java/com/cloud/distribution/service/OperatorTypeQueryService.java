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

import com.cloud.distribution.domain.OperatorType;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.OperatorTypeRepository;
import com.cloud.distribution.repository.search.OperatorTypeSearchRepository;
import com.cloud.distribution.service.dto.OperatorTypeCriteria;

import com.cloud.distribution.service.dto.OperatorTypeDTO;
import com.cloud.distribution.service.mapper.OperatorTypeMapper;

/**
 * Service for executing complex queries for OperatorType entities in the database.
 * The main input is a {@link OperatorTypeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OperatorTypeDTO} or a {@link Page} of {@link OperatorTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OperatorTypeQueryService extends QueryService<OperatorType> {

    private final Logger log = LoggerFactory.getLogger(OperatorTypeQueryService.class);


    private final OperatorTypeRepository operatorTypeRepository;

    private final OperatorTypeMapper operatorTypeMapper;

    private final OperatorTypeSearchRepository operatorTypeSearchRepository;

    public OperatorTypeQueryService(OperatorTypeRepository operatorTypeRepository, OperatorTypeMapper operatorTypeMapper, OperatorTypeSearchRepository operatorTypeSearchRepository) {
        this.operatorTypeRepository = operatorTypeRepository;
        this.operatorTypeMapper = operatorTypeMapper;
        this.operatorTypeSearchRepository = operatorTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link OperatorTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OperatorTypeDTO> findByCriteria(OperatorTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<OperatorType> specification = createSpecification(criteria);
        return operatorTypeMapper.toDto(operatorTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OperatorTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OperatorTypeDTO> findByCriteria(OperatorTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<OperatorType> specification = createSpecification(criteria);
        final Page<OperatorType> result = operatorTypeRepository.findAll(specification, page);
        return result.map(operatorTypeMapper::toDto);
    }

    /**
     * Function to convert OperatorTypeCriteria to a {@link Specifications}
     */
    private Specifications<OperatorType> createSpecification(OperatorTypeCriteria criteria) {
        Specifications<OperatorType> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OperatorType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), OperatorType_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), OperatorType_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), OperatorType_.status, Status_.id));
            }
        }
        return specification;
    }

}
