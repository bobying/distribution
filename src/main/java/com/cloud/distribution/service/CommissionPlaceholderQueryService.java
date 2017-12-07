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

import com.cloud.distribution.domain.CommissionPlaceholder;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.CommissionPlaceholderRepository;
import com.cloud.distribution.repository.search.CommissionPlaceholderSearchRepository;
import com.cloud.distribution.service.dto.CommissionPlaceholderCriteria;

import com.cloud.distribution.service.dto.CommissionPlaceholderDTO;
import com.cloud.distribution.service.mapper.CommissionPlaceholderMapper;

/**
 * Service for executing complex queries for CommissionPlaceholder entities in the database.
 * The main input is a {@link CommissionPlaceholderCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommissionPlaceholderDTO} or a {@link Page} of {@link CommissionPlaceholderDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommissionPlaceholderQueryService extends QueryService<CommissionPlaceholder> {

    private final Logger log = LoggerFactory.getLogger(CommissionPlaceholderQueryService.class);


    private final CommissionPlaceholderRepository commissionPlaceholderRepository;

    private final CommissionPlaceholderMapper commissionPlaceholderMapper;

    private final CommissionPlaceholderSearchRepository commissionPlaceholderSearchRepository;

    public CommissionPlaceholderQueryService(CommissionPlaceholderRepository commissionPlaceholderRepository, CommissionPlaceholderMapper commissionPlaceholderMapper, CommissionPlaceholderSearchRepository commissionPlaceholderSearchRepository) {
        this.commissionPlaceholderRepository = commissionPlaceholderRepository;
        this.commissionPlaceholderMapper = commissionPlaceholderMapper;
        this.commissionPlaceholderSearchRepository = commissionPlaceholderSearchRepository;
    }

    /**
     * Return a {@link List} of {@link CommissionPlaceholderDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommissionPlaceholderDTO> findByCriteria(CommissionPlaceholderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<CommissionPlaceholder> specification = createSpecification(criteria);
        return commissionPlaceholderMapper.toDto(commissionPlaceholderRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommissionPlaceholderDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommissionPlaceholderDTO> findByCriteria(CommissionPlaceholderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<CommissionPlaceholder> specification = createSpecification(criteria);
        final Page<CommissionPlaceholder> result = commissionPlaceholderRepository.findAll(specification, page);
        return result.map(commissionPlaceholderMapper::toDto);
    }

    /**
     * Function to convert CommissionPlaceholderCriteria to a {@link Specifications}
     */
    private Specifications<CommissionPlaceholder> createSpecification(CommissionPlaceholderCriteria criteria) {
        Specifications<CommissionPlaceholder> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), CommissionPlaceholder_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CommissionPlaceholder_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), CommissionPlaceholder_.code));
            }
            if (criteria.getStatusId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getStatusId(), CommissionPlaceholder_.status, Status_.id));
            }
        }
        return specification;
    }

}
