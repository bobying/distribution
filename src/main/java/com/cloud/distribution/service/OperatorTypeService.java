package com.cloud.distribution.service;

import com.cloud.distribution.domain.OperatorType;
import com.cloud.distribution.repository.OperatorTypeRepository;
import com.cloud.distribution.repository.search.OperatorTypeSearchRepository;
import com.cloud.distribution.service.dto.OperatorTypeDTO;
import com.cloud.distribution.service.mapper.OperatorTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing OperatorType.
 */
@Service
@Transactional
public class OperatorTypeService {

    private final Logger log = LoggerFactory.getLogger(OperatorTypeService.class);

    private final OperatorTypeRepository operatorTypeRepository;

    private final OperatorTypeMapper operatorTypeMapper;

    private final OperatorTypeSearchRepository operatorTypeSearchRepository;

    public OperatorTypeService(OperatorTypeRepository operatorTypeRepository, OperatorTypeMapper operatorTypeMapper, OperatorTypeSearchRepository operatorTypeSearchRepository) {
        this.operatorTypeRepository = operatorTypeRepository;
        this.operatorTypeMapper = operatorTypeMapper;
        this.operatorTypeSearchRepository = operatorTypeSearchRepository;
    }

    /**
     * Save a operatorType.
     *
     * @param operatorTypeDTO the entity to save
     * @return the persisted entity
     */
    public OperatorTypeDTO save(OperatorTypeDTO operatorTypeDTO) {
        log.debug("Request to save OperatorType : {}", operatorTypeDTO);
        OperatorType operatorType = operatorTypeMapper.toEntity(operatorTypeDTO);
        operatorType = operatorTypeRepository.save(operatorType);
        OperatorTypeDTO result = operatorTypeMapper.toDto(operatorType);
        operatorTypeSearchRepository.save(operatorType);
        return result;
    }

    /**
     * Get all the operatorTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OperatorTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OperatorTypes");
        return operatorTypeRepository.findAll(pageable)
            .map(operatorTypeMapper::toDto);
    }

    /**
     * Get one operatorType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public OperatorTypeDTO findOne(Long id) {
        log.debug("Request to get OperatorType : {}", id);
        OperatorType operatorType = operatorTypeRepository.findOne(id);
        return operatorTypeMapper.toDto(operatorType);
    }

    /**
     * Delete the operatorType by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete OperatorType : {}", id);
        operatorTypeRepository.delete(id);
        operatorTypeSearchRepository.delete(id);
    }

    /**
     * Search for the operatorType corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OperatorTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OperatorTypes for query {}", query);
        Page<OperatorType> result = operatorTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(operatorTypeMapper::toDto);
    }
}
