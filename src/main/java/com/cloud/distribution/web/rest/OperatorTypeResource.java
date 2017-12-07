package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.OperatorTypeService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.OperatorTypeDTO;
import com.cloud.distribution.service.dto.OperatorTypeCriteria;
import com.cloud.distribution.service.OperatorTypeQueryService;
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
 * REST controller for managing OperatorType.
 */
@RestController
@RequestMapping("/api")
public class OperatorTypeResource {

    private final Logger log = LoggerFactory.getLogger(OperatorTypeResource.class);

    private static final String ENTITY_NAME = "operatorType";

    private final OperatorTypeService operatorTypeService;

    private final OperatorTypeQueryService operatorTypeQueryService;

    public OperatorTypeResource(OperatorTypeService operatorTypeService, OperatorTypeQueryService operatorTypeQueryService) {
        this.operatorTypeService = operatorTypeService;
        this.operatorTypeQueryService = operatorTypeQueryService;
    }

    /**
     * POST  /operator-types : Create a new operatorType.
     *
     * @param operatorTypeDTO the operatorTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new operatorTypeDTO, or with status 400 (Bad Request) if the operatorType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/operator-types")
    @Timed
    public ResponseEntity<OperatorTypeDTO> createOperatorType(@RequestBody OperatorTypeDTO operatorTypeDTO) throws URISyntaxException {
        log.debug("REST request to save OperatorType : {}", operatorTypeDTO);
        if (operatorTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new operatorType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OperatorTypeDTO result = operatorTypeService.save(operatorTypeDTO);
        return ResponseEntity.created(new URI("/api/operator-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /operator-types : Updates an existing operatorType.
     *
     * @param operatorTypeDTO the operatorTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated operatorTypeDTO,
     * or with status 400 (Bad Request) if the operatorTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the operatorTypeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/operator-types")
    @Timed
    public ResponseEntity<OperatorTypeDTO> updateOperatorType(@RequestBody OperatorTypeDTO operatorTypeDTO) throws URISyntaxException {
        log.debug("REST request to update OperatorType : {}", operatorTypeDTO);
        if (operatorTypeDTO.getId() == null) {
            return createOperatorType(operatorTypeDTO);
        }
        OperatorTypeDTO result = operatorTypeService.save(operatorTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, operatorTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /operator-types : get all the operatorTypes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of operatorTypes in body
     */
    @GetMapping("/operator-types")
    @Timed
    public ResponseEntity<List<OperatorTypeDTO>> getAllOperatorTypes(OperatorTypeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OperatorTypes by criteria: {}", criteria);
        Page<OperatorTypeDTO> page = operatorTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/operator-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /operator-types/:id : get the "id" operatorType.
     *
     * @param id the id of the operatorTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the operatorTypeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/operator-types/{id}")
    @Timed
    public ResponseEntity<OperatorTypeDTO> getOperatorType(@PathVariable Long id) {
        log.debug("REST request to get OperatorType : {}", id);
        OperatorTypeDTO operatorTypeDTO = operatorTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(operatorTypeDTO));
    }

    /**
     * DELETE  /operator-types/:id : delete the "id" operatorType.
     *
     * @param id the id of the operatorTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/operator-types/{id}")
    @Timed
    public ResponseEntity<Void> deleteOperatorType(@PathVariable Long id) {
        log.debug("REST request to delete OperatorType : {}", id);
        operatorTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/operator-types?query=:query : search for the operatorType corresponding
     * to the query.
     *
     * @param query the query of the operatorType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/operator-types")
    @Timed
    public ResponseEntity<List<OperatorTypeDTO>> searchOperatorTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of OperatorTypes for query {}", query);
        Page<OperatorTypeDTO> page = operatorTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/operator-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
