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

import com.cloud.distribution.domain.OrderType;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.OrderTypeRepository;
import com.cloud.distribution.repository.search.OrderTypeSearchRepository;
import com.cloud.distribution.service.dto.OrderTypeCriteria;

import com.cloud.distribution.service.dto.OrderTypeDTO;
import com.cloud.distribution.service.mapper.OrderTypeMapper;

/**
 * Service for executing complex queries for OrderType entities in the database.
 * The main input is a {@link OrderTypeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderTypeDTO} or a {@link Page} of {@link OrderTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderTypeQueryService extends QueryService<OrderType> {

    private final Logger log = LoggerFactory.getLogger(OrderTypeQueryService.class);


    private final OrderTypeRepository orderTypeRepository;

    private final OrderTypeMapper orderTypeMapper;

    private final OrderTypeSearchRepository orderTypeSearchRepository;

    public OrderTypeQueryService(OrderTypeRepository orderTypeRepository, OrderTypeMapper orderTypeMapper, OrderTypeSearchRepository orderTypeSearchRepository) {
        this.orderTypeRepository = orderTypeRepository;
        this.orderTypeMapper = orderTypeMapper;
        this.orderTypeSearchRepository = orderTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link OrderTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderTypeDTO> findByCriteria(OrderTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<OrderType> specification = createSpecification(criteria);
        return orderTypeMapper.toDto(orderTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderTypeDTO> findByCriteria(OrderTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<OrderType> specification = createSpecification(criteria);
        final Page<OrderType> result = orderTypeRepository.findAll(specification, page);
        return result.map(orderTypeMapper::toDto);
    }

    /**
     * Function to convert OrderTypeCriteria to a {@link Specifications}
     */
    private Specifications<OrderType> createSpecification(OrderTypeCriteria criteria) {
        Specifications<OrderType> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OrderType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), OrderType_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), OrderType_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), OrderType_.status, Status_.id));
            }
        }
        return specification;
    }

}
