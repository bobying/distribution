package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.MerchantStatusService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.MerchantStatusDTO;
import com.cloud.distribution.service.dto.MerchantStatusCriteria;
import com.cloud.distribution.service.MerchantStatusQueryService;
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
 * REST controller for managing MerchantStatus.
 */
@RestController
@RequestMapping("/api")
public class MerchantStatusResource {

    private final Logger log = LoggerFactory.getLogger(MerchantStatusResource.class);

    private static final String ENTITY_NAME = "merchantStatus";

    private final MerchantStatusService merchantStatusService;

    private final MerchantStatusQueryService merchantStatusQueryService;

    public MerchantStatusResource(MerchantStatusService merchantStatusService, MerchantStatusQueryService merchantStatusQueryService) {
        this.merchantStatusService = merchantStatusService;
        this.merchantStatusQueryService = merchantStatusQueryService;
    }

    /**
     * POST  /merchant-statuses : Create a new merchantStatus.
     *
     * @param merchantStatusDTO the merchantStatusDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new merchantStatusDTO, or with status 400 (Bad Request) if the merchantStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/merchant-statuses")
    @Timed
    public ResponseEntity<MerchantStatusDTO> createMerchantStatus(@RequestBody MerchantStatusDTO merchantStatusDTO) throws URISyntaxException {
        log.debug("REST request to save MerchantStatus : {}", merchantStatusDTO);
        if (merchantStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MerchantStatusDTO result = merchantStatusService.save(merchantStatusDTO);
        return ResponseEntity.created(new URI("/api/merchant-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /merchant-statuses : Updates an existing merchantStatus.
     *
     * @param merchantStatusDTO the merchantStatusDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated merchantStatusDTO,
     * or with status 400 (Bad Request) if the merchantStatusDTO is not valid,
     * or with status 500 (Internal Server Error) if the merchantStatusDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/merchant-statuses")
    @Timed
    public ResponseEntity<MerchantStatusDTO> updateMerchantStatus(@RequestBody MerchantStatusDTO merchantStatusDTO) throws URISyntaxException {
        log.debug("REST request to update MerchantStatus : {}", merchantStatusDTO);
        if (merchantStatusDTO.getId() == null) {
            return createMerchantStatus(merchantStatusDTO);
        }
        MerchantStatusDTO result = merchantStatusService.save(merchantStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, merchantStatusDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /merchant-statuses : get all the merchantStatuses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of merchantStatuses in body
     */
    @GetMapping("/merchant-statuses")
    @Timed
    public ResponseEntity<List<MerchantStatusDTO>> getAllMerchantStatuses(MerchantStatusCriteria criteria, Pageable pageable) {
        log.debug("REST request to get MerchantStatuses by criteria: {}", criteria);
        Page<MerchantStatusDTO> page = merchantStatusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/merchant-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /merchant-statuses/:id : get the "id" merchantStatus.
     *
     * @param id the id of the merchantStatusDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the merchantStatusDTO, or with status 404 (Not Found)
     */
    @GetMapping("/merchant-statuses/{id}")
    @Timed
    public ResponseEntity<MerchantStatusDTO> getMerchantStatus(@PathVariable Long id) {
        log.debug("REST request to get MerchantStatus : {}", id);
        MerchantStatusDTO merchantStatusDTO = merchantStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(merchantStatusDTO));
    }

    /**
     * DELETE  /merchant-statuses/:id : delete the "id" merchantStatus.
     *
     * @param id the id of the merchantStatusDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/merchant-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteMerchantStatus(@PathVariable Long id) {
        log.debug("REST request to delete MerchantStatus : {}", id);
        merchantStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/merchant-statuses?query=:query : search for the merchantStatus corresponding
     * to the query.
     *
     * @param query the query of the merchantStatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/merchant-statuses")
    @Timed
    public ResponseEntity<List<MerchantStatusDTO>> searchMerchantStatuses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MerchantStatuses for query {}", query);
        Page<MerchantStatusDTO> page = merchantStatusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/merchant-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
