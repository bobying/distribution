package com.cloud.distribution.service;

import com.cloud.distribution.domain.OrderType;
import com.cloud.distribution.repository.OrderTypeRepository;
import com.cloud.distribution.repository.search.OrderTypeSearchRepository;
import com.cloud.distribution.service.dto.OrderTypeDTO;
import com.cloud.distribution.service.mapper.OrderTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing OrderType.
 */
@Service
@Transactional
public class OrderTypeService {

    private final Logger log = LoggerFactory.getLogger(OrderTypeService.class);

    private final OrderTypeRepository orderTypeRepository;

    private final OrderTypeMapper orderTypeMapper;

    private final OrderTypeSearchRepository orderTypeSearchRepository;

    public OrderTypeService(OrderTypeRepository orderTypeRepository, OrderTypeMapper orderTypeMapper, OrderTypeSearchRepository orderTypeSearchRepository) {
        this.orderTypeRepository = orderTypeRepository;
        this.orderTypeMapper = orderTypeMapper;
        this.orderTypeSearchRepository = orderTypeSearchRepository;
    }

    /**
     * Save a orderType.
     *
     * @param orderTypeDTO the entity to save
     * @return the persisted entity
     */
    public OrderTypeDTO save(OrderTypeDTO orderTypeDTO) {
        log.debug("Request to save OrderType : {}", orderTypeDTO);
        OrderType orderType = orderTypeMapper.toEntity(orderTypeDTO);
        orderType = orderTypeRepository.save(orderType);
        OrderTypeDTO result = orderTypeMapper.toDto(orderType);
        orderTypeSearchRepository.save(orderType);
        return result;
    }

    /**
     * Get all the orderTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderTypes");
        return orderTypeRepository.findAll(pageable)
            .map(orderTypeMapper::toDto);
    }

    /**
     * Get one orderType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public OrderTypeDTO findOne(Long id) {
        log.debug("Request to get OrderType : {}", id);
        OrderType orderType = orderTypeRepository.findOne(id);
        return orderTypeMapper.toDto(orderType);
    }

    /**
     * Delete the orderType by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderType : {}", id);
        orderTypeRepository.delete(id);
        orderTypeSearchRepository.delete(id);
    }

    /**
     * Search for the orderType corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrderTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderTypes for query {}", query);
        Page<OrderType> result = orderTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(orderTypeMapper::toDto);
    }
}
