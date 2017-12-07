package com.cloud.distribution.service;

import com.cloud.distribution.domain.MerchantStatus;
import com.cloud.distribution.repository.MerchantStatusRepository;
import com.cloud.distribution.repository.search.MerchantStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantStatusDTO;
import com.cloud.distribution.service.mapper.MerchantStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MerchantStatus.
 */
@Service
@Transactional
public class MerchantStatusService {

    private final Logger log = LoggerFactory.getLogger(MerchantStatusService.class);

    private final MerchantStatusRepository merchantStatusRepository;

    private final MerchantStatusMapper merchantStatusMapper;

    private final MerchantStatusSearchRepository merchantStatusSearchRepository;

    public MerchantStatusService(MerchantStatusRepository merchantStatusRepository, MerchantStatusMapper merchantStatusMapper, MerchantStatusSearchRepository merchantStatusSearchRepository) {
        this.merchantStatusRepository = merchantStatusRepository;
        this.merchantStatusMapper = merchantStatusMapper;
        this.merchantStatusSearchRepository = merchantStatusSearchRepository;
    }

    /**
     * Save a merchantStatus.
     *
     * @param merchantStatusDTO the entity to save
     * @return the persisted entity
     */
    public MerchantStatusDTO save(MerchantStatusDTO merchantStatusDTO) {
        log.debug("Request to save MerchantStatus : {}", merchantStatusDTO);
        MerchantStatus merchantStatus = merchantStatusMapper.toEntity(merchantStatusDTO);
        merchantStatus = merchantStatusRepository.save(merchantStatus);
        MerchantStatusDTO result = merchantStatusMapper.toDto(merchantStatus);
        merchantStatusSearchRepository.save(merchantStatus);
        return result;
    }

    /**
     * Get all the merchantStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantStatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MerchantStatuses");
        return merchantStatusRepository.findAll(pageable)
            .map(merchantStatusMapper::toDto);
    }

    /**
     * Get one merchantStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MerchantStatusDTO findOne(Long id) {
        log.debug("Request to get MerchantStatus : {}", id);
        MerchantStatus merchantStatus = merchantStatusRepository.findOne(id);
        return merchantStatusMapper.toDto(merchantStatus);
    }

    /**
     * Delete the merchantStatus by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MerchantStatus : {}", id);
        merchantStatusRepository.delete(id);
        merchantStatusSearchRepository.delete(id);
    }

    /**
     * Search for the merchantStatus corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantStatusDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MerchantStatuses for query {}", query);
        Page<MerchantStatus> result = merchantStatusSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(merchantStatusMapper::toDto);
    }
}
