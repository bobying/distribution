package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.OrderStatusHistory;
import com.cloud.distribution.domain.OperatorType;
import com.cloud.distribution.domain.Order;
import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.repository.OrderStatusHistoryRepository;
import com.cloud.distribution.service.OrderStatusHistoryService;
import com.cloud.distribution.repository.search.OrderStatusHistorySearchRepository;
import com.cloud.distribution.service.dto.OrderStatusHistoryDTO;
import com.cloud.distribution.service.mapper.OrderStatusHistoryMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.OrderStatusHistoryCriteria;
import com.cloud.distribution.service.OrderStatusHistoryQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.cloud.distribution.web.rest.TestUtil.sameInstant;
import static com.cloud.distribution.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrderStatusHistoryResource REST controller.
 *
 * @see OrderStatusHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class OrderStatusHistoryResourceIntTest {

    private static final ZonedDateTime DEFAULT_MODIFIED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_MODIFIED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_OPERATOR_CODE = "AAAAAAAAAA";
    private static final String UPDATED_OPERATOR_CODE = "BBBBBBBBBB";

    @Autowired
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    private OrderStatusHistoryMapper orderStatusHistoryMapper;

    @Autowired
    private OrderStatusHistoryService orderStatusHistoryService;

    @Autowired
    private OrderStatusHistorySearchRepository orderStatusHistorySearchRepository;

    @Autowired
    private OrderStatusHistoryQueryService orderStatusHistoryQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrderStatusHistoryMockMvc;

    private OrderStatusHistory orderStatusHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrderStatusHistoryResource orderStatusHistoryResource = new OrderStatusHistoryResource(orderStatusHistoryService, orderStatusHistoryQueryService);
        this.restOrderStatusHistoryMockMvc = MockMvcBuilders.standaloneSetup(orderStatusHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderStatusHistory createEntity(EntityManager em) {
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory()
            .modifiedTime(DEFAULT_MODIFIED_TIME)
            .desc(DEFAULT_DESC)
            .operatorCode(DEFAULT_OPERATOR_CODE);
        return orderStatusHistory;
    }

    @Before
    public void initTest() {
        orderStatusHistorySearchRepository.deleteAll();
        orderStatusHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderStatusHistory() throws Exception {
        int databaseSizeBeforeCreate = orderStatusHistoryRepository.findAll().size();

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);
        restOrderStatusHistoryMockMvc.perform(post("/api/order-status-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderStatusHistory in the database
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findAll();
        assertThat(orderStatusHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        OrderStatusHistory testOrderStatusHistory = orderStatusHistoryList.get(orderStatusHistoryList.size() - 1);
        assertThat(testOrderStatusHistory.getModifiedTime()).isEqualTo(DEFAULT_MODIFIED_TIME);
        assertThat(testOrderStatusHistory.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testOrderStatusHistory.getOperatorCode()).isEqualTo(DEFAULT_OPERATOR_CODE);

        // Validate the OrderStatusHistory in Elasticsearch
        OrderStatusHistory orderStatusHistoryEs = orderStatusHistorySearchRepository.findOne(testOrderStatusHistory.getId());
        assertThat(orderStatusHistoryEs).isEqualToComparingFieldByField(testOrderStatusHistory);
    }

    @Test
    @Transactional
    public void createOrderStatusHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderStatusHistoryRepository.findAll().size();

        // Create the OrderStatusHistory with an existing ID
        orderStatusHistory.setId(1L);
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderStatusHistoryMockMvc.perform(post("/api/order-status-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderStatusHistory in the database
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findAll();
        assertThat(orderStatusHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistories() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList
        restOrderStatusHistoryMockMvc.perform(get("/api/order-status-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].modifiedTime").value(hasItem(sameInstant(DEFAULT_MODIFIED_TIME))))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].operatorCode").value(hasItem(DEFAULT_OPERATOR_CODE.toString())));
    }

    @Test
    @Transactional
    public void getOrderStatusHistory() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get the orderStatusHistory
        restOrderStatusHistoryMockMvc.perform(get("/api/order-status-histories/{id}", orderStatusHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderStatusHistory.getId().intValue()))
            .andExpect(jsonPath("$.modifiedTime").value(sameInstant(DEFAULT_MODIFIED_TIME)))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.operatorCode").value(DEFAULT_OPERATOR_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByModifiedTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where modifiedTime equals to DEFAULT_MODIFIED_TIME
        defaultOrderStatusHistoryShouldBeFound("modifiedTime.equals=" + DEFAULT_MODIFIED_TIME);

        // Get all the orderStatusHistoryList where modifiedTime equals to UPDATED_MODIFIED_TIME
        defaultOrderStatusHistoryShouldNotBeFound("modifiedTime.equals=" + UPDATED_MODIFIED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByModifiedTimeIsInShouldWork() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where modifiedTime in DEFAULT_MODIFIED_TIME or UPDATED_MODIFIED_TIME
        defaultOrderStatusHistoryShouldBeFound("modifiedTime.in=" + DEFAULT_MODIFIED_TIME + "," + UPDATED_MODIFIED_TIME);

        // Get all the orderStatusHistoryList where modifiedTime equals to UPDATED_MODIFIED_TIME
        defaultOrderStatusHistoryShouldNotBeFound("modifiedTime.in=" + UPDATED_MODIFIED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByModifiedTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where modifiedTime is not null
        defaultOrderStatusHistoryShouldBeFound("modifiedTime.specified=true");

        // Get all the orderStatusHistoryList where modifiedTime is null
        defaultOrderStatusHistoryShouldNotBeFound("modifiedTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByModifiedTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where modifiedTime greater than or equals to DEFAULT_MODIFIED_TIME
        defaultOrderStatusHistoryShouldBeFound("modifiedTime.greaterOrEqualThan=" + DEFAULT_MODIFIED_TIME);

        // Get all the orderStatusHistoryList where modifiedTime greater than or equals to UPDATED_MODIFIED_TIME
        defaultOrderStatusHistoryShouldNotBeFound("modifiedTime.greaterOrEqualThan=" + UPDATED_MODIFIED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByModifiedTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where modifiedTime less than or equals to DEFAULT_MODIFIED_TIME
        defaultOrderStatusHistoryShouldNotBeFound("modifiedTime.lessThan=" + DEFAULT_MODIFIED_TIME);

        // Get all the orderStatusHistoryList where modifiedTime less than or equals to UPDATED_MODIFIED_TIME
        defaultOrderStatusHistoryShouldBeFound("modifiedTime.lessThan=" + UPDATED_MODIFIED_TIME);
    }


    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOperatorCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where operatorCode equals to DEFAULT_OPERATOR_CODE
        defaultOrderStatusHistoryShouldBeFound("operatorCode.equals=" + DEFAULT_OPERATOR_CODE);

        // Get all the orderStatusHistoryList where operatorCode equals to UPDATED_OPERATOR_CODE
        defaultOrderStatusHistoryShouldNotBeFound("operatorCode.equals=" + UPDATED_OPERATOR_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOperatorCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where operatorCode in DEFAULT_OPERATOR_CODE or UPDATED_OPERATOR_CODE
        defaultOrderStatusHistoryShouldBeFound("operatorCode.in=" + DEFAULT_OPERATOR_CODE + "," + UPDATED_OPERATOR_CODE);

        // Get all the orderStatusHistoryList where operatorCode equals to UPDATED_OPERATOR_CODE
        defaultOrderStatusHistoryShouldNotBeFound("operatorCode.in=" + UPDATED_OPERATOR_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOperatorCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);

        // Get all the orderStatusHistoryList where operatorCode is not null
        defaultOrderStatusHistoryShouldBeFound("operatorCode.specified=true");

        // Get all the orderStatusHistoryList where operatorCode is null
        defaultOrderStatusHistoryShouldNotBeFound("operatorCode.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOperatorTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        OperatorType operatorType = OperatorTypeResourceIntTest.createEntity(em);
        em.persist(operatorType);
        em.flush();
        orderStatusHistory.setOperatorType(operatorType);
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        Long operatorTypeId = operatorType.getId();

        // Get all the orderStatusHistoryList where operatorType equals to operatorTypeId
        defaultOrderStatusHistoryShouldBeFound("operatorTypeId.equals=" + operatorTypeId);

        // Get all the orderStatusHistoryList where operatorType equals to operatorTypeId + 1
        defaultOrderStatusHistoryShouldNotBeFound("operatorTypeId.equals=" + (operatorTypeId + 1));
    }


    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOrderIsEqualToSomething() throws Exception {
        // Initialize the database
        Order order = OrderResourceIntTest.createEntity(em);
        em.persist(order);
        em.flush();
        orderStatusHistory.setOrder(order);
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        Long orderId = order.getId();

        // Get all the orderStatusHistoryList where order equals to orderId
        defaultOrderStatusHistoryShouldBeFound("orderId.equals=" + orderId);

        // Get all the orderStatusHistoryList where order equals to orderId + 1
        defaultOrderStatusHistoryShouldNotBeFound("orderId.equals=" + (orderId + 1));
    }


    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByOldStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        OrderStatus oldStatus = OrderStatusResourceIntTest.createEntity(em);
        em.persist(oldStatus);
        em.flush();
        orderStatusHistory.setOldStatus(oldStatus);
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        Long oldStatusId = oldStatus.getId();

        // Get all the orderStatusHistoryList where oldStatus equals to oldStatusId
        defaultOrderStatusHistoryShouldBeFound("oldStatusId.equals=" + oldStatusId);

        // Get all the orderStatusHistoryList where oldStatus equals to oldStatusId + 1
        defaultOrderStatusHistoryShouldNotBeFound("oldStatusId.equals=" + (oldStatusId + 1));
    }


    @Test
    @Transactional
    public void getAllOrderStatusHistoriesByNewStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        OrderStatus newStatus = OrderStatusResourceIntTest.createEntity(em);
        em.persist(newStatus);
        em.flush();
        orderStatusHistory.setNewStatus(newStatus);
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        Long newStatusId = newStatus.getId();

        // Get all the orderStatusHistoryList where newStatus equals to newStatusId
        defaultOrderStatusHistoryShouldBeFound("newStatusId.equals=" + newStatusId);

        // Get all the orderStatusHistoryList where newStatus equals to newStatusId + 1
        defaultOrderStatusHistoryShouldNotBeFound("newStatusId.equals=" + (newStatusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrderStatusHistoryShouldBeFound(String filter) throws Exception {
        restOrderStatusHistoryMockMvc.perform(get("/api/order-status-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].modifiedTime").value(hasItem(sameInstant(DEFAULT_MODIFIED_TIME))))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].operatorCode").value(hasItem(DEFAULT_OPERATOR_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrderStatusHistoryShouldNotBeFound(String filter) throws Exception {
        restOrderStatusHistoryMockMvc.perform(get("/api/order-status-histories?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingOrderStatusHistory() throws Exception {
        // Get the orderStatusHistory
        restOrderStatusHistoryMockMvc.perform(get("/api/order-status-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderStatusHistory() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        orderStatusHistorySearchRepository.save(orderStatusHistory);
        int databaseSizeBeforeUpdate = orderStatusHistoryRepository.findAll().size();

        // Update the orderStatusHistory
        OrderStatusHistory updatedOrderStatusHistory = orderStatusHistoryRepository.findOne(orderStatusHistory.getId());
        // Disconnect from session so that the updates on updatedOrderStatusHistory are not directly saved in db
        em.detach(updatedOrderStatusHistory);
        updatedOrderStatusHistory
            .modifiedTime(UPDATED_MODIFIED_TIME)
            .desc(UPDATED_DESC)
            .operatorCode(UPDATED_OPERATOR_CODE);
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(updatedOrderStatusHistory);

        restOrderStatusHistoryMockMvc.perform(put("/api/order-status-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusHistoryDTO)))
            .andExpect(status().isOk());

        // Validate the OrderStatusHistory in the database
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findAll();
        assertThat(orderStatusHistoryList).hasSize(databaseSizeBeforeUpdate);
        OrderStatusHistory testOrderStatusHistory = orderStatusHistoryList.get(orderStatusHistoryList.size() - 1);
        assertThat(testOrderStatusHistory.getModifiedTime()).isEqualTo(UPDATED_MODIFIED_TIME);
        assertThat(testOrderStatusHistory.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testOrderStatusHistory.getOperatorCode()).isEqualTo(UPDATED_OPERATOR_CODE);

        // Validate the OrderStatusHistory in Elasticsearch
        OrderStatusHistory orderStatusHistoryEs = orderStatusHistorySearchRepository.findOne(testOrderStatusHistory.getId());
        assertThat(orderStatusHistoryEs).isEqualToComparingFieldByField(testOrderStatusHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderStatusHistory() throws Exception {
        int databaseSizeBeforeUpdate = orderStatusHistoryRepository.findAll().size();

        // Create the OrderStatusHistory
        OrderStatusHistoryDTO orderStatusHistoryDTO = orderStatusHistoryMapper.toDto(orderStatusHistory);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrderStatusHistoryMockMvc.perform(put("/api/order-status-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderStatusHistory in the database
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findAll();
        assertThat(orderStatusHistoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrderStatusHistory() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        orderStatusHistorySearchRepository.save(orderStatusHistory);
        int databaseSizeBeforeDelete = orderStatusHistoryRepository.findAll().size();

        // Get the orderStatusHistory
        restOrderStatusHistoryMockMvc.perform(delete("/api/order-status-histories/{id}", orderStatusHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean orderStatusHistoryExistsInEs = orderStatusHistorySearchRepository.exists(orderStatusHistory.getId());
        assertThat(orderStatusHistoryExistsInEs).isFalse();

        // Validate the database is empty
        List<OrderStatusHistory> orderStatusHistoryList = orderStatusHistoryRepository.findAll();
        assertThat(orderStatusHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrderStatusHistory() throws Exception {
        // Initialize the database
        orderStatusHistoryRepository.saveAndFlush(orderStatusHistory);
        orderStatusHistorySearchRepository.save(orderStatusHistory);

        // Search the orderStatusHistory
        restOrderStatusHistoryMockMvc.perform(get("/api/_search/order-status-histories?query=id:" + orderStatusHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].modifiedTime").value(hasItem(sameInstant(DEFAULT_MODIFIED_TIME))))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].operatorCode").value(hasItem(DEFAULT_OPERATOR_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistory.class);
        OrderStatusHistory orderStatusHistory1 = new OrderStatusHistory();
        orderStatusHistory1.setId(1L);
        OrderStatusHistory orderStatusHistory2 = new OrderStatusHistory();
        orderStatusHistory2.setId(orderStatusHistory1.getId());
        assertThat(orderStatusHistory1).isEqualTo(orderStatusHistory2);
        orderStatusHistory2.setId(2L);
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);
        orderStatusHistory1.setId(null);
        assertThat(orderStatusHistory1).isNotEqualTo(orderStatusHistory2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusHistoryDTO.class);
        OrderStatusHistoryDTO orderStatusHistoryDTO1 = new OrderStatusHistoryDTO();
        orderStatusHistoryDTO1.setId(1L);
        OrderStatusHistoryDTO orderStatusHistoryDTO2 = new OrderStatusHistoryDTO();
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(orderStatusHistoryDTO1.getId());
        assertThat(orderStatusHistoryDTO1).isEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO2.setId(2L);
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
        orderStatusHistoryDTO1.setId(null);
        assertThat(orderStatusHistoryDTO1).isNotEqualTo(orderStatusHistoryDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(orderStatusHistoryMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(orderStatusHistoryMapper.fromId(null)).isNull();
    }
}
