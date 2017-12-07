package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.OrderTypeService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.OrderTypeDTO;
import com.cloud.distribution.service.dto.OrderTypeCriteria;
import com.cloud.distribution.service.OrderTypeQueryService;
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
 * REST controller for managing OrderType.
 */
@RestController
@RequestMapping("/api")
public class OrderTypeResource {

    private final Logger log = LoggerFactory.getLogger(OrderTypeResource.class);

    private static final String ENTITY_NAME = "orderType";

    private final OrderTypeService orderTypeService;

    private final OrderTypeQueryService orderTypeQueryService;

    public OrderTypeResource(OrderTypeService orderTypeService, OrderTypeQueryService orderTypeQueryService) {
        this.orderTypeService = orderTypeService;
        this.orderTypeQueryService = orderTypeQueryService;
    }

    /**
     * POST  /order-types : Create a new orderType.
     *
     * @param orderTypeDTO the orderTypeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orderTypeDTO, or with status 400 (Bad Request) if the orderType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/order-types")
    @Timed
    public ResponseEntity<OrderTypeDTO> createOrderType(@RequestBody OrderTypeDTO orderTypeDTO) throws URISyntaxException {
        log.debug("REST request to save OrderType : {}", orderTypeDTO);
        if (orderTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderTypeDTO result = orderTypeService.save(orderTypeDTO);
        return ResponseEntity.created(new URI("/api/order-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /order-types : Updates an existing orderType.
     *
     * @param orderTypeDTO the orderTypeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orderTypeDTO,
     * or with status 400 (Bad Request) if the orderTypeDTO is not valid,
     * or with status 500 (Internal Server Error) if the orderTypeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/order-types")
    @Timed
    public ResponseEntity<OrderTypeDTO> updateOrderType(@RequestBody OrderTypeDTO orderTypeDTO) throws URISyntaxException {
        log.debug("REST request to update OrderType : {}", orderTypeDTO);
        if (orderTypeDTO.getId() == null) {
            return createOrderType(orderTypeDTO);
        }
        OrderTypeDTO result = orderTypeService.save(orderTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderTypeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /order-types : get all the orderTypes.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of orderTypes in body
     */
    @GetMapping("/order-types")
    @Timed
    public ResponseEntity<List<OrderTypeDTO>> getAllOrderTypes(OrderTypeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OrderTypes by criteria: {}", criteria);
        Page<OrderTypeDTO> page = orderTypeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/order-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /order-types/:id : get the "id" orderType.
     *
     * @param id the id of the orderTypeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderTypeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/order-types/{id}")
    @Timed
    public ResponseEntity<OrderTypeDTO> getOrderType(@PathVariable Long id) {
        log.debug("REST request to get OrderType : {}", id);
        OrderTypeDTO orderTypeDTO = orderTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderTypeDTO));
    }

    /**
     * DELETE  /order-types/:id : delete the "id" orderType.
     *
     * @param id the id of the orderTypeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/order-types/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrderType(@PathVariable Long id) {
        log.debug("REST request to delete OrderType : {}", id);
        orderTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/order-types?query=:query : search for the orderType corresponding
     * to the query.
     *
     * @param query the query of the orderType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/order-types")
    @Timed
    public ResponseEntity<List<OrderTypeDTO>> searchOrderTypes(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of OrderTypes for query {}", query);
        Page<OrderTypeDTO> page = orderTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/order-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
