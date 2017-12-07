package com.cloud.distribution.service;

import com.cloud.distribution.domain.MerchantAuditStatus;
import com.cloud.distribution.repository.MerchantAuditStatusRepository;
import com.cloud.distribution.repository.search.MerchantAuditStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantAuditStatusDTO;
import com.cloud.distribution.service.mapper.MerchantAuditStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MerchantAuditStatus.
 */
@Service
@Transactional
public class MerchantAuditStatusService {

    private final Logger log = LoggerFactory.getLogger(MerchantAuditStatusService.class);

    private final MerchantAuditStatusRepository merchantAuditStatusRepository;

    private final MerchantAuditStatusMapper merchantAuditStatusMapper;

    private final MerchantAuditStatusSearchRepository merchantAuditStatusSearchRepository;

    public MerchantAuditStatusService(MerchantAuditStatusRepository merchantAuditStatusRepository, MerchantAuditStatusMapper merchantAuditStatusMapper, MerchantAuditStatusSearchRepository merchantAuditStatusSearchRepository) {
        this.merchantAuditStatusRepository = merchantAuditStatusRepository;
        this.merchantAuditStatusMapper = merchantAuditStatusMapper;
        this.merchantAuditStatusSearchRepository = merchantAuditStatusSearchRepository;
    }

    /**
     * Save a merchantAuditStatus.
     *
     * @param merchantAuditStatusDTO the entity to save
     * @return the persisted entity
     */
    public MerchantAuditStatusDTO save(MerchantAuditStatusDTO merchantAuditStatusDTO) {
        log.debug("Request to save MerchantAuditStatus : {}", merchantAuditStatusDTO);
        MerchantAuditStatus merchantAuditStatus = merchantAuditStatusMapper.toEntity(merchantAuditStatusDTO);
        merchantAuditStatus = merchantAuditStatusRepository.save(merchantAuditStatus);
        MerchantAuditStatusDTO result = merchantAuditStatusMapper.toDto(merchantAuditStatus);
        merchantAuditStatusSearchRepository.save(merchantAuditStatus);
        return result;
    }

    /**
     * Get all the merchantAuditStatuses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantAuditStatusDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MerchantAuditStatuses");
        return merchantAuditStatusRepository.findAll(pageable)
            .map(merchantAuditStatusMapper::toDto);
    }

    /**
     * Get one merchantAuditStatus by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MerchantAuditStatusDTO findOne(Long id) {
        log.debug("Request to get MerchantAuditStatus : {}", id);
        MerchantAuditStatus merchantAuditStatus = merchantAuditStatusRepository.findOne(id);
        return merchantAuditStatusMapper.toDto(merchantAuditStatus);
    }

    /**
     * Delete the merchantAuditStatus by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MerchantAuditStatus : {}", id);
        merchantAuditStatusRepository.delete(id);
        merchantAuditStatusSearchRepository.delete(id);
    }

    /**
     * Search for the merchantAuditStatus corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantAuditStatusDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MerchantAuditStatuses for query {}", query);
        Page<MerchantAuditStatus> result = merchantAuditStatusSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(merchantAuditStatusMapper::toDto);
    }
}
