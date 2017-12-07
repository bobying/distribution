package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.MerchantAuditStatus;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.MerchantAuditStatusRepository;
import com.cloud.distribution.service.MerchantAuditStatusService;
import com.cloud.distribution.repository.search.MerchantAuditStatusSearchRepository;
import com.cloud.distribution.service.dto.MerchantAuditStatusDTO;
import com.cloud.distribution.service.mapper.MerchantAuditStatusMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.MerchantAuditStatusCriteria;
import com.cloud.distribution.service.MerchantAuditStatusQueryService;

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
 * Test class for the MerchantAuditStatusResource REST controller.
 *
 * @see MerchantAuditStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class MerchantAuditStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private MerchantAuditStatusRepository merchantAuditStatusRepository;

    @Autowired
    private MerchantAuditStatusMapper merchantAuditStatusMapper;

    @Autowired
    private MerchantAuditStatusService merchantAuditStatusService;

    @Autowired
    private MerchantAuditStatusSearchRepository merchantAuditStatusSearchRepository;

    @Autowired
    private MerchantAuditStatusQueryService merchantAuditStatusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMerchantAuditStatusMockMvc;

    private MerchantAuditStatus merchantAuditStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MerchantAuditStatusResource merchantAuditStatusResource = new MerchantAuditStatusResource(merchantAuditStatusService, merchantAuditStatusQueryService);
        this.restMerchantAuditStatusMockMvc = MockMvcBuilders.standaloneSetup(merchantAuditStatusResource)
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
    public static MerchantAuditStatus createEntity(EntityManager em) {
        MerchantAuditStatus merchantAuditStatus = new MerchantAuditStatus()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .code(DEFAULT_CODE);
        return merchantAuditStatus;
    }

    @Before
    public void initTest() {
        merchantAuditStatusSearchRepository.deleteAll();
        merchantAuditStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createMerchantAuditStatus() throws Exception {
        int databaseSizeBeforeCreate = merchantAuditStatusRepository.findAll().size();

        // Create the MerchantAuditStatus
        MerchantAuditStatusDTO merchantAuditStatusDTO = merchantAuditStatusMapper.toDto(merchantAuditStatus);
        restMerchantAuditStatusMockMvc.perform(post("/api/merchant-audit-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantAuditStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantAuditStatus in the database
        List<MerchantAuditStatus> merchantAuditStatusList = merchantAuditStatusRepository.findAll();
        assertThat(merchantAuditStatusList).hasSize(databaseSizeBeforeCreate + 1);
        MerchantAuditStatus testMerchantAuditStatus = merchantAuditStatusList.get(merchantAuditStatusList.size() - 1);
        assertThat(testMerchantAuditStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMerchantAuditStatus.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testMerchantAuditStatus.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the MerchantAuditStatus in Elasticsearch
        MerchantAuditStatus merchantAuditStatusEs = merchantAuditStatusSearchRepository.findOne(testMerchantAuditStatus.getId());
        assertThat(merchantAuditStatusEs).isEqualToComparingFieldByField(testMerchantAuditStatus);
    }

    @Test
    @Transactional
    public void createMerchantAuditStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = merchantAuditStatusRepository.findAll().size();

        // Create the MerchantAuditStatus with an existing ID
        merchantAuditStatus.setId(1L);
        MerchantAuditStatusDTO merchantAuditStatusDTO = merchantAuditStatusMapper.toDto(merchantAuditStatus);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantAuditStatusMockMvc.perform(post("/api/merchant-audit-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantAuditStatusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantAuditStatus in the database
        List<MerchantAuditStatus> merchantAuditStatusList = merchantAuditStatusRepository.findAll();
        assertThat(merchantAuditStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatuses() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList
        restMerchantAuditStatusMockMvc.perform(get("/api/merchant-audit-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantAuditStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getMerchantAuditStatus() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get the merchantAuditStatus
        restMerchantAuditStatusMockMvc.perform(get("/api/merchant-audit-statuses/{id}", merchantAuditStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(merchantAuditStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where name equals to DEFAULT_NAME
        defaultMerchantAuditStatusShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the merchantAuditStatusList where name equals to UPDATED_NAME
        defaultMerchantAuditStatusShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMerchantAuditStatusShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the merchantAuditStatusList where name equals to UPDATED_NAME
        defaultMerchantAuditStatusShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where name is not null
        defaultMerchantAuditStatusShouldBeFound("name.specified=true");

        // Get all the merchantAuditStatusList where name is null
        defaultMerchantAuditStatusShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where code equals to DEFAULT_CODE
        defaultMerchantAuditStatusShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the merchantAuditStatusList where code equals to UPDATED_CODE
        defaultMerchantAuditStatusShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where code in DEFAULT_CODE or UPDATED_CODE
        defaultMerchantAuditStatusShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the merchantAuditStatusList where code equals to UPDATED_CODE
        defaultMerchantAuditStatusShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);

        // Get all the merchantAuditStatusList where code is not null
        defaultMerchantAuditStatusShouldBeFound("code.specified=true");

        // Get all the merchantAuditStatusList where code is null
        defaultMerchantAuditStatusShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantAuditStatusesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        merchantAuditStatus.setStatus(status);
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);
        Long statusId = status.getId();

        // Get all the merchantAuditStatusList where status equals to statusId
        defaultMerchantAuditStatusShouldBeFound("statusId.equals=" + statusId);

        // Get all the merchantAuditStatusList where status equals to statusId + 1
        defaultMerchantAuditStatusShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMerchantAuditStatusShouldBeFound(String filter) throws Exception {
        restMerchantAuditStatusMockMvc.perform(get("/api/merchant-audit-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantAuditStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMerchantAuditStatusShouldNotBeFound(String filter) throws Exception {
        restMerchantAuditStatusMockMvc.perform(get("/api/merchant-audit-statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMerchantAuditStatus() throws Exception {
        // Get the merchantAuditStatus
        restMerchantAuditStatusMockMvc.perform(get("/api/merchant-audit-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMerchantAuditStatus() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);
        merchantAuditStatusSearchRepository.save(merchantAuditStatus);
        int databaseSizeBeforeUpdate = merchantAuditStatusRepository.findAll().size();

        // Update the merchantAuditStatus
        MerchantAuditStatus updatedMerchantAuditStatus = merchantAuditStatusRepository.findOne(merchantAuditStatus.getId());
        // Disconnect from session so that the updates on updatedMerchantAuditStatus are not directly saved in db
        em.detach(updatedMerchantAuditStatus);
        updatedMerchantAuditStatus
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .code(UPDATED_CODE);
        MerchantAuditStatusDTO merchantAuditStatusDTO = merchantAuditStatusMapper.toDto(updatedMerchantAuditStatus);

        restMerchantAuditStatusMockMvc.perform(put("/api/merchant-audit-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantAuditStatusDTO)))
            .andExpect(status().isOk());

        // Validate the MerchantAuditStatus in the database
        List<MerchantAuditStatus> merchantAuditStatusList = merchantAuditStatusRepository.findAll();
        assertThat(merchantAuditStatusList).hasSize(databaseSizeBeforeUpdate);
        MerchantAuditStatus testMerchantAuditStatus = merchantAuditStatusList.get(merchantAuditStatusList.size() - 1);
        assertThat(testMerchantAuditStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMerchantAuditStatus.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testMerchantAuditStatus.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the MerchantAuditStatus in Elasticsearch
        MerchantAuditStatus merchantAuditStatusEs = merchantAuditStatusSearchRepository.findOne(testMerchantAuditStatus.getId());
        assertThat(merchantAuditStatusEs).isEqualToComparingFieldByField(testMerchantAuditStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingMerchantAuditStatus() throws Exception {
        int databaseSizeBeforeUpdate = merchantAuditStatusRepository.findAll().size();

        // Create the MerchantAuditStatus
        MerchantAuditStatusDTO merchantAuditStatusDTO = merchantAuditStatusMapper.toDto(merchantAuditStatus);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMerchantAuditStatusMockMvc.perform(put("/api/merchant-audit-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantAuditStatusDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantAuditStatus in the database
        List<MerchantAuditStatus> merchantAuditStatusList = merchantAuditStatusRepository.findAll();
        assertThat(merchantAuditStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMerchantAuditStatus() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);
        merchantAuditStatusSearchRepository.save(merchantAuditStatus);
        int databaseSizeBeforeDelete = merchantAuditStatusRepository.findAll().size();

        // Get the merchantAuditStatus
        restMerchantAuditStatusMockMvc.perform(delete("/api/merchant-audit-statuses/{id}", merchantAuditStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean merchantAuditStatusExistsInEs = merchantAuditStatusSearchRepository.exists(merchantAuditStatus.getId());
        assertThat(merchantAuditStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<MerchantAuditStatus> merchantAuditStatusList = merchantAuditStatusRepository.findAll();
        assertThat(merchantAuditStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMerchantAuditStatus() throws Exception {
        // Initialize the database
        merchantAuditStatusRepository.saveAndFlush(merchantAuditStatus);
        merchantAuditStatusSearchRepository.save(merchantAuditStatus);

        // Search the merchantAuditStatus
        restMerchantAuditStatusMockMvc.perform(get("/api/_search/merchant-audit-statuses?query=id:" + merchantAuditStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantAuditStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantAuditStatus.class);
        MerchantAuditStatus merchantAuditStatus1 = new MerchantAuditStatus();
        merchantAuditStatus1.setId(1L);
        MerchantAuditStatus merchantAuditStatus2 = new MerchantAuditStatus();
        merchantAuditStatus2.setId(merchantAuditStatus1.getId());
        assertThat(merchantAuditStatus1).isEqualTo(merchantAuditStatus2);
        merchantAuditStatus2.setId(2L);
        assertThat(merchantAuditStatus1).isNotEqualTo(merchantAuditStatus2);
        merchantAuditStatus1.setId(null);
        assertThat(merchantAuditStatus1).isNotEqualTo(merchantAuditStatus2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantAuditStatusDTO.class);
        MerchantAuditStatusDTO merchantAuditStatusDTO1 = new MerchantAuditStatusDTO();
        merchantAuditStatusDTO1.setId(1L);
        MerchantAuditStatusDTO merchantAuditStatusDTO2 = new MerchantAuditStatusDTO();
        assertThat(merchantAuditStatusDTO1).isNotEqualTo(merchantAuditStatusDTO2);
        merchantAuditStatusDTO2.setId(merchantAuditStatusDTO1.getId());
        assertThat(merchantAuditStatusDTO1).isEqualTo(merchantAuditStatusDTO2);
        merchantAuditStatusDTO2.setId(2L);
        assertThat(merchantAuditStatusDTO1).isNotEqualTo(merchantAuditStatusDTO2);
        merchantAuditStatusDTO1.setId(null);
        assertThat(merchantAuditStatusDTO1).isNotEqualTo(merchantAuditStatusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(merchantAuditStatusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(merchantAuditStatusMapper.fromId(null)).isNull();
    }
}
