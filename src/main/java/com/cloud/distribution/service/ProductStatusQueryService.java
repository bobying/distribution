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

import com.cloud.distribution.domain.ProductStatus;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.ProductStatusRepository;
import com.cloud.distribution.repository.search.ProductStatusSearchRepository;
import com.cloud.distribution.service.dto.ProductStatusCriteria;

import com.cloud.distribution.service.dto.ProductStatusDTO;
import com.cloud.distribution.service.mapper.ProductStatusMapper;

/**
 * Service for executing complex queries for ProductStatus entities in the database.
 * The main input is a {@link ProductStatusCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductStatusDTO} or a {@link Page} of {@link ProductStatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductStatusQueryService extends QueryService<ProductStatus> {

    private final Logger log = LoggerFactory.getLogger(ProductStatusQueryService.class);


    private final ProductStatusRepository productStatusRepository;

    private final ProductStatusMapper productStatusMapper;

    private final ProductStatusSearchRepository productStatusSearchRepository;

    public ProductStatusQueryService(ProductStatusRepository productStatusRepository, ProductStatusMapper productStatusMapper, ProductStatusSearchRepository productStatusSearchRepository) {
        this.productStatusRepository = productStatusRepository;
        this.productStatusMapper = productStatusMapper;
        this.productStatusSearchRepository = productStatusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ProductStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductStatusDTO> findByCriteria(ProductStatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<ProductStatus> specification = createSpecification(criteria);
        return productStatusMapper.toDto(productStatusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductStatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductStatusDTO> findByCriteria(ProductStatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ProductStatus> specification = createSpecification(criteria);
        final Page<ProductStatus> result = productStatusRepository.findAll(specification, page);
        return result.map(productStatusMapper::toDto);
    }

    /**
     * Function to convert ProductStatusCriteria to a {@link Specifications}
     */
    private Specifications<ProductStatus> createSpecification(ProductStatusCriteria criteria) {
        Specifications<ProductStatus> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ProductStatus_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), ProductStatus_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), ProductStatus_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), ProductStatus_.status, Status_.id));
            }
        }
        return specification;
    }

}
