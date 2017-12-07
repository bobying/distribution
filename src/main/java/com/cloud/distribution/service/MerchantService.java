package com.cloud.distribution.service;

import com.cloud.distribution.domain.Merchant;
import com.cloud.distribution.repository.MerchantRepository;
import com.cloud.distribution.repository.search.MerchantSearchRepository;
import com.cloud.distribution.service.dto.MerchantDTO;
import com.cloud.distribution.service.mapper.MerchantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Merchant.
 */
@Service
@Transactional
public class MerchantService {

    private final Logger log = LoggerFactory.getLogger(MerchantService.class);

    private final MerchantRepository merchantRepository;

    private final MerchantMapper merchantMapper;

    private final MerchantSearchRepository merchantSearchRepository;

    public MerchantService(MerchantRepository merchantRepository, MerchantMapper merchantMapper, MerchantSearchRepository merchantSearchRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantMapper = merchantMapper;
        this.merchantSearchRepository = merchantSearchRepository;
    }

    /**
     * Save a merchant.
     *
     * @param merchantDTO the entity to save
     * @return the persisted entity
     */
    public MerchantDTO save(MerchantDTO merchantDTO) {
        log.debug("Request to save Merchant : {}", merchantDTO);
        Merchant merchant = merchantMapper.toEntity(merchantDTO);
        merchant = merchantRepository.save(merchant);
        MerchantDTO result = merchantMapper.toDto(merchant);
        merchantSearchRepository.save(merchant);
        return result;
    }

    /**
     * Get all the merchants.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Merchants");
        return merchantRepository.findAll(pageable)
            .map(merchantMapper::toDto);
    }

    /**
     * Get one merchant by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public MerchantDTO findOne(Long id) {
        log.debug("Request to get Merchant : {}", id);
        Merchant merchant = merchantRepository.findOne(id);
        return merchantMapper.toDto(merchant);
    }

    /**
     * Delete the merchant by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Merchant : {}", id);
        merchantRepository.delete(id);
        merchantSearchRepository.delete(id);
    }

    /**
     * Search for the merchant corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MerchantDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Merchants for query {}", query);
        Page<Merchant> result = merchantSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(merchantMapper::toDto);
    }
}
