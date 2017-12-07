package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.MerchantAuditStatusService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.MerchantAuditStatusDTO;
import com.cloud.distribution.service.dto.MerchantAuditStatusCriteria;
import com.cloud.distribution.service.MerchantAuditStatusQueryService;
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
 * REST controller for managing MerchantAuditStatus.
 */
@RestController
@RequestMapping("/api")
public class MerchantAuditStatusResource {

    private final Logger log = LoggerFactory.getLogger(MerchantAuditStatusResource.class);

    private static final String ENTITY_NAME = "merchantAuditStatus";

    private final MerchantAuditStatusService merchantAuditStatusService;

    private final MerchantAuditStatusQueryService merchantAuditStatusQueryService;

    public MerchantAuditStatusResource(MerchantAuditStatusService merchantAuditStatusService, MerchantAuditStatusQueryService merchantAuditStatusQueryService) {
        this.merchantAuditStatusService = merchantAuditStatusService;
        this.merchantAuditStatusQueryService = merchantAuditStatusQueryService;
    }

    /**
     * POST  /merchant-audit-statuses : Create a new merchantAuditStatus.
     *
     * @param merchantAuditStatusDTO the merchantAuditStatusDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new merchantAuditStatusDTO, or with status 400 (Bad Request) if the merchantAuditStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/merchant-audit-statuses")
    @Timed
    public ResponseEntity<MerchantAuditStatusDTO> createMerchantAuditStatus(@RequestBody MerchantAuditStatusDTO merchantAuditStatusDTO) throws URISyntaxException {
        log.debug("REST request to save MerchantAuditStatus : {}", merchantAuditStatusDTO);
        if (merchantAuditStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantAuditStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MerchantAuditStatusDTO result = merchantAuditStatusService.save(merchantAuditStatusDTO);
        return ResponseEntity.created(new URI("/api/merchant-audit-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /merchant-audit-statuses : Updates an existing merchantAuditStatus.
     *
     * @param merchantAuditStatusDTO the merchantAuditStatusDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated merchantAuditStatusDTO,
     * or with status 400 (Bad Request) if the merchantAuditStatusDTO is not valid,
     * or with status 500 (Internal Server Error) if the merchantAuditStatusDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/merchant-audit-statuses")
    @Timed
    public ResponseEntity<MerchantAuditStatusDTO> updateMerchantAuditStatus(@RequestBody MerchantAuditStatusDTO merchantAuditStatusDTO) throws URISyntaxException {
        log.debug("REST request to update MerchantAuditStatus : {}", merchantAuditStatusDTO);
        if (merchantAuditStatusDTO.getId() == null) {
            return createMerchantAuditStatus(merchantAuditStatusDTO);
        }
        MerchantAuditStatusDTO result = merchantAuditStatusService.save(merchantAuditStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, merchantAuditStatusDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /merchant-audit-statuses : get all the merchantAuditStatuses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of merchantAuditStatuses in body
     */
    @GetMapping("/merchant-audit-statuses")
    @Timed
    public ResponseEntity<List<MerchantAuditStatusDTO>> getAllMerchantAuditStatuses(MerchantAuditStatusCriteria criteria, Pageable pageable) {
        log.debug("REST request to get MerchantAuditStatuses by criteria: {}", criteria);
        Page<MerchantAuditStatusDTO> page = merchantAuditStatusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/merchant-audit-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /merchant-audit-statuses/:id : get the "id" merchantAuditStatus.
     *
     * @param id the id of the merchantAuditStatusDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the merchantAuditStatusDTO, or with status 404 (Not Found)
     */
    @GetMapping("/merchant-audit-statuses/{id}")
    @Timed
    public ResponseEntity<MerchantAuditStatusDTO> getMerchantAuditStatus(@PathVariable Long id) {
        log.debug("REST request to get MerchantAuditStatus : {}", id);
        MerchantAuditStatusDTO merchantAuditStatusDTO = merchantAuditStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(merchantAuditStatusDTO));
    }

    /**
     * DELETE  /merchant-audit-statuses/:id : delete the "id" merchantAuditStatus.
     *
     * @param id the id of the merchantAuditStatusDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/merchant-audit-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteMerchantAuditStatus(@PathVariable Long id) {
        log.debug("REST request to delete MerchantAuditStatus : {}", id);
        merchantAuditStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/merchant-audit-statuses?query=:query : search for the merchantAuditStatus corresponding
     * to the query.
     *
     * @param query the query of the merchantAuditStatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/merchant-audit-statuses")
    @Timed
    public ResponseEntity<List<MerchantAuditStatusDTO>> searchMerchantAuditStatuses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MerchantAuditStatuses for query {}", query);
        Page<MerchantAuditStatusDTO> page = merchantAuditStatusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/merchant-audit-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
