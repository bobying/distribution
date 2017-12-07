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

import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.OrderStatusRepository;
import com.cloud.distribution.repository.search.OrderStatusSearchRepository;
import com.cloud.distribution.service.dto.OrderStatusCriteria;

import com.cloud.distribution.service.dto.OrderStatusDTO;
import com.cloud.distribution.service.mapper.OrderStatusMapper;

/**
 * Service for executing complex queries for OrderStatus entities in the database.
 * The main input is a {@link OrderStatusCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderStatusDTO} or a {@link Page} of {@link OrderStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderStatusQueryService extends QueryService<OrderStatus> {

    private final Logger log = LoggerFactory.getLogger(OrderStatusQueryService.class);


    private final OrderStatusRepository orderStatusRepository;

    private final OrderStatusMapper orderStatusMapper;

    private final OrderStatusSearchRepository orderStatusSearchRepository;

    public OrderStatusQueryService(OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper, OrderStatusSearchRepository orderStatusSearchRepository) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMapper = orderStatusMapper;
        this.orderStatusSearchRepository = orderStatusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link OrderStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusDTO> findByCriteria(OrderStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<OrderStatus> specification = createSpecification(criteria);
        return orderStatusMapper.toDto(orderStatusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusDTO> findByCriteria(OrderStatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<OrderStatus> specification = createSpecification(criteria);
        final Page<OrderStatus> result = orderStatusRepository.findAll(specification, page);
        return result.map(orderStatusMapper::toDto);
    }

    /**
     * Function to convert OrderStatusCriteria to a {@link Specifications}
     */
    private Specifications<OrderStatus> createSpecification(OrderStatusCriteria criteria) {
        Specifications<OrderStatus> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OrderStatus_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), OrderStatus_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), OrderStatus_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), OrderStatus_.status, Status_.id));
            }
        }
        return specification;
    }

}
