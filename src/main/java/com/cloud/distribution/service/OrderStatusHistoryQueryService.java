package com.cloud.distribution.service;


import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cloud.distribution.domain.OrderStatusHistory;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.OrderStatusHistoryRepository;
import com.cloud.distribution.repository.search.OrderStatusHistorySearchRepository;
import com.cloud.distribution.service.dto.OrderStatusHistoryCriteria;

import com.cloud.distribution.service.dto.OrderStatusHistoryDTO;
import com.cloud.distribution.service.mapper.OrderStatusHistoryMapper;

/**
 * Service for executing complex queries for OrderStatusHistory entities in the database.
 * The main input is a {@link OrderStatusHistoryCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderStatusHistoryDTO} or a {@link Page} of {@link OrderStatusHistoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderStatusHistoryQueryService extends QueryService<OrderStatusHistory> {

    private final Logger log = LoggerFactory.getLogger(OrderStatusHistoryQueryService.class);


    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    private final OrderStatusHistorySearchRepository orderStatusHistorySearchRepository;

    public OrderStatusHistoryQueryService(OrderStatusHistoryRepository orderStatusHistoryRepository, OrderStatusHistoryMapper orderStatusHistoryMapper, OrderStatusHistorySearchRepository orderStatusHistorySearchRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
        this.orderStatusHistorySearchRepository = orderStatusHistorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link OrderStatusHistoryDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryDTO> findByCriteria(OrderStatusHistoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<OrderStatusHistory> specification = createSpecification(criteria);
        return orderStatusHistoryMapper.toDto(orderStatusHistoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderStatusHistoryDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusHistoryDTO> findByCriteria(OrderStatusHistoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<OrderStatusHistory> specification = createSpecification(criteria);
        final Page<OrderStatusHistory> result = orderStatusHistoryRepository.findAll(specification, page);
        return result.map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Function to convert OrderStatusHistoryCriteria to a {@link Specifications}
     */
    private Specifications<OrderStatusHistory> createSpecification(OrderStatusHistoryCriteria criteria) {
        Specifications<OrderStatusHistory> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), OrderStatusHistory_.id));
            }
            if (criteria.getModifiedTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getModifiedTime(), OrderStatusHistory_.modifiedTime));
            }
            if (criteria.getOperatorCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOperatorCode(), OrderStatusHistory_.operatorCode));
            }
            if (criteria.getOperatorTypeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getOperatorTypeId(), OrderStatusHistory_.operatorType, OperatorType_.id));
            }
            if (criteria.getOrderId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getOrderId(), OrderStatusHistory_.order, Order_.id));
            }
            if (criteria.getOldStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getOldStatusId(), OrderStatusHistory_.oldStatus, OrderStatus_.id));
            }
            if (criteria.getNewStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getNewStatusId(), OrderStatusHistory_.newStatus, OrderStatus_.id));
            }
        }
        return specification;
    }

}
