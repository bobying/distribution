package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.OrderType;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.OrderTypeRepository;
import com.cloud.distribution.service.OrderTypeService;
import com.cloud.distribution.repository.search.OrderTypeSearchRepository;
import com.cloud.distribution.service.dto.OrderTypeDTO;
import com.cloud.distribution.service.mapper.OrderTypeMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.OrderTypeCriteria;
import com.cloud.distribution.service.OrderTypeQueryService;

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
 * Test class for the OrderTypeResource REST controller.
 *
 * @see OrderTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class OrderTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private OrderTypeMapper orderTypeMapper;

    @Autowired
    private OrderTypeService orderTypeService;

    @Autowired
    private OrderTypeSearchRepository orderTypeSearchRepository;

    @Autowired
    private OrderTypeQueryService orderTypeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrderTypeMockMvc;

    private OrderType orderType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrderTypeResource orderTypeResource = new OrderTypeResource(orderTypeService, orderTypeQueryService);
        this.restOrderTypeMockMvc = MockMvcBuilders.standaloneSetup(orderTypeResource)
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
    public static OrderType createEntity(EntityManager em) {
        OrderType orderType = new OrderType()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .code(DEFAULT_CODE);
        return orderType;
    }

    @Before
    public void initTest() {
        orderTypeSearchRepository.deleteAll();
        orderType = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderType() throws Exception {
        int databaseSizeBeforeCreate = orderTypeRepository.findAll().size();

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);
        restOrderTypeMockMvc.perform(post("/api/order-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeCreate + 1);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrderType.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testOrderType.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the OrderType in Elasticsearch
        OrderType orderTypeEs = orderTypeSearchRepository.findOne(testOrderType.getId());
        assertThat(orderTypeEs).isEqualToComparingFieldByField(testOrderType);
    }

    @Test
    @Transactional
    public void createOrderTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderTypeRepository.findAll().size();

        // Create the OrderType with an existing ID
        orderType.setId(1L);
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderTypeMockMvc.perform(post("/api/order-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOrderTypes() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList
        restOrderTypeMockMvc.perform(get("/api/order-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getOrderType() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get the orderType
        restOrderTypeMockMvc.perform(get("/api/order-types/{id}", orderType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(orderType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllOrderTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where name equals to DEFAULT_NAME
        defaultOrderTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the orderTypeList where name equals to UPDATED_NAME
        defaultOrderTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrderTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOrderTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the orderTypeList where name equals to UPDATED_NAME
        defaultOrderTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrderTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where name is not null
        defaultOrderTypeShouldBeFound("name.specified=true");

        // Get all the orderTypeList where name is null
        defaultOrderTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderTypesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where code equals to DEFAULT_CODE
        defaultOrderTypeShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the orderTypeList where code equals to UPDATED_CODE
        defaultOrderTypeShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderTypesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where code in DEFAULT_CODE or UPDATED_CODE
        defaultOrderTypeShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the orderTypeList where code equals to UPDATED_CODE
        defaultOrderTypeShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrderTypesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);

        // Get all the orderTypeList where code is not null
        defaultOrderTypeShouldBeFound("code.specified=true");

        // Get all the orderTypeList where code is null
        defaultOrderTypeShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrderTypesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        orderType.setStatus(status);
        orderTypeRepository.saveAndFlush(orderType);
        Long statusId = status.getId();

        // Get all the orderTypeList where status equals to statusId
        defaultOrderTypeShouldBeFound("statusId.equals=" + statusId);

        // Get all the orderTypeList where status equals to statusId + 1
        defaultOrderTypeShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrderTypeShouldBeFound(String filter) throws Exception {
        restOrderTypeMockMvc.perform(get("/api/order-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrderTypeShouldNotBeFound(String filter) throws Exception {
        restOrderTypeMockMvc.perform(get("/api/order-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingOrderType() throws Exception {
        // Get the orderType
        restOrderTypeMockMvc.perform(get("/api/order-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderType() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);
        orderTypeSearchRepository.save(orderType);
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().size();

        // Update the orderType
        OrderType updatedOrderType = orderTypeRepository.findOne(orderType.getId());
        // Disconnect from session so that the updates on updatedOrderType are not directly saved in db
        em.detach(updatedOrderType);
        updatedOrderType
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .code(UPDATED_CODE);
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(updatedOrderType);

        restOrderTypeMockMvc.perform(put("/api/order-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTypeDTO)))
            .andExpect(status().isOk());

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrderType.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testOrderType.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the OrderType in Elasticsearch
        OrderType orderTypeEs = orderTypeSearchRepository.findOne(testOrderType.getId());
        assertThat(orderTypeEs).isEqualToComparingFieldByField(testOrderType);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().size();

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrderTypeMockMvc.perform(put("/api/order-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrderType() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);
        orderTypeSearchRepository.save(orderType);
        int databaseSizeBeforeDelete = orderTypeRepository.findAll().size();

        // Get the orderType
        restOrderTypeMockMvc.perform(delete("/api/order-types/{id}", orderType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean orderTypeExistsInEs = orderTypeSearchRepository.exists(orderType.getId());
        assertThat(orderTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<OrderType> orderTypeList = orderTypeRepository.findAll();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrderType() throws Exception {
        // Initialize the database
        orderTypeRepository.saveAndFlush(orderType);
        orderTypeSearchRepository.save(orderType);

        // Search the orderType
        restOrderTypeMockMvc.perform(get("/api/_search/order-types?query=id:" + orderType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderType.class);
        OrderType orderType1 = new OrderType();
        orderType1.setId(1L);
        OrderType orderType2 = new OrderType();
        orderType2.setId(orderType1.getId());
        assertThat(orderType1).isEqualTo(orderType2);
        orderType2.setId(2L);
        assertThat(orderType1).isNotEqualTo(orderType2);
        orderType1.setId(null);
        assertThat(orderType1).isNotEqualTo(orderType2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderTypeDTO.class);
        OrderTypeDTO orderTypeDTO1 = new OrderTypeDTO();
        orderTypeDTO1.setId(1L);
        OrderTypeDTO orderTypeDTO2 = new OrderTypeDTO();
        assertThat(orderTypeDTO1).isNotEqualTo(orderTypeDTO2);
        orderTypeDTO2.setId(orderTypeDTO1.getId());
        assertThat(orderTypeDTO1).isEqualTo(orderTypeDTO2);
        orderTypeDTO2.setId(2L);
        assertThat(orderTypeDTO1).isNotEqualTo(orderTypeDTO2);
        orderTypeDTO1.setId(null);
        assertThat(orderTypeDTO1).isNotEqualTo(orderTypeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(orderTypeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(orderTypeMapper.fromId(null)).isNull();
    }
}
