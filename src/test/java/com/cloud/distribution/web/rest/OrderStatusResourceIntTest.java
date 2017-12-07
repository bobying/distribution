package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.OrderStatusRepository;
import com.cloud.distribution.service.OrderStatusService;
import com.cloud.distribution.repository.search.OrderStatusSearchRepository;
import com.cloud.distribution.service.dto.OrderStatusDTO;
import com.cloud.distribution.service.mapper.OrderStatusMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.OrderStatusCriteria;
import com.cloud.distribution.service.OrderStatusQueryService;

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
import java.util.List;

import static com.cloud.distribution.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrderStatusResource REST controller.
 *
 * @see OrderStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class OrderStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderStatusSearchRepository orderStatusSearchRepository;

    @Autowired
    private OrderStatusQueryService orderStatusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrderStatusMockMvc;

    private OrderStatus orderStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrderStatusResource orderStatusResource = new OrderStatusResource(orderStatusService, orderStatusQueryService);
        this.restOrderStatusMockMvc = MockMvcBuilders.standaloneSetup(orderStatusResource)
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
    public static OrderStatus createEntity(EntityManager em) {
        OrderStatus orderStatus = new OrderStatus()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC);
        return orderStatus;
    }

    @Before
    public void initTest() {
        orderStatusSearchRepository.deleteAll();
        orderStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderStatus() throws Exception {
        int databaseSizeBeforeCreate = orderStatusRepository.findAll().size();

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);
        restOrderStatusMockMvc.perform(post("/api/order-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderStatus in the database
        List<OrderStatus> orderStatusList = orderStatusRepository.findAll();
        assertThat(orderStatusList).hasSize(databaseSizeBeforeCreate + 1);
        OrderStatus testOrderStatus = orderStatusList.get(orderStatusList.size() - 1);
        assertThat(testOrderStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrderStatus.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOrderStatus.getDesc()).isEqualTo(DEFAULT_DESC);

        // Validate the OrderStatus in Elasticsearch
        OrderStatus orderStatusEs = orderStatusSearchRepository.findOne(testOrderStatus.getId());
        assertThat(orderStatusEs).isEqualToComparingFieldByField(testOrderStatus);
    }

    @Test
    @Transactional
    public void createOrderStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderStatusRepository.findAll().size();

        // Create the OrderStatus with an existing ID
        orderStatus.setId(1L);
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderStatusMockMvc.perform(post("/api/order-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderStatus in the database
        List<OrderStatus> orderStatusList = orderStatusRepository.findAll();
        assertThat(orderStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOrderStatuses() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList
        restOrderStatusMockMvc.perform(get("/api/order-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getOrderStatus() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get the orderStatus
        restOrderStatusMockMvc.perform(get("/api/order-statuses/{id}", orderStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where name equals to DEFAULT_NAME
        defaultOrderStatusShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the orderStatusList where name equals to UPDATED_NAME
        defaultOrderStatusShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOrderStatusShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the orderStatusList where name equals to UPDATED_NAME
        defaultOrderStatusShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where name is not null
        defaultOrderStatusShouldBeFound("name.specified=true");

        // Get all the orderStatusList where name is null
        defaultOrderStatusShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where code equals to DEFAULT_CODE
        defaultOrderStatusShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the orderStatusList where code equals to UPDATED_CODE
        defaultOrderStatusShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where code in DEFAULT_CODE or UPDATED_CODE
        defaultOrderStatusShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the orderStatusList where code equals to UPDATED_CODE
        defaultOrderStatusShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);

        // Get all the orderStatusList where code is not null
        defaultOrderStatusShouldBeFound("code.specified=true");

        // Get all the orderStatusList where code is null
        defaultOrderStatusShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderStatusesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        orderStatus.setStatus(status);
        orderStatusRepository.saveAndFlush(orderStatus);
        Long statusId = status.getId();

        // Get all the orderStatusList where status equals to statusId
        defaultOrderStatusShouldBeFound("statusId.equals=" + statusId);

        // Get all the orderStatusList where status equals to statusId + 1
        defaultOrderStatusShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrderStatusShouldBeFound(String filter) throws Exception {
        restOrderStatusMockMvc.perform(get("/api/order-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrderStatusShouldNotBeFound(String filter) throws Exception {
        restOrderStatusMockMvc.perform(get("/api/order-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingOrderStatus() throws Exception {
        // Get the orderStatus
        restOrderStatusMockMvc.perform(get("/api/order-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderStatus() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);
        orderStatusSearchRepository.save(orderStatus);
        int databaseSizeBeforeUpdate = orderStatusRepository.findAll().size();

        // Update the orderStatus
        OrderStatus updatedOrderStatus = orderStatusRepository.findOne(orderStatus.getId());
        // Disconnect from session so that the updates on updatedOrderStatus are not directly saved in db
        em.detach(updatedOrderStatus);
        updatedOrderStatus
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC);
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(updatedOrderStatus);

        restOrderStatusMockMvc.perform(put("/api/order-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusDTO)))
            .andExpect(status().isOk());

        // Validate the OrderStatus in the database
        List<OrderStatus> orderStatusList = orderStatusRepository.findAll();
        assertThat(orderStatusList).hasSize(databaseSizeBeforeUpdate);
        OrderStatus testOrderStatus = orderStatusList.get(orderStatusList.size() - 1);
        assertThat(testOrderStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrderStatus.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOrderStatus.getDesc()).isEqualTo(UPDATED_DESC);

        // Validate the OrderStatus in Elasticsearch
        OrderStatus orderStatusEs = orderStatusSearchRepository.findOne(testOrderStatus.getId());
        assertThat(orderStatusEs).isEqualToComparingFieldByField(testOrderStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderStatus() throws Exception {
        int databaseSizeBeforeUpdate = orderStatusRepository.findAll().size();

        // Create the OrderStatus
        OrderStatusDTO orderStatusDTO = orderStatusMapper.toDto(orderStatus);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrderStatusMockMvc.perform(put("/api/order-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderStatus in the database
        List<OrderStatus> orderStatusList = orderStatusRepository.findAll();
        assertThat(orderStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrderStatus() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);
        orderStatusSearchRepository.save(orderStatus);
        int databaseSizeBeforeDelete = orderStatusRepository.findAll().size();

        // Get the orderStatus
        restOrderStatusMockMvc.perform(delete("/api/order-statuses/{id}", orderStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean orderStatusExistsInEs = orderStatusSearchRepository.exists(orderStatus.getId());
        assertThat(orderStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<OrderStatus> orderStatusList = orderStatusRepository.findAll();
        assertThat(orderStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrderStatus() throws Exception {
        // Initialize the database
        orderStatusRepository.saveAndFlush(orderStatus);
        orderStatusSearchRepository.save(orderStatus);

        // Search the orderStatus
        restOrderStatusMockMvc.perform(get("/api/_search/order-statuses?query=id:" + orderStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatus.class);
        OrderStatus orderStatus1 = new OrderStatus();
        orderStatus1.setId(1L);
        OrderStatus orderStatus2 = new OrderStatus();
        orderStatus2.setId(orderStatus1.getId());
        assertThat(orderStatus1).isEqualTo(orderStatus2);
        orderStatus2.setId(2L);
        assertThat(orderStatus1).isNotEqualTo(orderStatus2);
        orderStatus1.setId(null);
        assertThat(orderStatus1).isNotEqualTo(orderStatus2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderStatusDTO.class);
        OrderStatusDTO orderStatusDTO1 = new OrderStatusDTO();
        orderStatusDTO1.setId(1L);
        OrderStatusDTO orderStatusDTO2 = new OrderStatusDTO();
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
        orderStatusDTO2.setId(orderStatusDTO1.getId());
        assertThat(orderStatusDTO1).isEqualTo(orderStatusDTO2);
        orderStatusDTO2.setId(2L);
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
        orderStatusDTO1.setId(null);
        assertThat(orderStatusDTO1).isNotEqualTo(orderStatusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(orderStatusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(orderStatusMapper.fromId(null)).isNull();
    }
}
