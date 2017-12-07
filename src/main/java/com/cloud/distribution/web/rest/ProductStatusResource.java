package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.ProductStatusService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.ProductStatusDTO;
import com.cloud.distribution.service.dto.ProductStatusCriteria;
import com.cloud.distribution.service.ProductStatusQueryService;
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
 * REST controller for managing ProductStatus.
 */
@RestController
@RequestMapping("/api")
public class ProductStatusResource {

    private final Logger log = LoggerFactory.getLogger(ProductStatusResource.class);

    private static final String ENTITY_NAME = "productStatus";

    private final ProductStatusService productStatusService;

    private final ProductStatusQueryService productStatusQueryService;

    public ProductStatusResource(ProductStatusService productStatusService, ProductStatusQueryService productStatusQueryService) {
        this.productStatusService = productStatusService;
        this.productStatusQueryService = productStatusQueryService;
    }

    /**
     * POST  /product-statuses : Create a new productStatus.
     *
     * @param productStatusDTO the productStatusDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productStatusDTO, or with status 400 (Bad Request) if the productStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/product-statuses")
    @Timed
    public ResponseEntity<ProductStatusDTO> createProductStatus(@RequestBody ProductStatusDTO productStatusDTO) throws URISyntaxException {
        log.debug("REST request to save ProductStatus : {}", productStatusDTO);
        if (productStatusDTO.getId() != null) {
            throw new BadRequestAlertException("A new productStatus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductStatusDTO result = productStatusService.save(productStatusDTO);
        return ResponseEntity.created(new URI("/api/product-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /product-statuses : Updates an existing productStatus.
     *
     * @param productStatusDTO the productStatusDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productStatusDTO,
     * or with status 400 (Bad Request) if the productStatusDTO is not valid,
     * or with status 500 (Internal Server Error) if the productStatusDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/product-statuses")
    @Timed
    public ResponseEntity<ProductStatusDTO> updateProductStatus(@RequestBody ProductStatusDTO productStatusDTO) throws URISyntaxException {
        log.debug("REST request to update ProductStatus : {}", productStatusDTO);
        if (productStatusDTO.getId() == null) {
            return createProductStatus(productStatusDTO);
        }
        ProductStatusDTO result = productStatusService.save(productStatusDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, productStatusDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /product-statuses : get all the productStatuses.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of productStatuses in body
     */
    @GetMapping("/product-statuses")
    @Timed
    public ResponseEntity<List<ProductStatusDTO>> getAllProductStatuses(ProductStatusCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ProductStatuses by criteria: {}", criteria);
        Page<ProductStatusDTO> page = productStatusQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/product-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /product-statuses/:id : get the "id" productStatus.
     *
     * @param id the id of the productStatusDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productStatusDTO, or with status 404 (Not Found)
     */
    @GetMapping("/product-statuses/{id}")
    @Timed
    public ResponseEntity<ProductStatusDTO> getProductStatus(@PathVariable Long id) {
        log.debug("REST request to get ProductStatus : {}", id);
        ProductStatusDTO productStatusDTO = productStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productStatusDTO));
    }

    /**
     * DELETE  /product-statuses/:id : delete the "id" productStatus.
     *
     * @param id the id of the productStatusDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/product-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteProductStatus(@PathVariable Long id) {
        log.debug("REST request to delete ProductStatus : {}", id);
        productStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/product-statuses?query=:query : search for the productStatus corresponding
     * to the query.
     *
     * @param query the query of the productStatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/product-statuses")
    @Timed
    public ResponseEntity<List<ProductStatusDTO>> searchProductStatuses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ProductStatuses for query {}", query);
        Page<ProductStatusDTO> page = productStatusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/product-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
