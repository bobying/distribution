package com.cloud.distribution.service;

import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.repository.OrderStatusRepository;
import com.cloud.distribution.repository.search.OrderStatusSearchRepository;
import com.cloud.distribution.service.dto.OrderStatusDTO;
import com.cloud.distribution.service.mapper.OrderStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing OrderStatus.
 */
@Service
@Transactional
public class OrderStatusService {

    private final Logger log = LoggerFactory.getLogger(OrderStatusService.class);

    private final OrderStatusRepository orderStatusRepository;

    private final OrderStatusMapper orderStatusMapper;

    private final OrderStatusSearchRepository orderStatusSearchRepository;

    public OrderStatusService(OrderStatusRepository orderStatusRepository, OrderStatusMapper orderStatusMapper, OrderStatusSearchRepository orderStatusSearchRepository) {
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusMapper = orderStatusMapper;
        this.orderStatusSearchRepository = orderStatusSearchRepository;
    }

    /**
     * Save a orderStatus.
     *
     * @param orderStatusDTO the entity to save
     * @return the persisted entity
     */
    public OrderStatusDTO save(OrderStatusDTO orderStatusDTO) {
        log.debug("Request to save OrderStatus : {}", orderStatusDTO);
        OrderStatus orderStatus = orderStatusMapper.toEntity(orderStatusDTO);
        orderStatus = orderStatusRepository.save(orderStatus);
        OrderStatusDTO result = orderStatusMapper.toDto(orderStatus);
        orderStatusSearchRepository.save(orderStatus);
        return result;
    }

    /**
     * Get all the orderStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderStatuses");
        return orderStatusRepository.findAll(pageable)
            .map(orderStatusMapper::toDto);
    }

    /**
     * Get one orderStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public OrderStatusDTO findOne(Long id) {
        log.debug("Request to get OrderStatus : {}", id);
        OrderStatus orderStatus = orderStatusRepository.findOne(id);
        return orderStatusMapper.toDto(orderStatus);
    }

    /**
     * Delete the orderStatus by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderStatus : {}", id);
        orderStatusRepository.delete(id);
        orderStatusSearchRepository.delete(id);
    }

    /**
     * Search for the orderStatus corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderStatuses for query {}", query);
        Page<OrderStatus> result = orderStatusSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(orderStatusMapper::toDto);
    }
}
