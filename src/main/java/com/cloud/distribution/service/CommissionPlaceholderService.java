package com.cloud.distribution.service;

import com.cloud.distribution.domain.CommissionPlaceholder;
import com.cloud.distribution.repository.CommissionPlaceholderRepository;
import com.cloud.distribution.repository.search.CommissionPlaceholderSearchRepository;
import com.cloud.distribution.service.dto.CommissionPlaceholderDTO;
import com.cloud.distribution.service.mapper.CommissionPlaceholderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing CommissionPlaceholder.
 */
@Service
@Transactional
public class CommissionPlaceholderService {

    private final Logger log = LoggerFactory.getLogger(CommissionPlaceholderService.class);

    private final CommissionPlaceholderRepository commissionPlaceholderRepository;

    private final CommissionPlaceholderMapper commissionPlaceholderMapper;

    private final CommissionPlaceholderSearchRepository commissionPlaceholderSearchRepository;

    public CommissionPlaceholderService(CommissionPlaceholderRepository commissionPlaceholderRepository, CommissionPlaceholderMapper commissionPlaceholderMapper, CommissionPlaceholderSearchRepository commissionPlaceholderSearchRepository) {
        this.commissionPlaceholderRepository = commissionPlaceholderRepository;
        this.commissionPlaceholderMapper = commissionPlaceholderMapper;
        this.commissionPlaceholderSearchRepository = commissionPlaceholderSearchRepository;
    }

    /**
     * Save a commissionPlaceholder.
     *
     * @param commissionPlaceholderDTO the entity to save
     * @return the persisted entity
     */
    public CommissionPlaceholderDTO save(CommissionPlaceholderDTO commissionPlaceholderDTO) {
        log.debug("Request to save CommissionPlaceholder : {}", commissionPlaceholderDTO);
        CommissionPlaceholder commissionPlaceholder = commissionPlaceholderMapper.toEntity(commissionPlaceholderDTO);
        commissionPlaceholder = commissionPlaceholderRepository.save(commissionPlaceholder);
        CommissionPlaceholderDTO result = commissionPlaceholderMapper.toDto(commissionPlaceholder);
        commissionPlaceholderSearchRepository.save(commissionPlaceholder);
        return result;
    }

    /**
     * Get all the commissionPlaceholders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CommissionPlaceholderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CommissionPlaceholders");
        return commissionPlaceholderRepository.findAll(pageable)
            .map(commissionPlaceholderMapper::toDto);
    }

    /**
     * Get one commissionPlaceholder by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public CommissionPlaceholderDTO findOne(Long id) {
        log.debug("Request to get CommissionPlaceholder : {}", id);
        CommissionPlaceholder commissionPlaceholder = commissionPlaceholderRepository.findOne(id);
        return commissionPlaceholderMapper.toDto(commissionPlaceholder);
    }

    /**
     * Delete the commissionPlaceholder by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete CommissionPlaceholder : {}", id);
        commissionPlaceholderRepository.delete(id);
        commissionPlaceholderSearchRepository.delete(id);
    }

    /**
     * Search for the commissionPlaceholder corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<CommissionPlaceholderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CommissionPlaceholders for query {}", query);
        Page<CommissionPlaceholder> result = commissionPlaceholderSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(commissionPlaceholderMapper::toDto);
    }
}
