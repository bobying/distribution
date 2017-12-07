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

import com.cloud.distribution.domain.Order;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.OrderRepository;
import com.cloud.distribution.repository.search.OrderSearchRepository;
import com.cloud.distribution.service.dto.OrderCriteria;

import com.cloud.distribution.service.dto.OrderDTO;
import com.cloud.distribution.service.mapper.OrderMapper;

/**
 * Service for executing complex queries for Order entities in the database.
 * The main input is a {@link OrderCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderDTO} or a {@link Page} of {@link OrderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderQueryService extends QueryService<Order> {

    private final Logger log = LoggerFactory.getLogger(OrderQueryService.class);


    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderSearchRepository orderSearchRepository;

    public OrderQueryService(OrderRepository orderRepository, OrderMapper orderMapper, OrderSearchRepository orderSearchRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderSearchRepository = orderSearchRepository;
    }

    /**
     * Return a {@link List} of {@link OrderDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> findByCriteria(OrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Order> specification = createSpecification(criteria);
        return orderMapper.toDto(orderRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByCriteria(OrderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Order> specification = createSpecification(criteria);
        final Page<Order> result = orderRepository.findAll(specification, page);
        return result.map(orderMapper::toDto);
    }

    /**
     * Function to convert OrderCriteria to a {@link Specifications}
     */
    private Specifications<Order> createSpecification(OrderCriteria criteria) {
        Specifications<Order> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Order_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Order_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Order_.code));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Order_.amount));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Order_.createdDate));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUserId(), Order_.userId));
            }
            if (criteria.getPayTypeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getPayTypeId(), Order_.payType, PayType_.id));
            }
            if (criteria.getOrderTypeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getOrderTypeId(), Order_.orderType, OrderType_.id));
            }
            if (criteria.getOrderStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getOrderStatusId(), Order_.orderStatus, OrderStatus_.id));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getProductId(), Order_.product, Product_.id));
            }
        }
        return specification;
    }

}
