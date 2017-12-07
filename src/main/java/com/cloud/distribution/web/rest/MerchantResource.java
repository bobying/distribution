package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.MerchantService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.MerchantDTO;
import com.cloud.distribution.service.dto.MerchantCriteria;
import com.cloud.distribution.service.MerchantQueryService;
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
 * REST controller for managing Merchant.
 */
@RestController
@RequestMapping("/api")
public class MerchantResource {

    private final Logger log = LoggerFactory.getLogger(MerchantResource.class);

    private static final String ENTITY_NAME = "merchant";

    private final MerchantService merchantService;

    private final MerchantQueryService merchantQueryService;

    public MerchantResource(MerchantService merchantService, MerchantQueryService merchantQueryService) {
        this.merchantService = merchantService;
        this.merchantQueryService = merchantQueryService;
    }

    /**
     * POST  /merchants : Create a new merchant.
     *
     * @param merchantDTO the merchantDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new merchantDTO, or with status 400 (Bad Request) if the merchant has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/merchants")
    @Timed
    public ResponseEntity<MerchantDTO> createMerchant(@RequestBody MerchantDTO merchantDTO) throws URISyntaxException {
        log.debug("REST request to save Merchant : {}", merchantDTO);
        if (merchantDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MerchantDTO result = merchantService.save(merchantDTO);
        return ResponseEntity.created(new URI("/api/merchants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /merchants : Updates an existing merchant.
     *
     * @param merchantDTO the merchantDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated merchantDTO,
     * or with status 400 (Bad Request) if the merchantDTO is not valid,
     * or with status 500 (Internal Server Error) if the merchantDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/merchants")
    @Timed
    public ResponseEntity<MerchantDTO> updateMerchant(@RequestBody MerchantDTO merchantDTO) throws URISyntaxException {
        log.debug("REST request to update Merchant : {}", merchantDTO);
        if (merchantDTO.getId() == null) {
            return createMerchant(merchantDTO);
        }
        MerchantDTO result = merchantService.save(merchantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, merchantDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /merchants : get all the merchants.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of merchants in body
     */
    @GetMapping("/merchants")
    @Timed
    public ResponseEntity<List<MerchantDTO>> getAllMerchants(MerchantCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Merchants by criteria: {}", criteria);
        Page<MerchantDTO> page = merchantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/merchants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /merchants/:id : get the "id" merchant.
     *
     * @param id the id of the merchantDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the merchantDTO, or with status 404 (Not Found)
     */
    @GetMapping("/merchants/{id}")
    @Timed
    public ResponseEntity<MerchantDTO> getMerchant(@PathVariable Long id) {
        log.debug("REST request to get Merchant : {}", id);
        MerchantDTO merchantDTO = merchantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(merchantDTO));
    }

    /**
     * DELETE  /merchants/:id : delete the "id" merchant.
     *
     * @param id the id of the merchantDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/merchants/{id}")
    @Timed
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        log.debug("REST request to delete Merchant : {}", id);
        merchantService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/merchants?query=:query : search for the merchant corresponding
     * to the query.
     *
     * @param query the query of the merchant search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/merchants")
    @Timed
    public ResponseEntity<List<MerchantDTO>> searchMerchants(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Merchants for query {}", query);
        Page<MerchantDTO> page = merchantService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/merchants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
