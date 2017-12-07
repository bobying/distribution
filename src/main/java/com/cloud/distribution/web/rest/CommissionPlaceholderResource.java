package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.CommissionPlaceholderService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.CommissionPlaceholderDTO;
import com.cloud.distribution.service.dto.CommissionPlaceholderCriteria;
import com.cloud.distribution.service.CommissionPlaceholderQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing CommissionPlaceholder.
 */
@RestController
@RequestMapping("/api")
public class CommissionPlaceholderResource {

    private final Logger log = LoggerFactory.getLogger(CommissionPlaceholderResource.class);

    private static final String ENTITY_NAME = "commissionPlaceholder";

    private final CommissionPlaceholderService commissionPlaceholderService;

    private final CommissionPlaceholderQueryService commissionPlaceholderQueryService;

    public CommissionPlaceholderResource(CommissionPlaceholderService commissionPlaceholderService, CommissionPlaceholderQueryService commissionPlaceholderQueryService) {
        this.commissionPlaceholderService = commissionPlaceholderService;
        this.commissionPlaceholderQueryService = commissionPlaceholderQueryService;
    }

    /**
     * POST  /commission-placeholders : Create a new commissionPlaceholder.
     *
     * @param commissionPlaceholderDTO the commissionPlaceholderDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new commissionPlaceholderDTO, or with status 400 (Bad Request) if the commissionPlaceholder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/commission-placeholders")
    @Timed
    public ResponseEntity<CommissionPlaceholderDTO> createCommissionPlaceholder(@RequestBody CommissionPlaceholderDTO commissionPlaceholderDTO) throws URISyntaxException {
        log.debug("REST request to save CommissionPlaceholder : {}", commissionPlaceholderDTO);
        if (commissionPlaceholderDTO.getId() != null) {
            throw new BadRequestAlertException("A new commissionPlaceholder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CommissionPlaceholderDTO result = commissionPlaceholderService.save(commissionPlaceholderDTO);
        return ResponseEntity.created(new URI("/api/commission-placeholders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /commission-placeholders : Updates an existing commissionPlaceholder.
     *
     * @param commissionPlaceholderDTO the commissionPlaceholderDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated commissionPlaceholderDTO,
     * or with status 400 (Bad Request) if the commissionPlaceholderDTO is not valid,
     * or with status 500 (Internal Server Error) if the commissionPlaceholderDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/commission-placeholders")
    @Timed
    public ResponseEntity<CommissionPlaceholderDTO> updateCommissionPlaceholder(@RequestBody CommissionPlaceholderDTO commissionPlaceholderDTO) throws URISyntaxException {
        log.debug("REST request to update CommissionPlaceholder : {}", commissionPlaceholderDTO);
        if (commissionPlaceholderDTO.getId() == null) {
            return createCommissionPlaceholder(commissionPlaceholderDTO);
        }
        CommissionPlaceholderDTO result = commissionPlaceholderService.save(commissionPlaceholderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, commissionPlaceholderDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /commission-placeholders : get all the commissionPlaceholders.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of commissionPlaceholders in body
     */
    @GetMapping("/commission-placeholders")
    @Timed
    public ResponseEntity<List<CommissionPlaceholderDTO>> getAllCommissionPlaceholders(CommissionPlaceholderCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CommissionPlaceholders by criteria: {}", criteria);
        Page<CommissionPlaceholderDTO> page = commissionPlaceholderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/commission-placeholders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /commission-placeholders/:id : get the "id" commissionPlaceholder.
     *
     * @param id the id of the commissionPlaceholderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the commissionPlaceholderDTO, or with status 404 (Not Found)
     */
    @GetMapping("/commission-placeholders/{id}")
    @Timed
    public ResponseEntity<CommissionPlaceholderDTO> getCommissionPlaceholder(@PathVariable Long id) {
        log.debug("REST request to get CommissionPlaceholder : {}", id);
        CommissionPlaceholderDTO commissionPlaceholderDTO = commissionPlaceholderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(commissionPlaceholderDTO));
    }

    /**
     * DELETE  /commission-placeholders/:id : delete the "id" commissionPlaceholder.
     *
     * @param id the id of the commissionPlaceholderDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/commission-placeholders/{id}")
    @Timed
    public ResponseEntity<Void> deleteCommissionPlaceholder(@PathVariable Long id) {
        log.debug("REST request to delete CommissionPlaceholder : {}", id);
        commissionPlaceholderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/commission-placeholders?query=:query : search for the commissionPlaceholder corresponding
     * to the query.
     *
     * @param query the query of the commissionPlaceholder search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/commission-placeholders")
    @Timed
    public ResponseEntity<List<CommissionPlaceholderDTO>> searchCommissionPlaceholders(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of CommissionPlaceholders for query {}", query);
        Page<CommissionPlaceholderDTO> page = commissionPlaceholderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/commission-placeholders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
