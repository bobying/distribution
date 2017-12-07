package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.Order;
import com.cloud.distribution.domain.PayType;
import com.cloud.distribution.domain.OrderType;
import com.cloud.distribution.domain.OrderStatus;
import com.cloud.distribution.domain.Product;
import com.cloud.distribution.repository.OrderRepository;
import com.cloud.distribution.service.OrderService;
import com.cloud.distribution.repository.search.OrderSearchRepository;
import com.cloud.distribution.service.dto.OrderDTO;
import com.cloud.distribution.service.mapper.OrderMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.OrderCriteria;
import com.cloud.distribution.service.OrderQueryService;

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
 * Test class for the OrderResource REST controller.
 *
 * @see OrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class OrderResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT = 1L;
    private static final Long UPDATED_AMOUNT = 2L;

    private static final ZonedDateTime DEFAULT_CREATED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderSearchRepository orderSearchRepository;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOrderMockMvc;

    private Order order;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OrderResource orderResource = new OrderResource(orderService, orderQueryService);
        this.restOrderMockMvc = MockMvcBuilders.standaloneSetup(orderResource)
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
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC)
            .amount(DEFAULT_AMOUNT)
            .createdDate(DEFAULT_CREATED_DATE)
            .userId(DEFAULT_USER_ID);
        return order;
    }

    @Before
    public void initTest() {
        orderSearchRepository.deleteAll();
        order = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        restOrderMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOrder.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testOrder.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOrder.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testOrder.getUserId()).isEqualTo(DEFAULT_USER_ID);

        // Validate the Order in Elasticsearch
        Order orderEs = orderSearchRepository.findOne(testOrder.getId());
        assertThat(orderEs).isEqualToComparingFieldByField(testOrder);
    }

    @Test
    @Transactional
    public void createOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // Create the Order with an existing ID
        order.setId(1L);
        OrderDTO orderDTO = orderMapper.toDto(order);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc.perform(post("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    public void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.createdDate").value(sameInstant(DEFAULT_CREATED_DATE)))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }

    @Test
    @Transactional
    public void getAllOrdersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where name equals to DEFAULT_NAME
        defaultOrderShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the orderList where name equals to UPDATED_NAME
        defaultOrderShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrdersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOrderShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the orderList where name equals to UPDATED_NAME
        defaultOrderShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOrdersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where name is not null
        defaultOrderShouldBeFound("name.specified=true");

        // Get all the orderList where name is null
        defaultOrderShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where code equals to DEFAULT_CODE
        defaultOrderShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the orderList where code equals to UPDATED_CODE
        defaultOrderShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where code in DEFAULT_CODE or UPDATED_CODE
        defaultOrderShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the orderList where code equals to UPDATED_CODE
        defaultOrderShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where code is not null
        defaultOrderShouldBeFound("code.specified=true");

        // Get all the orderList where code is null
        defaultOrderShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where amount equals to DEFAULT_AMOUNT
        defaultOrderShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the orderList where amount equals to UPDATED_AMOUNT
        defaultOrderShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultOrderShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the orderList where amount equals to UPDATED_AMOUNT
        defaultOrderShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where amount is not null
        defaultOrderShouldBeFound("amount.specified=true");

        // Get all the orderList where amount is null
        defaultOrderShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where amount greater than or equals to DEFAULT_AMOUNT
        defaultOrderShouldBeFound("amount.greaterOrEqualThan=" + DEFAULT_AMOUNT);

        // Get all the orderList where amount greater than or equals to UPDATED_AMOUNT
        defaultOrderShouldNotBeFound("amount.greaterOrEqualThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void getAllOrdersByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where amount less than or equals to DEFAULT_AMOUNT
        defaultOrderShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the orderList where amount less than or equals to UPDATED_AMOUNT
        defaultOrderShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }


    @Test
    @Transactional
    public void getAllOrdersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate equals to DEFAULT_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the orderList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the orderList where createdDate equals to UPDATED_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate is not null
        defaultOrderShouldBeFound("createdDate.specified=true");

        // Get all the orderList where createdDate is null
        defaultOrderShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByCreatedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate greater than or equals to DEFAULT_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.greaterOrEqualThan=" + DEFAULT_CREATED_DATE);

        // Get all the orderList where createdDate greater than or equals to UPDATED_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.greaterOrEqualThan=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllOrdersByCreatedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where createdDate less than or equals to DEFAULT_CREATED_DATE
        defaultOrderShouldNotBeFound("createdDate.lessThan=" + DEFAULT_CREATED_DATE);

        // Get all the orderList where createdDate less than or equals to UPDATED_CREATED_DATE
        defaultOrderShouldBeFound("createdDate.lessThan=" + UPDATED_CREATED_DATE);
    }


    @Test
    @Transactional
    public void getAllOrdersByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where userId equals to DEFAULT_USER_ID
        defaultOrderShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the orderList where userId equals to UPDATED_USER_ID
        defaultOrderShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllOrdersByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultOrderShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the orderList where userId equals to UPDATED_USER_ID
        defaultOrderShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllOrdersByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where userId is not null
        defaultOrderShouldBeFound("userId.specified=true");

        // Get all the orderList where userId is null
        defaultOrderShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where userId greater than or equals to DEFAULT_USER_ID
        defaultOrderShouldBeFound("userId.greaterOrEqualThan=" + DEFAULT_USER_ID);

        // Get all the orderList where userId greater than or equals to UPDATED_USER_ID
        defaultOrderShouldNotBeFound("userId.greaterOrEqualThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllOrdersByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where userId less than or equals to DEFAULT_USER_ID
        defaultOrderShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the orderList where userId less than or equals to UPDATED_USER_ID
        defaultOrderShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }


    @Test
    @Transactional
    public void getAllOrdersByPayTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        PayType payType = PayTypeResourceIntTest.createEntity(em);
        em.persist(payType);
        em.flush();
        order.setPayType(payType);
        orderRepository.saveAndFlush(order);
        Long payTypeId = payType.getId();

        // Get all the orderList where payType equals to payTypeId
        defaultOrderShouldBeFound("payTypeId.equals=" + payTypeId);

        // Get all the orderList where payType equals to payTypeId + 1
        defaultOrderShouldNotBeFound("payTypeId.equals=" + (payTypeId + 1));
    }


    @Test
    @Transactional
    public void getAllOrdersByOrderTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        OrderType orderType = OrderTypeResourceIntTest.createEntity(em);
        em.persist(orderType);
        em.flush();
        order.setOrderType(orderType);
        orderRepository.saveAndFlush(order);
        Long orderTypeId = orderType.getId();

        // Get all the orderList where orderType equals to orderTypeId
        defaultOrderShouldBeFound("orderTypeId.equals=" + orderTypeId);

        // Get all the orderList where orderType equals to orderTypeId + 1
        defaultOrderShouldNotBeFound("orderTypeId.equals=" + (orderTypeId + 1));
    }


    @Test
    @Transactional
    public void getAllOrdersByOrderStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        OrderStatus orderStatus = OrderStatusResourceIntTest.createEntity(em);
        em.persist(orderStatus);
        em.flush();
        order.setOrderStatus(orderStatus);
        orderRepository.saveAndFlush(order);
        Long orderStatusId = orderStatus.getId();

        // Get all the orderList where orderStatus equals to orderStatusId
        defaultOrderShouldBeFound("orderStatusId.equals=" + orderStatusId);

        // Get all the orderList where orderStatus equals to orderStatusId + 1
        defaultOrderShouldNotBeFound("orderStatusId.equals=" + (orderStatusId + 1));
    }


    @Test
    @Transactional
    public void getAllOrdersByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        Product product = ProductResourceIntTest.createEntity(em);
        em.persist(product);
        em.flush();
        order.setProduct(product);
        orderRepository.saveAndFlush(order);
        Long productId = product.getId();

        // Get all the orderList where product equals to productId
        defaultOrderShouldBeFound("productId.equals=" + productId);

        // Get all the orderList where product equals to productId + 1
        defaultOrderShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);
        orderSearchRepository.save(order);
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findOne(order.getId());
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC)
            .amount(UPDATED_AMOUNT)
            .createdDate(UPDATED_CREATED_DATE)
            .userId(UPDATED_USER_ID);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOrder.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testOrder.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrder.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrder.getUserId()).isEqualTo(UPDATED_USER_ID);

        // Validate the Order in Elasticsearch
        Order orderEs = orderSearchRepository.findOne(testOrder.getId());
        assertThat(orderEs).isEqualToComparingFieldByField(testOrder);
    }

    @Test
    @Transactional
    public void updateNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrderMockMvc.perform(put("/api/orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);
        orderSearchRepository.save(order);
        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Get the order
        restOrderMockMvc.perform(delete("/api/orders/{id}", order.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean orderExistsInEs = orderSearchRepository.exists(order.getId());
        assertThat(orderExistsInEs).isFalse();

        // Validate the database is empty
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);
        orderSearchRepository.save(order);

        // Search the order
        restOrderMockMvc.perform(get("/api/_search/orders?query=id:" + order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(sameInstant(DEFAULT_CREATED_DATE))))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);
        order2.setId(2L);
        assertThat(order1).isNotEqualTo(order2);
        order1.setId(null);
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderDTO.class);
        OrderDTO orderDTO1 = new OrderDTO();
        orderDTO1.setId(1L);
        OrderDTO orderDTO2 = new OrderDTO();
        assertThat(orderDTO1).isNotEqualTo(orderDTO2);
        orderDTO2.setId(orderDTO1.getId());
        assertThat(orderDTO1).isEqualTo(orderDTO2);
        orderDTO2.setId(2L);
        assertThat(orderDTO1).isNotEqualTo(orderDTO2);
        orderDTO1.setId(null);
        assertThat(orderDTO1).isNotEqualTo(orderDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(orderMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(orderMapper.fromId(null)).isNull();
    }
}
