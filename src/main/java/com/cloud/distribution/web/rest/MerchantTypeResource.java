package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.MerchantTypeService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.MerchantTypeDTO;
import com.cloud.distribution.service.dto.MerchantTypeCriteria;
import com.cloud.distribution.service.MerchantTypeQueryService;
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
 * REST controller for managing MerchantType.
 */
@RestController
@RequestMapping("/api")
public class MerchantTypeResource {

    private final Logger log = LoggerFactory.getLogger(MerchantTypeResource.class);

    private static final String ENTITY_NAME = "merchantType";

    private final MerchantTypeService merchantTypeService;

    private final MerchantTypeQueryService merchantTypeQueryService;

    public MerchantTypeResource(MerchantTypeService merchantTypeService, MerchantTypeQueryService merchantTypeQueryService) {
        this.merchantTypeService = merchantTypeService;
        this.merchantTypeQueryService = merchantTypeQueryService;
    }

    /**
     * POST  /merchant-types : Create a new merchantType.
     *
     * @param merchantTypeDTO the merchantTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new merchantTypeDTO, or with status 400 (Bad Request) if the merchantType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/merchant-types")
    @Timed
    public ResponseEntity<MerchantTypeDTO> createMerchantType(@RequestBody MerchantTypeDTO merchantTypeDTO) throws URISyntaxException {
        log.debug("REST request to save MerchantType : {}", merchantTypeDTO);
        if (merchantTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MerchantTypeDTO result = merchantTypeService.save(merchantTypeDTO);
        return ResponseEntity.created(new URI("/api/merchant-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /merchant-types : Updates an existing merchantType.
     *
     * @param merchantTypeDTO the merchantTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated merchantTypeDTO,
     * or with status 400 (Bad Request) if the merchantTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the merchantTypeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/merchant-types")
    @Timed
    public ResponseEntity<MerchantTypeDTO> updateMerchantType(@RequestBody MerchantTypeDTO merchantTypeDTO) throws URISyntaxException {
        log.debug("REST request to update MerchantType : {}", merchantTypeDTO);
        if (merchantTypeDTO.getId() == null) {
            return createMerchantType(merchantTypeDTO);
        }
        MerchantTypeDTO result = merchantTypeService.save(merchantTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, merchantTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /merchant-types : get all the merchantTypes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of merchantTypes in body
     */
    @GetMapping("/merchant-types")
    @Timed
    public ResponseEntity<List<MerchantTypeDTO>> getAllMerchantTypes(MerchantTypeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get MerchantTypes by criteria: {}", criteria);
        Page<MerchantTypeDTO> page = merchantTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/merchant-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /merchant-types/:id : get the "id" merchantType.
     *
     * @param id the id of the merchantTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the merchantTypeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/merchant-types/{id}")
    @Timed
    public ResponseEntity<MerchantTypeDTO> getMerchantType(@PathVariable Long id) {
        log.debug("REST request to get MerchantType : {}", id);
        MerchantTypeDTO merchantTypeDTO = merchantTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(merchantTypeDTO));
    }

    /**
     * DELETE  /merchant-types/:id : delete the "id" merchantType.
     *
     * @param id the id of the merchantTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/merchant-types/{id}")
    @Timed
    public ResponseEntity<Void> deleteMerchantType(@PathVariable Long id) {
        log.debug("REST request to delete MerchantType : {}", id);
        merchantTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/merchant-types?query=:query : search for the merchantType corresponding
     * to the query.
     *
     * @param query the query of the merchantType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/merchant-types")
    @Timed
    public ResponseEntity<List<MerchantTypeDTO>> searchMerchantTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MerchantTypes for query {}", query);
        Page<MerchantTypeDTO> page = merchantTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/merchant-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
