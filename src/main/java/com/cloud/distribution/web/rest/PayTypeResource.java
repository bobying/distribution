package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.PayTypeService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.PayTypeDTO;
import com.cloud.distribution.service.dto.PayTypeCriteria;
import com.cloud.distribution.service.PayTypeQueryService;
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
 * REST controller for managing PayType.
 */
@RestController
@RequestMapping("/api")
public class PayTypeResource {

    private final Logger log = LoggerFactory.getLogger(PayTypeResource.class);

    private static final String ENTITY_NAME = "payType";

    private final PayTypeService payTypeService;

    private final PayTypeQueryService payTypeQueryService;

    public PayTypeResource(PayTypeService payTypeService, PayTypeQueryService payTypeQueryService) {
        this.payTypeService = payTypeService;
        this.payTypeQueryService = payTypeQueryService;
    }

    /**
     * POST  /pay-types : Create a new payType.
     *
     * @param payTypeDTO the payTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new payTypeDTO, or with status 400 (Bad Request) if the payType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pay-types")
    @Timed
    public ResponseEntity<PayTypeDTO> createPayType(@RequestBody PayTypeDTO payTypeDTO) throws URISyntaxException {
        log.debug("REST request to save PayType : {}", payTypeDTO);
        if (payTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new payType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PayTypeDTO result = payTypeService.save(payTypeDTO);
        return ResponseEntity.created(new URI("/api/pay-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pay-types : Updates an existing payType.
     *
     * @param payTypeDTO the payTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated payTypeDTO,
     * or with status 400 (Bad Request) if the payTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the payTypeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pay-types")
    @Timed
    public ResponseEntity<PayTypeDTO> updatePayType(@RequestBody PayTypeDTO payTypeDTO) throws URISyntaxException {
        log.debug("REST request to update PayType : {}", payTypeDTO);
        if (payTypeDTO.getId() == null) {
            return createPayType(payTypeDTO);
        }
        PayTypeDTO result = payTypeService.save(payTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pay-types : get all the payTypes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of payTypes in body
     */
    @GetMapping("/pay-types")
    @Timed
    public ResponseEntity<List<PayTypeDTO>> getAllPayTypes(PayTypeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get PayTypes by criteria: {}", criteria);
        Page<PayTypeDTO> page = payTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pay-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pay-types/:id : get the "id" payType.
     *
     * @param id the id of the payTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the payTypeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/pay-types/{id}")
    @Timed
    public ResponseEntity<PayTypeDTO> getPayType(@PathVariable Long id) {
        log.debug("REST request to get PayType : {}", id);
        PayTypeDTO payTypeDTO = payTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(payTypeDTO));
    }

    /**
     * DELETE  /pay-types/:id : delete the "id" payType.
     *
     * @param id the id of the payTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pay-types/{id}")
    @Timed
    public ResponseEntity<Void> deletePayType(@PathVariable Long id) {
        log.debug("REST request to delete PayType : {}", id);
        payTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/pay-types?query=:query : search for the payType corresponding
     * to the query.
     *
     * @param query the query of the payType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/pay-types")
    @Timed
    public ResponseEntity<List<PayTypeDTO>> searchPayTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of PayTypes for query {}", query);
        Page<PayTypeDTO> page = payTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/pay-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
