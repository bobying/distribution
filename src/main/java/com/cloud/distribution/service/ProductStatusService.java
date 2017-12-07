package com.cloud.distribution.service;

import com.cloud.distribution.domain.ProductStatus;
import com.cloud.distribution.repository.ProductStatusRepository;
import com.cloud.distribution.repository.search.ProductStatusSearchRepository;
import com.cloud.distribution.service.dto.ProductStatusDTO;
import com.cloud.distribution.service.mapper.ProductStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProductStatus.
 */
@Service
@Transactional
public class ProductStatusService {

    private final Logger log = LoggerFactory.getLogger(ProductStatusService.class);

    private final ProductStatusRepository productStatusRepository;

    private final ProductStatusMapper productStatusMapper;

    private final ProductStatusSearchRepository productStatusSearchRepository;

    public ProductStatusService(ProductStatusRepository productStatusRepository, ProductStatusMapper productStatusMapper, ProductStatusSearchRepository productStatusSearchRepository) {
        this.productStatusRepository = productStatusRepository;
        this.productStatusMapper = productStatusMapper;
        this.productStatusSearchRepository = productStatusSearchRepository;
    }

    /**
     * Save a productStatus.
     *
     * @param productStatusDTO the entity to save
     * @return the persisted entity
     */
    public ProductStatusDTO save(ProductStatusDTO productStatusDTO) {
        log.debug("Request to save ProductStatus : {}", productStatusDTO);
        ProductStatus productStatus = productStatusMapper.toEntity(productStatusDTO);
        productStatus = productStatusRepository.save(productStatus);
        ProductStatusDTO result = productStatusMapper.toDto(productStatus);
        productStatusSearchRepository.save(productStatus);
        return result;
    }

    /**
     * Get all the productStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProductStatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductStatuses");
        return productStatusRepository.findAll(pageable)
            .map(productStatusMapper::toDto);
    }

    /**
     * Get one productStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ProductStatusDTO findOne(Long id) {
        log.debug("Request to get ProductStatus : {}", id);
        ProductStatus productStatus = productStatusRepository.findOne(id);
        return productStatusMapper.toDto(productStatus);
    }

    /**
     * Delete the productStatus by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductStatus : {}", id);
        productStatusRepository.delete(id);
        productStatusSearchRepository.delete(id);
    }

    /**
     * Search for the productStatus corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ProductStatusDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProductStatuses for query {}", query);
        Page<ProductStatus> result = productStatusSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(productStatusMapper::toDto);
    }
}
