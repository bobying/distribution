package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.StatusRepository;
import com.cloud.distribution.service.StatusService;
import com.cloud.distribution.repository.search.StatusSearchRepository;
import com.cloud.distribution.service.dto.StatusDTO;
import com.cloud.distribution.service.mapper.StatusMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.StatusCriteria;
import com.cloud.distribution.service.StatusQueryService;

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

import javax.persistence.EntityManager;
import java.util.List;

import static com.cloud.distribution.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StatusResource REST controller.
 *
 * @see StatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class StatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private StatusMapper statusMapper;

    @Autowired
    private StatusService statusService;

    @Autowired
    private StatusSearchRepository statusSearchRepository;

    @Autowired
    private StatusQueryService statusQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restStatusMockMvc;

    private Status status;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final StatusResource statusResource = new StatusResource(statusService, statusQueryService);
        this.restStatusMockMvc = MockMvcBuilders.standaloneSetup(statusResource)
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
    public static Status createEntity(EntityManager em) {
        Status status = new Status()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE);
        return status;
    }

    @Before
    public void initTest() {
        statusSearchRepository.deleteAll();
        status = createEntity(em);
    }

    @Test
    @Transactional
    public void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);
        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate + 1);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStatus.getCode()).isEqualTo(DEFAULT_CODE);

        // Validate the Status in Elasticsearch
        Status statusEs = statusSearchRepository.findOne(testStatus.getId());
        assertThat(statusEs).isEqualToComparingFieldByField(testStatus);
    }

    @Test
    @Transactional
    public void createStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status with an existing ID
        status.setId(1L);
        StatusDTO statusDTO = statusMapper.toDto(status);

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatusMockMvc.perform(post("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList
        restStatusMockMvc.perform(get("/api/statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllStatusesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where name equals to DEFAULT_NAME
        defaultStatusShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the statusList where name equals to UPDATED_NAME
        defaultStatusShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStatusesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where name in DEFAULT_NAME or UPDATED_NAME
        defaultStatusShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the statusList where name equals to UPDATED_NAME
        defaultStatusShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllStatusesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where name is not null
        defaultStatusShouldBeFound("name.specified=true");

        // Get all the statusList where name is null
        defaultStatusShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllStatusesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where code equals to DEFAULT_CODE
        defaultStatusShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the statusList where code equals to UPDATED_CODE
        defaultStatusShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllStatusesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where code in DEFAULT_CODE or UPDATED_CODE
        defaultStatusShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the statusList where code equals to UPDATED_CODE
        defaultStatusShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllStatusesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statusList where code is not null
        defaultStatusShouldBeFound("code.specified=true");

        // Get all the statusList where code is null
        defaultStatusShouldNotBeFound("code.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultStatusShouldBeFound(String filter) throws Exception {
        restStatusMockMvc.perform(get("/api/statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultStatusShouldNotBeFound(String filter) throws Exception {
        restStatusMockMvc.perform(get("/api/statuses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status
        Status updatedStatus = statusRepository.findOne(status.getId());
        // Disconnect from session so that the updates on updatedStatus are not directly saved in db
        em.detach(updatedStatus);
        updatedStatus
            .name(UPDATED_NAME)
            .code(UPDATED_CODE);
        StatusDTO statusDTO = statusMapper.toDto(updatedStatus);

        restStatusMockMvc.perform(put("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statusList.get(statusList.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStatus.getCode()).isEqualTo(UPDATED_CODE);

        // Validate the Status in Elasticsearch
        Status statusEs = statusSearchRepository.findOne(testStatus.getId());
        assertThat(statusEs).isEqualToComparingFieldByField(testStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingStatus() throws Exception {
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Create the Status
        StatusDTO statusDTO = statusMapper.toDto(status);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restStatusMockMvc.perform(put("/api/statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(statusDTO)))
            .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);
        int databaseSizeBeforeDelete = statusRepository.findAll().size();

        // Get the status
        restStatusMockMvc.perform(delete("/api/statuses/{id}", status.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean statusExistsInEs = statusSearchRepository.exists(status.getId());
        assertThat(statusExistsInEs).isFalse();

        // Validate the database is empty
        List<Status> statusList = statusRepository.findAll();
        assertThat(statusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);

        // Search the status
        restStatusMockMvc.perform(get("/api/_search/statuses?query=id:" + status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Status.class);
        Status status1 = new Status();
        status1.setId(1L);
        Status status2 = new Status();
        status2.setId(status1.getId());
        assertThat(status1).isEqualTo(status2);
        status2.setId(2L);
        assertThat(status1).isNotEqualTo(status2);
        status1.setId(null);
        assertThat(status1).isNotEqualTo(status2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StatusDTO.class);
        StatusDTO statusDTO1 = new StatusDTO();
        statusDTO1.setId(1L);
        StatusDTO statusDTO2 = new StatusDTO();
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
        statusDTO2.setId(statusDTO1.getId());
        assertThat(statusDTO1).isEqualTo(statusDTO2);
        statusDTO2.setId(2L);
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
        statusDTO1.setId(null);
        assertThat(statusDTO1).isNotEqualTo(statusDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(statusMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(statusMapper.fromId(null)).isNull();
    }
}
