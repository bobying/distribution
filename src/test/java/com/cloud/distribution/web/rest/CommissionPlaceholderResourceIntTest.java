package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.CommissionPlaceholder;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.CommissionPlaceholderRepository;
import com.cloud.distribution.service.CommissionPlaceholderService;
import com.cloud.distribution.repository.search.CommissionPlaceholderSearchRepository;
import com.cloud.distribution.service.dto.CommissionPlaceholderDTO;
import com.cloud.distribution.service.mapper.CommissionPlaceholderMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.CommissionPlaceholderCriteria;
import com.cloud.distribution.service.CommissionPlaceholderQueryService;

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
 * Test class for the CommissionPlaceholderResource REST controller.
 *
 * @see CommissionPlaceholderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class CommissionPlaceholderResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private CommissionPlaceholderRepository commissionPlaceholderRepository;

    @Autowired
    private CommissionPlaceholderMapper commissionPlaceholderMapper;

    @Autowired
    private CommissionPlaceholderService commissionPlaceholderService;

    @Autowired
    private CommissionPlaceholderSearchRepository commissionPlaceholderSearchRepository;

    @Autowired
    private CommissionPlaceholderQueryService commissionPlaceholderQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCommissionPlaceholderMockMvc;

    private CommissionPlaceholder commissionPlaceholder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CommissionPlaceholderResource commissionPlaceholderResource = new CommissionPlaceholderResource(commissionPlaceholderService, commissionPlaceholderQueryService);
        this.restCommissionPlaceholderMockMvc = MockMvcBuilders.standaloneSetup(commissionPlaceholderResource)
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
    public static CommissionPlaceholder createEntity(EntityManager em) {
        CommissionPlaceholder commissionPlaceholder = new CommissionPlaceholder()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC);
        return commissionPlaceholder;
    }

    @Before
    public void initTest() {
        commissionPlaceholderSearchRepository.deleteAll();
        commissionPlaceholder = createEntity(em);
    }

    @Test
    @Transactional
    public void createCommissionPlaceholder() throws Exception {
        int databaseSizeBeforeCreate = commissionPlaceholderRepository.findAll().size();

        // Create the CommissionPlaceholder
        CommissionPlaceholderDTO commissionPlaceholderDTO = commissionPlaceholderMapper.toDto(commissionPlaceholder);
        restCommissionPlaceholderMockMvc.perform(post("/api/commission-placeholders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commissionPlaceholderDTO)))
            .andExpect(status().isCreated());

        // Validate the CommissionPlaceholder in the database
        List<CommissionPlaceholder> commissionPlaceholderList = commissionPlaceholderRepository.findAll();
        assertThat(commissionPlaceholderList).hasSize(databaseSizeBeforeCreate + 1);
        CommissionPlaceholder testCommissionPlaceholder = commissionPlaceholderList.get(commissionPlaceholderList.size() - 1);
        assertThat(testCommissionPlaceholder.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCommissionPlaceholder.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCommissionPlaceholder.getDesc()).isEqualTo(DEFAULT_DESC);

        // Validate the CommissionPlaceholder in Elasticsearch
        CommissionPlaceholder commissionPlaceholderEs = commissionPlaceholderSearchRepository.findOne(testCommissionPlaceholder.getId());
        assertThat(commissionPlaceholderEs).isEqualToComparingFieldByField(testCommissionPlaceholder);
    }

    @Test
    @Transactional
    public void createCommissionPlaceholderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = commissionPlaceholderRepository.findAll().size();

        // Create the CommissionPlaceholder with an existing ID
        commissionPlaceholder.setId(1L);
        CommissionPlaceholderDTO commissionPlaceholderDTO = commissionPlaceholderMapper.toDto(commissionPlaceholder);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommissionPlaceholderMockMvc.perform(post("/api/commission-placeholders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commissionPlaceholderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CommissionPlaceholder in the database
        List<CommissionPlaceholder> commissionPlaceholderList = commissionPlaceholderRepository.findAll();
        assertThat(commissionPlaceholderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholders() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList
        restCommissionPlaceholderMockMvc.perform(get("/api/commission-placeholders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commissionPlaceholder.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getCommissionPlaceholder() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get the commissionPlaceholder
        restCommissionPlaceholderMockMvc.perform(get("/api/commission-placeholders/{id}", commissionPlaceholder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(commissionPlaceholder.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where name equals to DEFAULT_NAME
        defaultCommissionPlaceholderShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the commissionPlaceholderList where name equals to UPDATED_NAME
        defaultCommissionPlaceholderShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCommissionPlaceholderShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the commissionPlaceholderList where name equals to UPDATED_NAME
        defaultCommissionPlaceholderShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where name is not null
        defaultCommissionPlaceholderShouldBeFound("name.specified=true");

        // Get all the commissionPlaceholderList where name is null
        defaultCommissionPlaceholderShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where code equals to DEFAULT_CODE
        defaultCommissionPlaceholderShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the commissionPlaceholderList where code equals to UPDATED_CODE
        defaultCommissionPlaceholderShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where code in DEFAULT_CODE or UPDATED_CODE
        defaultCommissionPlaceholderShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the commissionPlaceholderList where code equals to UPDATED_CODE
        defaultCommissionPlaceholderShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);

        // Get all the commissionPlaceholderList where code is not null
        defaultCommissionPlaceholderShouldBeFound("code.specified=true");

        // Get all the commissionPlaceholderList where code is null
        defaultCommissionPlaceholderShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllCommissionPlaceholdersByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        commissionPlaceholder.setStatus(status);
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);
        Long statusId = status.getId();

        // Get all the commissionPlaceholderList where status equals to statusId
        defaultCommissionPlaceholderShouldBeFound("statusId.equals=" + statusId);

        // Get all the commissionPlaceholderList where status equals to statusId + 1
        defaultCommissionPlaceholderShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultCommissionPlaceholderShouldBeFound(String filter) throws Exception {
        restCommissionPlaceholderMockMvc.perform(get("/api/commission-placeholders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commissionPlaceholder.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultCommissionPlaceholderShouldNotBeFound(String filter) throws Exception {
        restCommissionPlaceholderMockMvc.perform(get("/api/commission-placeholders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingCommissionPlaceholder() throws Exception {
        // Get the commissionPlaceholder
        restCommissionPlaceholderMockMvc.perform(get("/api/commission-placeholders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCommissionPlaceholder() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);
        commissionPlaceholderSearchRepository.save(commissionPlaceholder);
        int databaseSizeBeforeUpdate = commissionPlaceholderRepository.findAll().size();

        // Update the commissionPlaceholder
        CommissionPlaceholder updatedCommissionPlaceholder = commissionPlaceholderRepository.findOne(commissionPlaceholder.getId());
        // Disconnect from session so that the updates on updatedCommissionPlaceholder are not directly saved in db
        em.detach(updatedCommissionPlaceholder);
        updatedCommissionPlaceholder
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC);
        CommissionPlaceholderDTO commissionPlaceholderDTO = commissionPlaceholderMapper.toDto(updatedCommissionPlaceholder);

        restCommissionPlaceholderMockMvc.perform(put("/api/commission-placeholders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commissionPlaceholderDTO)))
            .andExpect(status().isOk());

        // Validate the CommissionPlaceholder in the database
        List<CommissionPlaceholder> commissionPlaceholderList = commissionPlaceholderRepository.findAll();
        assertThat(commissionPlaceholderList).hasSize(databaseSizeBeforeUpdate);
        CommissionPlaceholder testCommissionPlaceholder = commissionPlaceholderList.get(commissionPlaceholderList.size() - 1);
        assertThat(testCommissionPlaceholder.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCommissionPlaceholder.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCommissionPlaceholder.getDesc()).isEqualTo(UPDATED_DESC);

        // Validate the CommissionPlaceholder in Elasticsearch
        CommissionPlaceholder commissionPlaceholderEs = commissionPlaceholderSearchRepository.findOne(testCommissionPlaceholder.getId());
        assertThat(commissionPlaceholderEs).isEqualToComparingFieldByField(testCommissionPlaceholder);
    }

    @Test
    @Transactional
    public void updateNonExistingCommissionPlaceholder() throws Exception {
        int databaseSizeBeforeUpdate = commissionPlaceholderRepository.findAll().size();

        // Create the CommissionPlaceholder
        CommissionPlaceholderDTO commissionPlaceholderDTO = commissionPlaceholderMapper.toDto(commissionPlaceholder);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCommissionPlaceholderMockMvc.perform(put("/api/commission-placeholders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(commissionPlaceholderDTO)))
            .andExpect(status().isCreated());

        // Validate the CommissionPlaceholder in the database
        List<CommissionPlaceholder> commissionPlaceholderList = commissionPlaceholderRepository.findAll();
        assertThat(commissionPlaceholderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCommissionPlaceholder() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);
        commissionPlaceholderSearchRepository.save(commissionPlaceholder);
        int databaseSizeBeforeDelete = commissionPlaceholderRepository.findAll().size();

        // Get the commissionPlaceholder
        restCommissionPlaceholderMockMvc.perform(delete("/api/commission-placeholders/{id}", commissionPlaceholder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean commissionPlaceholderExistsInEs = commissionPlaceholderSearchRepository.exists(commissionPlaceholder.getId());
        assertThat(commissionPlaceholderExistsInEs).isFalse();

        // Validate the database is empty
        List<CommissionPlaceholder> commissionPlaceholderList = commissionPlaceholderRepository.findAll();
        assertThat(commissionPlaceholderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCommissionPlaceholder() throws Exception {
        // Initialize the database
        commissionPlaceholderRepository.saveAndFlush(commissionPlaceholder);
        commissionPlaceholderSearchRepository.save(commissionPlaceholder);

        // Search the commissionPlaceholder
        restCommissionPlaceholderMockMvc.perform(get("/api/_search/commission-placeholders?query=id:" + commissionPlaceholder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commissionPlaceholder.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommissionPlaceholder.class);
        CommissionPlaceholder commissionPlaceholder1 = new CommissionPlaceholder();
        commissionPlaceholder1.setId(1L);
        CommissionPlaceholder commissionPlaceholder2 = new CommissionPlaceholder();
        commissionPlaceholder2.setId(commissionPlaceholder1.getId());
        assertThat(commissionPlaceholder1).isEqualTo(commissionPlaceholder2);
        commissionPlaceholder2.setId(2L);
        assertThat(commissionPlaceholder1).isNotEqualTo(commissionPlaceholder2);
        commissionPlaceholder1.setId(null);
        assertThat(commissionPlaceholder1).isNotEqualTo(commissionPlaceholder2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommissionPlaceholderDTO.class);
        CommissionPlaceholderDTO commissionPlaceholderDTO1 = new CommissionPlaceholderDTO();
        commissionPlaceholderDTO1.setId(1L);
        CommissionPlaceholderDTO commissionPlaceholderDTO2 = new CommissionPlaceholderDTO();
        assertThat(commissionPlaceholderDTO1).isNotEqualTo(commissionPlaceholderDTO2);
        commissionPlaceholderDTO2.setId(commissionPlaceholderDTO1.getId());
        assertThat(commissionPlaceholderDTO1).isEqualTo(commissionPlaceholderDTO2);
        commissionPlaceholderDTO2.setId(2L);
        assertThat(commissionPlaceholderDTO1).isNotEqualTo(commissionPlaceholderDTO2);
        commissionPlaceholderDTO1.setId(null);
        assertThat(commissionPlaceholderDTO1).isNotEqualTo(commissionPlaceholderDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(commissionPlaceholderMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(commissionPlaceholderMapper.fromId(null)).isNull();
    }
}
