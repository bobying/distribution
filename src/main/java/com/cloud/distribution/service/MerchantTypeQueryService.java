package com.cloud.distribution.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.cloud.distribution.domain.MerchantType;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.MerchantTypeRepository;
import com.cloud.distribution.repository.search.MerchantTypeSearchRepository;
import com.cloud.distribution.service.dto.MerchantTypeCriteria;

import com.cloud.distribution.service.dto.MerchantTypeDTO;
import com.cloud.distribution.service.mapper.MerchantTypeMapper;

/**
 * Service for executing complex queries for MerchantType entities in the database.
 * The main input is a {@link MerchantTypeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link MerchantTypeDTO} or a {@link Page} of {@link MerchantTypeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MerchantTypeQueryService extends QueryService<MerchantType> {

    private final Logger log = LoggerFactory.getLogger(MerchantTypeQueryService.class);


    private final MerchantTypeRepository merchantTypeRepository;

    private final MerchantTypeMapper merchantTypeMapper;

    private final MerchantTypeSearchRepository merchantTypeSearchRepository;

    public MerchantTypeQueryService(MerchantTypeRepository merchantTypeRepository, MerchantTypeMapper merchantTypeMapper, MerchantTypeSearchRepository merchantTypeSearchRepository) {
        this.merchantTypeRepository = merchantTypeRepository;
        this.merchantTypeMapper = merchantTypeMapper;
        this.merchantTypeSearchRepository = merchantTypeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link MerchantTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<MerchantTypeDTO> findByCriteria(MerchantTypeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<MerchantType> specification = createSpecification(criteria);
        return merchantTypeMapper.toDto(merchantTypeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link MerchantTypeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MerchantTypeDTO> findByCriteria(MerchantTypeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<MerchantType> specification = createSpecification(criteria);
        final Page<MerchantType> result = merchantTypeRepository.findAll(specification, page);
        return result.map(merchantTypeMapper::toDto);
    }

    /**
     * Function to convert MerchantTypeCriteria to a {@link Specifications}
     */
    private Specifications<MerchantType> createSpecification(MerchantTypeCriteria criteria) {
        Specifications<MerchantType> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), MerchantType_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), MerchantType_.name));
            }
        }
        return specification;
    }

}
