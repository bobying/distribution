package com.cloud.distribution.service;

import com.cloud.distribution.domain.OrderStatusHistory;
import com.cloud.distribution.repository.OrderStatusHistoryRepository;
import com.cloud.distribution.repository.search.OrderStatusHistorySearchRepository;
import com.cloud.distribution.service.dto.OrderStatusHistoryDTO;
import com.cloud.distribution.service.mapper.OrderStatusHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing OrderStatusHistory.
 */
@Service
@Transactional
public class OrderStatusHistoryService {

    private final Logger log = LoggerFactory.getLogger(OrderStatusHistoryService.class);

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final OrderStatusHistoryMapper orderStatusHistoryMapper;

    private final OrderStatusHistorySearchRepository orderStatusHistorySearchRepository;

    public OrderStatusHistoryService(OrderStatusHistoryRepository orderStatusHistoryRepository, OrderStatusHistoryMapper orderStatusHistoryMapper, OrderStatusHistorySearchRepository orderStatusHistorySearchRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderStatusHistoryMapper = orderStatusHistoryMapper;
        this.orderStatusHistorySearchRepository = orderStatusHistorySearchRepository;
    }

    /**
     * Save a orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the entity to save
     * @return the persisted entity
     */
    public OrderStatusHistoryDTO save(OrderStatusHistoryDTO orderStatusHistoryDTO) {
        log.debug("Request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryMapper.toEntity(orderStatusHistoryDTO);
        orderStatusHistory = orderStatusHistoryRepository.save(orderStatusHistory);
        OrderStatusHistoryDTO result = orderStatusHistoryMapper.toDto(orderStatusHistory);
        orderStatusHistorySearchRepository.save(orderStatusHistory);
        return result;
    }

    /**
     * Get all the orderStatusHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderStatusHistories");
        return orderStatusHistoryRepository.findAll(pageable)
            .map(orderStatusHistoryMapper::toDto);
    }

    /**
     * Get one orderStatusHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public OrderStatusHistoryDTO findOne(Long id) {
        log.debug("Request to get OrderStatusHistory : {}", id);
        OrderStatusHistory orderStatusHistory = orderStatusHistoryRepository.findOne(id);
        return orderStatusHistoryMapper.toDto(orderStatusHistory);
    }

    /**
     * Delete the orderStatusHistory by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderStatusHistory : {}", id);
        orderStatusHistoryRepository.delete(id);
        orderStatusHistorySearchRepository.delete(id);
    }

    /**
     * Search for the orderStatusHistory corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderStatusHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderStatusHistories for query {}", query);
        Page<OrderStatusHistory> result = orderStatusHistorySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(orderStatusHistoryMapper::toDto);
    }
}
