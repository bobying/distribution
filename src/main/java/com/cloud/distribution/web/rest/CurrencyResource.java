package com.cloud.distribution.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloud.distribution.service.CurrencyService;
import com.cloud.distribution.web.rest.errors.BadRequestAlertException;
import com.cloud.distribution.web.rest.util.HeaderUtil;
import com.cloud.distribution.web.rest.util.PaginationUtil;
import com.cloud.distribution.service.dto.CurrencyDTO;
import com.cloud.distribution.service.dto.CurrencyCriteria;
import com.cloud.distribution.service.CurrencyQueryService;
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
 * REST controller for managing Currency.
 */
@RestController
@RequestMapping("/api")
public class CurrencyResource {

    private final Logger log = LoggerFactory.getLogger(CurrencyResource.class);

    private static final String ENTITY_NAME = "currency";

    private final CurrencyService currencyService;

    private final CurrencyQueryService currencyQueryService;

    public CurrencyResource(CurrencyService currencyService, CurrencyQueryService currencyQueryService) {
        this.currencyService = currencyService;
        this.currencyQueryService = currencyQueryService;
    }

    /**
     * POST  /currencies : Create a new currency.
     *
     * @param currencyDTO the currencyDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new currencyDTO, or with status 400 (Bad Request) if the currency has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/currencies")
    @Timed
    public ResponseEntity<CurrencyDTO> createCurrency(@RequestBody CurrencyDTO currencyDTO) throws URISyntaxException {
        log.debug("REST request to save Currency : {}", currencyDTO);
        if (currencyDTO.getId() != null) {
            throw new BadRequestAlertException("A new currency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CurrencyDTO result = currencyService.save(currencyDTO);
        return ResponseEntity.created(new URI("/api/currencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /currencies : Updates an existing currency.
     *
     * @param currencyDTO the currencyDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated currencyDTO,
     * or with status 400 (Bad Request) if the currencyDTO is not valid,
     * or with status 500 (Internal Server Error) if the currencyDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/currencies")
    @Timed
    public ResponseEntity<CurrencyDTO> updateCurrency(@RequestBody CurrencyDTO currencyDTO) throws URISyntaxException {
        log.debug("REST request to update Currency : {}", currencyDTO);
        if (currencyDTO.getId() == null) {
            return createCurrency(currencyDTO);
        }
        CurrencyDTO result = currencyService.save(currencyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, currencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /currencies : get all the currencies.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of currencies in body
     */
    @GetMapping("/currencies")
    @Timed
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies(CurrencyCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Currencies by criteria: {}", criteria);
        Page<CurrencyDTO> page = currencyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/currencies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /currencies/:id : get the "id" currency.
     *
     * @param id the id of the currencyDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the currencyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/currencies/{id}")
    @Timed
    public ResponseEntity<CurrencyDTO> getCurrency(@PathVariable Long id) {
        log.debug("REST request to get Currency : {}", id);
        CurrencyDTO currencyDTO = currencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(currencyDTO));
    }

    /**
     * DELETE  /currencies/:id : delete the "id" currency.
     *
     * @param id the id of the currencyDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/currencies/{id}")
    @Timed
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        log.debug("REST request to delete Currency : {}", id);
        currencyService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/currencies?query=:query : search for the currency corresponding
     * to the query.
     *
     * @param query the query of the currency search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/currencies")
    @Timed
    public ResponseEntity<List<CurrencyDTO>> searchCurrencies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Currencies for query {}", query);
        Page<CurrencyDTO> page = currencyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/currencies");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
