package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.ProductStatus;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.ProductStatusRepository;
import com.cloud.distribution.service.ProductStatusService;
import com.cloud.distribution.repository.search.ProductStatusSearchRepository;
import com.cloud.distribution.service.dto.ProductStatusDTO;
import com.cloud.distribution.service.mapper.ProductStatusMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.ProductStatusCriteria;
import com.cloud.distribution.service.ProductStatusQueryService;

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
 * Test class for the ProductStatusResource REST controller.
 *
 * @see ProductStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class ProductStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private ProductStatusRepository productStatusRepository;

    @Autowired
    private ProductStatusMapper productStatusMapper;

    @Autowired
    private ProductStatusService productStatusService;

    @Autowired
    private ProductStatusSearchRepository productStatusSearchRepository;

    @Autowired
    private ProductStatusQueryService productStatusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProductStatusMockMvc;

    private ProductStatus productStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductStatusResource productStatusResource = new ProductStatusResource(productStatusService, productStatusQueryService);
        this.restProductStatusMockMvc = MockMvcBuilders.standaloneSetup(productStatusResource)
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
    public static ProductStatus createEntity(EntityManager em) {
        ProductStatus productStatus = new ProductStatus()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .code(DEFAULT_CODE);
        return productStatus;
    }

    @Before
    public void initTest() {
        productStatusSearchRepository.deleteAll();
        productStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductStatus() throws Exception {
        int databaseSizeBeforeCreate = productStatusRepository.findAll().size();

        // Create the ProductStatus
        ProductStatusDTO productStatusDTO = productStatusMapper.toDto(productStatus);
        restProductStatusMockMvc.perform(post("/api/product-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductStatus in the database
        List<ProductStatus> productStatusList = productStatusRepository.findAll();
        assertThat(productStatusList).hasSize(databaseSizeBeforeCreate + 1);
        ProductStatus testProductStatus = productStatusList.get(productStatusList.size() - 1);
        assertThat(testProductStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductStatus.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testProductStatus.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the ProductStatus in Elasticsearch
        ProductStatus productStatusEs = productStatusSearchRepository.findOne(testProductStatus.getId());
        assertThat(productStatusEs).isEqualToComparingFieldByField(testProductStatus);
    }

    @Test
    @Transactional
    public void createProductStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productStatusRepository.findAll().size();

        // Create the ProductStatus with an existing ID
        productStatus.setId(1L);
        ProductStatusDTO productStatusDTO = productStatusMapper.toDto(productStatus);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductStatusMockMvc.perform(post("/api/product-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProductStatus in the database
        List<ProductStatus> productStatusList = productStatusRepository.findAll();
        assertThat(productStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProductStatuses() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList
        restProductStatusMockMvc.perform(get("/api/product-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getProductStatus() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get the productStatus
        restProductStatusMockMvc.perform(get("/api/product-statuses/{id}", productStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(productStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllProductStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where name equals to DEFAULT_NAME
        defaultProductStatusShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the productStatusList where name equals to UPDATED_NAME
        defaultProductStatusShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where name in DEFAULT_NAME or UPDATED_NAME
        defaultProductStatusShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the productStatusList where name equals to UPDATED_NAME
        defaultProductStatusShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllProductStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where name is not null
        defaultProductStatusShouldBeFound("name.specified=true");

        // Get all the productStatusList where name is null
        defaultProductStatusShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductStatusesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where code equals to DEFAULT_CODE
        defaultProductStatusShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the productStatusList where code equals to UPDATED_CODE
        defaultProductStatusShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllProductStatusesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where code in DEFAULT_CODE or UPDATED_CODE
        defaultProductStatusShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the productStatusList where code equals to UPDATED_CODE
        defaultProductStatusShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllProductStatusesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);

        // Get all the productStatusList where code is not null
        defaultProductStatusShouldBeFound("code.specified=true");

        // Get all the productStatusList where code is null
        defaultProductStatusShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllProductStatusesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        productStatus.setStatus(status);
        productStatusRepository.saveAndFlush(productStatus);
        Long statusId = status.getId();

        // Get all the productStatusList where status equals to statusId
        defaultProductStatusShouldBeFound("statusId.equals=" + statusId);

        // Get all the productStatusList where status equals to statusId + 1
        defaultProductStatusShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProductStatusShouldBeFound(String filter) throws Exception {
        restProductStatusMockMvc.perform(get("/api/product-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProductStatusShouldNotBeFound(String filter) throws Exception {
        restProductStatusMockMvc.perform(get("/api/product-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingProductStatus() throws Exception {
        // Get the productStatus
        restProductStatusMockMvc.perform(get("/api/product-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductStatus() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);
        productStatusSearchRepository.save(productStatus);
        int databaseSizeBeforeUpdate = productStatusRepository.findAll().size();

        // Update the productStatus
        ProductStatus updatedProductStatus = productStatusRepository.findOne(productStatus.getId());
        // Disconnect from session so that the updates on updatedProductStatus are not directly saved in db
        em.detach(updatedProductStatus);
        updatedProductStatus
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .code(UPDATED_CODE);
        ProductStatusDTO productStatusDTO = productStatusMapper.toDto(updatedProductStatus);

        restProductStatusMockMvc.perform(put("/api/product-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productStatusDTO)))
            .andExpect(status().isOk());

        // Validate the ProductStatus in the database
        List<ProductStatus> productStatusList = productStatusRepository.findAll();
        assertThat(productStatusList).hasSize(databaseSizeBeforeUpdate);
        ProductStatus testProductStatus = productStatusList.get(productStatusList.size() - 1);
        assertThat(testProductStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductStatus.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testProductStatus.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the ProductStatus in Elasticsearch
        ProductStatus productStatusEs = productStatusSearchRepository.findOne(testProductStatus.getId());
        assertThat(productStatusEs).isEqualToComparingFieldByField(testProductStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingProductStatus() throws Exception {
        int databaseSizeBeforeUpdate = productStatusRepository.findAll().size();

        // Create the ProductStatus
        ProductStatusDTO productStatusDTO = productStatusMapper.toDto(productStatus);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProductStatusMockMvc.perform(put("/api/product-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the ProductStatus in the database
        List<ProductStatus> productStatusList = productStatusRepository.findAll();
        assertThat(productStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProductStatus() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);
        productStatusSearchRepository.save(productStatus);
        int databaseSizeBeforeDelete = productStatusRepository.findAll().size();

        // Get the productStatus
        restProductStatusMockMvc.perform(delete("/api/product-statuses/{id}", productStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean productStatusExistsInEs = productStatusSearchRepository.exists(productStatus.getId());
        assertThat(productStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<ProductStatus> productStatusList = productStatusRepository.findAll();
        assertThat(productStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProductStatus() throws Exception {
        // Initialize the database
        productStatusRepository.saveAndFlush(productStatus);
        productStatusSearchRepository.save(productStatus);

        // Search the productStatus
        restProductStatusMockMvc.perform(get("/api/_search/product-statuses?query=id:" + productStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductStatus.class);
        ProductStatus productStatus1 = new ProductStatus();
        productStatus1.setId(1L);
        ProductStatus productStatus2 = new ProductStatus();
        productStatus2.setId(productStatus1.getId());
        assertThat(productStatus1).isEqualTo(productStatus2);
        productStatus2.setId(2L);
        assertThat(productStatus1).isNotEqualTo(productStatus2);
        productStatus1.setId(null);
        assertThat(productStatus1).isNotEqualTo(productStatus2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductStatusDTO.class);
        ProductStatusDTO productStatusDTO1 = new ProductStatusDTO();
        productStatusDTO1.setId(1L);
        ProductStatusDTO productStatusDTO2 = new ProductStatusDTO();
        assertThat(productStatusDTO1).isNotEqualTo(productStatusDTO2);
        productStatusDTO2.setId(productStatusDTO1.getId());
        assertThat(productStatusDTO1).isEqualTo(productStatusDTO2);
        productStatusDTO2.setId(2L);
        assertThat(productStatusDTO1).isNotEqualTo(productStatusDTO2);
        productStatusDTO1.setId(null);
        assertThat(productStatusDTO1).isNotEqualTo(productStatusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(productStatusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(productStatusMapper.fromId(null)).isNull();
    }
}
