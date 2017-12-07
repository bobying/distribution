package com.cloud.distribution.service;

import com.cloud.distribution.domain.PayType;
import com.cloud.distribution.repository.PayTypeRepository;
import com.cloud.distribution.repository.search.PayTypeSearchRepository;
import com.cloud.distribution.service.dto.PayTypeDTO;
import com.cloud.distribution.service.mapper.PayTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PayType.
 */
@Service
@Transactional
public class PayTypeService {

    private final Logger log = LoggerFactory.getLogger(PayTypeService.class);

    private final PayTypeRepository payTypeRepository;

    private final PayTypeMapper payTypeMapper;

    private final PayTypeSearchRepository payTypeSearchRepository;

    public PayTypeService(PayTypeRepository payTypeRepository, PayTypeMapper payTypeMapper, PayTypeSearchRepository payTypeSearchRepository) {
        this.payTypeRepository = payTypeRepository;
        this.payTypeMapper = payTypeMapper;
        this.payTypeSearchRepository = payTypeSearchRepository;
    }

    /**
     * Save a payType.
     *
     * @param payTypeDTO the entity to save
     * @return the persisted entity
     */
    public PayTypeDTO save(PayTypeDTO payTypeDTO) {
        log.debug("Request to save PayType : {}", payTypeDTO);
        PayType payType = payTypeMapper.toEntity(payTypeDTO);
        payType = payTypeRepository.save(payType);
        PayTypeDTO result = payTypeMapper.toDto(payType);
        payTypeSearchRepository.save(payType);
        return result;
    }

    /**
     * Get all the payTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PayTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PayTypes");
        return payTypeRepository.findAll(pageable)
            .map(payTypeMapper::toDto);
    }

    /**
     * Get one payType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public PayTypeDTO findOne(Long id) {
        log.debug("Request to get PayType : {}", id);
        PayType payType = payTypeRepository.findOne(id);
        return payTypeMapper.toDto(payType);
    }

    /**
     * Delete the payType by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete PayType : {}", id);
        payTypeRepository.delete(id);
        payTypeSearchRepository.delete(id);
    }

    /**
     * Search for the payType corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<PayTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PayTypes for query {}", query);
        Page<PayType> result = payTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(payTypeMapper::toDto);
    }
}
