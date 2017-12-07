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

import com.cloud.distribution.domain.Status;
import com.cloud.distribution.domain.*; // for static metamodels
import com.cloud.distribution.repository.StatusRepository;
import com.cloud.distribution.repository.search.StatusSearchRepository;
import com.cloud.distribution.service.dto.StatusCriteria;

import com.cloud.distribution.service.dto.StatusDTO;
import com.cloud.distribution.service.mapper.StatusMapper;

/**
 * Service for executing complex queries for Status entities in the database.
 * The main input is a {@link StatusCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StatusDTO} or a {@link Page} of {@link StatusDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StatusQueryService extends QueryService<Status> {

    private final Logger log = LoggerFactory.getLogger(StatusQueryService.class);


    private final StatusRepository statusRepository;

    private final StatusMapper statusMapper;

    private final StatusSearchRepository statusSearchRepository;

    public StatusQueryService(StatusRepository statusRepository, StatusMapper statusMapper, StatusSearchRepository statusSearchRepository) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
        this.statusSearchRepository = statusSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StatusDTO> findByCriteria(StatusCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Status> specification = createSpecification(criteria);
        return statusMapper.toDto(statusRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StatusDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StatusDTO> findByCriteria(StatusCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Status> specification = createSpecification(criteria);
        final Page<Status> result = statusRepository.findAll(specification, page);
        return result.map(statusMapper::toDto);
    }

    /**
     * Function to convert StatusCriteria to a {@link Specifications}
     */
    private Specifications<Status> createSpecification(StatusCriteria criteria) {
        Specifications<Status> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Status_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Status_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Status_.code));
            }
        }
        return specification;
    }

}
