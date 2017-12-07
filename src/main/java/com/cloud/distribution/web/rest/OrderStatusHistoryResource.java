package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.OrderStatusHistoryService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.OrderStatusHistoryDTO;
import com.cloud.distribution.service.dto.OrderStatusHistoryCriteria;
import com.cloud.distribution.service.OrderStatusHistoryQueryService;
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
 * REST controller for managing OrderStatusHistory.
 */
@RestController
@RequestMapping("/api")
public class OrderStatusHistoryResource {

    private final Logger log = LoggerFactory.getLogger(OrderStatusHistoryResource.class);

    private static final String ENTITY_NAME = "orderStatusHistory";

    private final OrderStatusHistoryService orderStatusHistoryService;

    private final OrderStatusHistoryQueryService orderStatusHistoryQueryService;

    public OrderStatusHistoryResource(OrderStatusHistoryService orderStatusHistoryService, OrderStatusHistoryQueryService orderStatusHistoryQueryService) {
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.orderStatusHistoryQueryService = orderStatusHistoryQueryService;
    }

    /**
     * POST  /order-status-histories : Create a new orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new orderStatusHistoryDTO, or with status 400 (Bad Request) if the orderStatusHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/order-status-histories")
    @Timed
    public ResponseEntity<OrderStatusHistoryDTO> createOrderStatusHistory(@RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save OrderStatusHistory : {}", orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderStatusHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderStatusHistoryDTO result = orderStatusHistoryService.save(orderStatusHistoryDTO);
        return ResponseEntity.created(new URI("/api/order-status-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /order-status-histories : Updates an existing orderStatusHistory.
     *
     * @param orderStatusHistoryDTO the orderStatusHistoryDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated orderStatusHistoryDTO,
     * or with status 400 (Bad Request) if the orderStatusHistoryDTO is not valid,
     * or with status 500 (Internal Server Error) if the orderStatusHistoryDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/order-status-histories")
    @Timed
    public ResponseEntity<OrderStatusHistoryDTO> updateOrderStatusHistory(@RequestBody OrderStatusHistoryDTO orderStatusHistoryDTO) throws URISyntaxException {
        log.debug("REST request to update OrderStatusHistory : {}", orderStatusHistoryDTO);
        if (orderStatusHistoryDTO.getId() == null) {
            return createOrderStatusHistory(orderStatusHistoryDTO);
        }
        OrderStatusHistoryDTO result = orderStatusHistoryService.save(orderStatusHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderStatusHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /order-status-histories : get all the orderStatusHistories.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of orderStatusHistories in body
     */
    @GetMapping("/order-status-histories")
    @Timed
    public ResponseEntity<List<OrderStatusHistoryDTO>> getAllOrderStatusHistories(OrderStatusHistoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OrderStatusHistories by criteria: {}", criteria);
        Page<OrderStatusHistoryDTO> page = orderStatusHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/order-status-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /order-status-histories/:id : get the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the orderStatusHistoryDTO, or with status 404 (Not Found)
     */
    @GetMapping("/order-status-histories/{id}")
    @Timed
    public ResponseEntity<OrderStatusHistoryDTO> getOrderStatusHistory(@PathVariable Long id) {
        log.debug("REST request to get OrderStatusHistory : {}", id);
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(orderStatusHistoryDTO));
    }

    /**
     * DELETE  /order-status-histories/:id : delete the "id" orderStatusHistory.
     *
     * @param id the id of the orderStatusHistoryDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/order-status-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteOrderStatusHistory(@PathVariable Long id) {
        log.debug("REST request to delete OrderStatusHistory : {}", id);
        orderStatusHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/order-status-histories?query=:query : search for the orderStatusHistory corresponding
     * to the query.
     *
     * @param query the query of the orderStatusHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/order-status-histories")
    @Timed
    public ResponseEntity<List<OrderStatusHistoryDTO>> searchOrderStatusHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of OrderStatusHistories for query {}", query);
        Page<OrderStatusHistoryDTO> page = orderStatusHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/order-status-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
