package com.cloud.distribution.service;

import com.cloud.distribution.domain.MerchantType;
import com.cloud.distribution.repository.MerchantTypeRepository;
import com.cloud.distribution.repository.search.MerchantTypeSearchRepository;
import com.cloud.distribution.service.dto.MerchantTypeDTO;
import com.cloud.distribution.service.mapper.MerchantTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing MerchantType.
 */
@Service
@Transactional
public class MerchantTypeService {

    private final Logger log = LoggerFactory.getLogger(MerchantTypeService.class);

    private final MerchantTypeRepository merchantTypeRepository;

    private final MerchantTypeMapper merchantTypeMapper;

    private final MerchantTypeSearchRepository merchantTypeSearchRepository;

    public MerchantTypeService(MerchantTypeRepository merchantTypeRepository, MerchantTypeMapper merchantTypeMapper, MerchantTypeSearchRepository merchantTypeSearchRepository) {
        this.merchantTypeRepository = merchantTypeRepository;
        this.merchantTypeMapper = merchantTypeMapper;
        this.merchantTypeSearchRepository = merchantTypeSearchRepository;
    }

    /**
     * Save a merchantType.
     *
     * @param merchantTypeDTO the entity to save
     * @return the persisted entity
     */
    public MerchantTypeDTO save(MerchantTypeDTO merchantTypeDTO) {
        log.debug("Request to save MerchantType : {}", merchantTypeDTO);
        MerchantType merchantType = merchantTypeMapper.toEntity(merchantTypeDTO);
        merchantType = merchantTypeRepository.save(merchantType);
        MerchantTypeDTO result = merchantTypeMapper.toDto(merchantType);
        merchantTypeSearchRepository.save(merchantType);
        return result;
    }

    /**
     * Get all the merchantTypes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantTypeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MerchantTypes");
        return merchantTypeRepository.findAll(pageable)
            .map(merchantTypeMapper::toDto);
    }

    /**
     * Get one merchantType by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MerchantTypeDTO findOne(Long id) {
        log.debug("Request to get MerchantType : {}", id);
        MerchantType merchantType = merchantTypeRepository.findOne(id);
        return merchantTypeMapper.toDto(merchantType);
    }

    /**
     * Delete the merchantType by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MerchantType : {}", id);
        merchantTypeRepository.delete(id);
        merchantTypeSearchRepository.delete(id);
    }

    /**
     * Search for the merchantType corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantTypeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MerchantTypes for query {}", query);
        Page<MerchantType> result = merchantTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(merchantTypeMapper::toDto);
    }
}
