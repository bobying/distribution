package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.PayType;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.PayTypeRepository;
import com.cloud.distribution.service.PayTypeService;
import com.cloud.distribution.repository.search.PayTypeSearchRepository;
import com.cloud.distribution.service.dto.PayTypeDTO;
import com.cloud.distribution.service.mapper.PayTypeMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.PayTypeCriteria;
import com.cloud.distribution.service.PayTypeQueryService;

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
 * Test class for the PayTypeResource REST controller.
 *
 * @see PayTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class PayTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private PayTypeRepository payTypeRepository;

    @Autowired
    private PayTypeMapper payTypeMapper;

    @Autowired
    private PayTypeService payTypeService;

    @Autowired
    private PayTypeSearchRepository payTypeSearchRepository;

    @Autowired
    private PayTypeQueryService payTypeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPayTypeMockMvc;

    private PayType payType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PayTypeResource payTypeResource = new PayTypeResource(payTypeService, payTypeQueryService);
        this.restPayTypeMockMvc = MockMvcBuilders.standaloneSetup(payTypeResource)
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
    public static PayType createEntity(EntityManager em) {
        PayType payType = new PayType()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC);
        return payType;
    }

    @Before
    public void initTest() {
        payTypeSearchRepository.deleteAll();
        payType = createEntity(em);
    }

    @Test
    @Transactional
    public void createPayType() throws Exception {
        int databaseSizeBeforeCreate = payTypeRepository.findAll().size();

        // Create the PayType
        PayTypeDTO payTypeDTO = payTypeMapper.toDto(payType);
        restPayTypeMockMvc.perform(post("/api/pay-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(payTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the PayType in the database
        List<PayType> payTypeList = payTypeRepository.findAll();
        assertThat(payTypeList).hasSize(databaseSizeBeforeCreate + 1);
        PayType testPayType = payTypeList.get(payTypeList.size() - 1);
        assertThat(testPayType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPayType.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testPayType.getDesc()).isEqualTo(DEFAULT_DESC);

        // Validate the PayType in Elasticsearch
        PayType payTypeEs = payTypeSearchRepository.findOne(testPayType.getId());
        assertThat(payTypeEs).isEqualToComparingFieldByField(testPayType);
    }

    @Test
    @Transactional
    public void createPayTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = payTypeRepository.findAll().size();

        // Create the PayType with an existing ID
        payType.setId(1L);
        PayTypeDTO payTypeDTO = payTypeMapper.toDto(payType);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPayTypeMockMvc.perform(post("/api/pay-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(payTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PayType in the database
        List<PayType> payTypeList = payTypeRepository.findAll();
        assertThat(payTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllPayTypes() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList
        restPayTypeMockMvc.perform(get("/api/pay-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getPayType() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get the payType
        restPayTypeMockMvc.perform(get("/api/pay-types/{id}", payType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(payType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllPayTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where name equals to DEFAULT_NAME
        defaultPayTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the payTypeList where name equals to UPDATED_NAME
        defaultPayTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPayTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPayTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the payTypeList where name equals to UPDATED_NAME
        defaultPayTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPayTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where name is not null
        defaultPayTypeShouldBeFound("name.specified=true");

        // Get all the payTypeList where name is null
        defaultPayTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPayTypesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where code equals to DEFAULT_CODE
        defaultPayTypeShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the payTypeList where code equals to UPDATED_CODE
        defaultPayTypeShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllPayTypesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where code in DEFAULT_CODE or UPDATED_CODE
        defaultPayTypeShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the payTypeList where code equals to UPDATED_CODE
        defaultPayTypeShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllPayTypesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);

        // Get all the payTypeList where code is not null
        defaultPayTypeShouldBeFound("code.specified=true");

        // Get all the payTypeList where code is null
        defaultPayTypeShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllPayTypesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        payType.setStatus(status);
        payTypeRepository.saveAndFlush(payType);
        Long statusId = status.getId();

        // Get all the payTypeList where status equals to statusId
        defaultPayTypeShouldBeFound("statusId.equals=" + statusId);

        // Get all the payTypeList where status equals to statusId + 1
        defaultPayTypeShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPayTypeShouldBeFound(String filter) throws Exception {
        restPayTypeMockMvc.perform(get("/api/pay-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPayTypeShouldNotBeFound(String filter) throws Exception {
        restPayTypeMockMvc.perform(get("/api/pay-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingPayType() throws Exception {
        // Get the payType
        restPayTypeMockMvc.perform(get("/api/pay-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePayType() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);
        payTypeSearchRepository.save(payType);
        int databaseSizeBeforeUpdate = payTypeRepository.findAll().size();

        // Update the payType
        PayType updatedPayType = payTypeRepository.findOne(payType.getId());
        // Disconnect from session so that the updates on updatedPayType are not directly saved in db
        em.detach(updatedPayType);
        updatedPayType
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC);
        PayTypeDTO payTypeDTO = payTypeMapper.toDto(updatedPayType);

        restPayTypeMockMvc.perform(put("/api/pay-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(payTypeDTO)))
            .andExpect(status().isOk());

        // Validate the PayType in the database
        List<PayType> payTypeList = payTypeRepository.findAll();
        assertThat(payTypeList).hasSize(databaseSizeBeforeUpdate);
        PayType testPayType = payTypeList.get(payTypeList.size() - 1);
        assertThat(testPayType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPayType.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testPayType.getDesc()).isEqualTo(UPDATED_DESC);

        // Validate the PayType in Elasticsearch
        PayType payTypeEs = payTypeSearchRepository.findOne(testPayType.getId());
        assertThat(payTypeEs).isEqualToComparingFieldByField(testPayType);
    }

    @Test
    @Transactional
    public void updateNonExistingPayType() throws Exception {
        int databaseSizeBeforeUpdate = payTypeRepository.findAll().size();

        // Create the PayType
        PayTypeDTO payTypeDTO = payTypeMapper.toDto(payType);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPayTypeMockMvc.perform(put("/api/pay-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(payTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the PayType in the database
        List<PayType> payTypeList = payTypeRepository.findAll();
        assertThat(payTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePayType() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);
        payTypeSearchRepository.save(payType);
        int databaseSizeBeforeDelete = payTypeRepository.findAll().size();

        // Get the payType
        restPayTypeMockMvc.perform(delete("/api/pay-types/{id}", payType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean payTypeExistsInEs = payTypeSearchRepository.exists(payType.getId());
        assertThat(payTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<PayType> payTypeList = payTypeRepository.findAll();
        assertThat(payTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPayType() throws Exception {
        // Initialize the database
        payTypeRepository.saveAndFlush(payType);
        payTypeSearchRepository.save(payType);

        // Search the payType
        restPayTypeMockMvc.perform(get("/api/_search/pay-types?query=id:" + payType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayType.class);
        PayType payType1 = new PayType();
        payType1.setId(1L);
        PayType payType2 = new PayType();
        payType2.setId(payType1.getId());
        assertThat(payType1).isEqualTo(payType2);
        payType2.setId(2L);
        assertThat(payType1).isNotEqualTo(payType2);
        payType1.setId(null);
        assertThat(payType1).isNotEqualTo(payType2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PayTypeDTO.class);
        PayTypeDTO payTypeDTO1 = new PayTypeDTO();
        payTypeDTO1.setId(1L);
        PayTypeDTO payTypeDTO2 = new PayTypeDTO();
        assertThat(payTypeDTO1).isNotEqualTo(payTypeDTO2);
        payTypeDTO2.setId(payTypeDTO1.getId());
        assertThat(payTypeDTO1).isEqualTo(payTypeDTO2);
        payTypeDTO2.setId(2L);
        assertThat(payTypeDTO1).isNotEqualTo(payTypeDTO2);
        payTypeDTO1.setId(null);
        assertThat(payTypeDTO1).isNotEqualTo(payTypeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(payTypeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(payTypeMapper.fromId(null)).isNull();
    }
}
