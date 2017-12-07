package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.MerchantStatus;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.MerchantStatusRepository;
import com.cloud.distribution.service.MerchantStatusService;
import com.cloud.distribution.repository.search.MerchantStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantStatusDTO;
import com.cloud.distribution.service.mapper.MerchantStatusMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.MerchantStatusCriteria;
import com.cloud.distribution.service.MerchantStatusQueryService;

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
 * Test class for the MerchantStatusResource REST controller.
 *
 * @see MerchantStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class MerchantStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private MerchantStatusRepository merchantStatusRepository;

    @Autowired
    private MerchantStatusMapper merchantStatusMapper;

    @Autowired
    private MerchantStatusService merchantStatusService;

    @Autowired
    private MerchantStatusSearchRepository merchantStatusSearchRepository;

    @Autowired
    private MerchantStatusQueryService merchantStatusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMerchantStatusMockMvc;

    private MerchantStatus merchantStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MerchantStatusResource merchantStatusResource = new MerchantStatusResource(merchantStatusService, merchantStatusQueryService);
        this.restMerchantStatusMockMvc = MockMvcBuilders.standaloneSetup(merchantStatusResource)
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
    public static MerchantStatus createEntity(EntityManager em) {
        MerchantStatus merchantStatus = new MerchantStatus()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .code(DEFAULT_CODE);
        return merchantStatus;
    }

    @Before
    public void initTest() {
        merchantStatusSearchRepository.deleteAll();
        merchantStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createMerchantStatus() throws Exception {
        int databaseSizeBeforeCreate = merchantStatusRepository.findAll().size();

        // Create the MerchantStatus
        MerchantStatusDTO merchantStatusDTO = merchantStatusMapper.toDto(merchantStatus);
        restMerchantStatusMockMvc.perform(post("/api/merchant-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantStatus in the database
        List<MerchantStatus> merchantStatusList = merchantStatusRepository.findAll();
        assertThat(merchantStatusList).hasSize(databaseSizeBeforeCreate + 1);
        MerchantStatus testMerchantStatus = merchantStatusList.get(merchantStatusList.size() - 1);
        assertThat(testMerchantStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMerchantStatus.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testMerchantStatus.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the MerchantStatus in Elasticsearch
        MerchantStatus merchantStatusEs = merchantStatusSearchRepository.findOne(testMerchantStatus.getId());
        assertThat(merchantStatusEs).isEqualToComparingFieldByField(testMerchantStatus);
    }

    @Test
    @Transactional
    public void createMerchantStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = merchantStatusRepository.findAll().size();

        // Create the MerchantStatus with an existing ID
        merchantStatus.setId(1L);
        MerchantStatusDTO merchantStatusDTO = merchantStatusMapper.toDto(merchantStatus);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantStatusMockMvc.perform(post("/api/merchant-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantStatus in the database
        List<MerchantStatus> merchantStatusList = merchantStatusRepository.findAll();
        assertThat(merchantStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMerchantStatuses() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList
        restMerchantStatusMockMvc.perform(get("/api/merchant-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getMerchantStatus() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get the merchantStatus
        restMerchantStatusMockMvc.perform(get("/api/merchant-statuses/{id}", merchantStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(merchantStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where name equals to DEFAULT_NAME
        defaultMerchantStatusShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the merchantStatusList where name equals to UPDATED_NAME
        defaultMerchantStatusShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMerchantStatusShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the merchantStatusList where name equals to UPDATED_NAME
        defaultMerchantStatusShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where name is not null
        defaultMerchantStatusShouldBeFound("name.specified=true");

        // Get all the merchantStatusList where name is null
        defaultMerchantStatusShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where code equals to DEFAULT_CODE
        defaultMerchantStatusShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the merchantStatusList where code equals to UPDATED_CODE
        defaultMerchantStatusShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where code in DEFAULT_CODE or UPDATED_CODE
        defaultMerchantStatusShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the merchantStatusList where code equals to UPDATED_CODE
        defaultMerchantStatusShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);

        // Get all the merchantStatusList where code is not null
        defaultMerchantStatusShouldBeFound("code.specified=true");

        // Get all the merchantStatusList where code is null
        defaultMerchantStatusShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantStatusesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        merchantStatus.setStatus(status);
        merchantStatusRepository.saveAndFlush(merchantStatus);
        Long statusId = status.getId();

        // Get all the merchantStatusList where status equals to statusId
        defaultMerchantStatusShouldBeFound("statusId.equals=" + statusId);

        // Get all the merchantStatusList where status equals to statusId + 1
        defaultMerchantStatusShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMerchantStatusShouldBeFound(String filter) throws Exception {
        restMerchantStatusMockMvc.perform(get("/api/merchant-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMerchantStatusShouldNotBeFound(String filter) throws Exception {
        restMerchantStatusMockMvc.perform(get("/api/merchant-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMerchantStatus() throws Exception {
        // Get the merchantStatus
        restMerchantStatusMockMvc.perform(get("/api/merchant-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMerchantStatus() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);
        merchantStatusSearchRepository.save(merchantStatus);
        int databaseSizeBeforeUpdate = merchantStatusRepository.findAll().size();

        // Update the merchantStatus
        MerchantStatus updatedMerchantStatus = merchantStatusRepository.findOne(merchantStatus.getId());
        // Disconnect from session so that the updates on updatedMerchantStatus are not directly saved in db
        em.detach(updatedMerchantStatus);
        updatedMerchantStatus
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .code(UPDATED_CODE);
        MerchantStatusDTO merchantStatusDTO = merchantStatusMapper.toDto(updatedMerchantStatus);

        restMerchantStatusMockMvc.perform(put("/api/merchant-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantStatusDTO)))
            .andExpect(status().isOk());

        // Validate the MerchantStatus in the database
        List<MerchantStatus> merchantStatusList = merchantStatusRepository.findAll();
        assertThat(merchantStatusList).hasSize(databaseSizeBeforeUpdate);
        MerchantStatus testMerchantStatus = merchantStatusList.get(merchantStatusList.size() - 1);
        assertThat(testMerchantStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMerchantStatus.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testMerchantStatus.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the MerchantStatus in Elasticsearch
        MerchantStatus merchantStatusEs = merchantStatusSearchRepository.findOne(testMerchantStatus.getId());
        assertThat(merchantStatusEs).isEqualToComparingFieldByField(testMerchantStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingMerchantStatus() throws Exception {
        int databaseSizeBeforeUpdate = merchantStatusRepository.findAll().size();

        // Create the MerchantStatus
        MerchantStatusDTO merchantStatusDTO = merchantStatusMapper.toDto(merchantStatus);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMerchantStatusMockMvc.perform(put("/api/merchant-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantStatus in the database
        List<MerchantStatus> merchantStatusList = merchantStatusRepository.findAll();
        assertThat(merchantStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMerchantStatus() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);
        merchantStatusSearchRepository.save(merchantStatus);
        int databaseSizeBeforeDelete = merchantStatusRepository.findAll().size();

        // Get the merchantStatus
        restMerchantStatusMockMvc.perform(delete("/api/merchant-statuses/{id}", merchantStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean merchantStatusExistsInEs = merchantStatusSearchRepository.exists(merchantStatus.getId());
        assertThat(merchantStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<MerchantStatus> merchantStatusList = merchantStatusRepository.findAll();
        assertThat(merchantStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMerchantStatus() throws Exception {
        // Initialize the database
        merchantStatusRepository.saveAndFlush(merchantStatus);
        merchantStatusSearchRepository.save(merchantStatus);

        // Search the merchantStatus
        restMerchantStatusMockMvc.perform(get("/api/_search/merchant-statuses?query=id:" + merchantStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantStatus.class);
        MerchantStatus merchantStatus1 = new MerchantStatus();
        merchantStatus1.setId(1L);
        MerchantStatus merchantStatus2 = new MerchantStatus();
        merchantStatus2.setId(merchantStatus1.getId());
        assertThat(merchantStatus1).isEqualTo(merchantStatus2);
        merchantStatus2.setId(2L);
        assertThat(merchantStatus1).isNotEqualTo(merchantStatus2);
        merchantStatus1.setId(null);
        assertThat(merchantStatus1).isNotEqualTo(merchantStatus2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantStatusDTO.class);
        MerchantStatusDTO merchantStatusDTO1 = new MerchantStatusDTO();
        merchantStatusDTO1.setId(1L);
        MerchantStatusDTO merchantStatusDTO2 = new MerchantStatusDTO();
        assertThat(merchantStatusDTO1).isNotEqualTo(merchantStatusDTO2);
        merchantStatusDTO2.setId(merchantStatusDTO1.getId());
        assertThat(merchantStatusDTO1).isEqualTo(merchantStatusDTO2);
        merchantStatusDTO2.setId(2L);
        assertThat(merchantStatusDTO1).isNotEqualTo(merchantStatusDTO2);
        merchantStatusDTO1.setId(null);
        assertThat(merchantStatusDTO1).isNotEqualTo(merchantStatusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(merchantStatusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(merchantStatusMapper.fromId(null)).isNull();
    }
}
