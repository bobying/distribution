package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.OperatorType;
import com.cloud.distribution.domain.Status;
import com.cloud.distribution.repository.OperatorTypeRepository;
import com.cloud.distribution.service.OperatorTypeService;
import com.cloud.distribution.repository.search.OperatorTypeSearchRepository;
import com.cloud.distribution.service.dto.OperatorTypeDTO;
import com.cloud.distribution.service.mapper.OperatorTypeMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.OperatorTypeCriteria;
import com.cloud.distribution.service.OperatorTypeQueryService;

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
 * Test class for the OperatorTypeResource REST controller.
 *
 * @see OperatorTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class OperatorTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private OperatorTypeRepository operatorTypeRepository;

    @Autowired
    private OperatorTypeMapper operatorTypeMapper;

    @Autowired
    private OperatorTypeService operatorTypeService;

    @Autowired
    private OperatorTypeSearchRepository operatorTypeSearchRepository;

    @Autowired
    private OperatorTypeQueryService operatorTypeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOperatorTypeMockMvc;

    private OperatorType operatorType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OperatorTypeResource operatorTypeResource = new OperatorTypeResource(operatorTypeService, operatorTypeQueryService);
        this.restOperatorTypeMockMvc = MockMvcBuilders.standaloneSetup(operatorTypeResource)
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
    public static OperatorType createEntity(EntityManager em) {
        OperatorType operatorType = new OperatorType()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .desc(DEFAULT_DESC);
        return operatorType;
    }

    @Before
    public void initTest() {
        operatorTypeSearchRepository.deleteAll();
        operatorType = createEntity(em);
    }

    @Test
    @Transactional
    public void createOperatorType() throws Exception {
        int databaseSizeBeforeCreate = operatorTypeRepository.findAll().size();

        // Create the OperatorType
        OperatorTypeDTO operatorTypeDTO = operatorTypeMapper.toDto(operatorType);
        restOperatorTypeMockMvc.perform(post("/api/operator-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operatorTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the OperatorType in the database
        List<OperatorType> operatorTypeList = operatorTypeRepository.findAll();
        assertThat(operatorTypeList).hasSize(databaseSizeBeforeCreate + 1);
        OperatorType testOperatorType = operatorTypeList.get(operatorTypeList.size() - 1);
        assertThat(testOperatorType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOperatorType.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOperatorType.getDesc()).isEqualTo(DEFAULT_DESC);

        // Validate the OperatorType in Elasticsearch
        OperatorType operatorTypeEs = operatorTypeSearchRepository.findOne(testOperatorType.getId());
        assertThat(operatorTypeEs).isEqualToComparingFieldByField(testOperatorType);
    }

    @Test
    @Transactional
    public void createOperatorTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = operatorTypeRepository.findAll().size();

        // Create the OperatorType with an existing ID
        operatorType.setId(1L);
        OperatorTypeDTO operatorTypeDTO = operatorTypeMapper.toDto(operatorType);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperatorTypeMockMvc.perform(post("/api/operator-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operatorTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OperatorType in the database
        List<OperatorType> operatorTypeList = operatorTypeRepository.findAll();
        assertThat(operatorTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOperatorTypes() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList
        restOperatorTypeMockMvc.perform(get("/api/operator-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operatorType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getOperatorType() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get the operatorType
        restOperatorTypeMockMvc.perform(get("/api/operator-types/{id}", operatorType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(operatorType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where name equals to DEFAULT_NAME
        defaultOperatorTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the operatorTypeList where name equals to UPDATED_NAME
        defaultOperatorTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultOperatorTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the operatorTypeList where name equals to UPDATED_NAME
        defaultOperatorTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where name is not null
        defaultOperatorTypeShouldBeFound("name.specified=true");

        // Get all the operatorTypeList where name is null
        defaultOperatorTypeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where code equals to DEFAULT_CODE
        defaultOperatorTypeShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the operatorTypeList where code equals to UPDATED_CODE
        defaultOperatorTypeShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where code in DEFAULT_CODE or UPDATED_CODE
        defaultOperatorTypeShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the operatorTypeList where code equals to UPDATED_CODE
        defaultOperatorTypeShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);

        // Get all the operatorTypeList where code is not null
        defaultOperatorTypeShouldBeFound("code.specified=true");

        // Get all the operatorTypeList where code is null
        defaultOperatorTypeShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllOperatorTypesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        Status status = StatusResourceIntTest.createEntity(em);
        em.persist(status);
        em.flush();
        operatorType.setStatus(status);
        operatorTypeRepository.saveAndFlush(operatorType);
        Long statusId = status.getId();

        // Get all the operatorTypeList where status equals to statusId
        defaultOperatorTypeShouldBeFound("statusId.equals=" + statusId);

        // Get all the operatorTypeList where status equals to statusId + 1
        defaultOperatorTypeShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultOperatorTypeShouldBeFound(String filter) throws Exception {
        restOperatorTypeMockMvc.perform(get("/api/operator-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operatorType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultOperatorTypeShouldNotBeFound(String filter) throws Exception {
        restOperatorTypeMockMvc.perform(get("/api/operator-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingOperatorType() throws Exception {
        // Get the operatorType
        restOperatorTypeMockMvc.perform(get("/api/operator-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOperatorType() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);
        operatorTypeSearchRepository.save(operatorType);
        int databaseSizeBeforeUpdate = operatorTypeRepository.findAll().size();

        // Update the operatorType
        OperatorType updatedOperatorType = operatorTypeRepository.findOne(operatorType.getId());
        // Disconnect from session so that the updates on updatedOperatorType are not directly saved in db
        em.detach(updatedOperatorType);
        updatedOperatorType
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .desc(UPDATED_DESC);
        OperatorTypeDTO operatorTypeDTO = operatorTypeMapper.toDto(updatedOperatorType);

        restOperatorTypeMockMvc.perform(put("/api/operator-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operatorTypeDTO)))
            .andExpect(status().isOk());

        // Validate the OperatorType in the database
        List<OperatorType> operatorTypeList = operatorTypeRepository.findAll();
        assertThat(operatorTypeList).hasSize(databaseSizeBeforeUpdate);
        OperatorType testOperatorType = operatorTypeList.get(operatorTypeList.size() - 1);
        assertThat(testOperatorType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOperatorType.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOperatorType.getDesc()).isEqualTo(UPDATED_DESC);

        // Validate the OperatorType in Elasticsearch
        OperatorType operatorTypeEs = operatorTypeSearchRepository.findOne(testOperatorType.getId());
        assertThat(operatorTypeEs).isEqualToComparingFieldByField(testOperatorType);
    }

    @Test
    @Transactional
    public void updateNonExistingOperatorType() throws Exception {
        int databaseSizeBeforeUpdate = operatorTypeRepository.findAll().size();

        // Create the OperatorType
        OperatorTypeDTO operatorTypeDTO = operatorTypeMapper.toDto(operatorType);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOperatorTypeMockMvc.perform(put("/api/operator-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(operatorTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the OperatorType in the database
        List<OperatorType> operatorTypeList = operatorTypeRepository.findAll();
        assertThat(operatorTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOperatorType() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);
        operatorTypeSearchRepository.save(operatorType);
        int databaseSizeBeforeDelete = operatorTypeRepository.findAll().size();

        // Get the operatorType
        restOperatorTypeMockMvc.perform(delete("/api/operator-types/{id}", operatorType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean operatorTypeExistsInEs = operatorTypeSearchRepository.exists(operatorType.getId());
        assertThat(operatorTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<OperatorType> operatorTypeList = operatorTypeRepository.findAll();
        assertThat(operatorTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOperatorType() throws Exception {
        // Initialize the database
        operatorTypeRepository.saveAndFlush(operatorType);
        operatorTypeSearchRepository.save(operatorType);

        // Search the operatorType
        restOperatorTypeMockMvc.perform(get("/api/_search/operator-types?query=id:" + operatorType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operatorType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperatorType.class);
        OperatorType operatorType1 = new OperatorType();
        operatorType1.setId(1L);
        OperatorType operatorType2 = new OperatorType();
        operatorType2.setId(operatorType1.getId());
        assertThat(operatorType1).isEqualTo(operatorType2);
        operatorType2.setId(2L);
        assertThat(operatorType1).isNotEqualTo(operatorType2);
        operatorType1.setId(null);
        assertThat(operatorType1).isNotEqualTo(operatorType2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperatorTypeDTO.class);
        OperatorTypeDTO operatorTypeDTO1 = new OperatorTypeDTO();
        operatorTypeDTO1.setId(1L);
        OperatorTypeDTO operatorTypeDTO2 = new OperatorTypeDTO();
        assertThat(operatorTypeDTO1).isNotEqualTo(operatorTypeDTO2);
        operatorTypeDTO2.setId(operatorTypeDTO1.getId());
        assertThat(operatorTypeDTO1).isEqualTo(operatorTypeDTO2);
        operatorTypeDTO2.setId(2L);
        assertThat(operatorTypeDTO1).isNotEqualTo(operatorTypeDTO2);
        operatorTypeDTO1.setId(null);
        assertThat(operatorTypeDTO1).isNotEqualTo(operatorTypeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(operatorTypeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(operatorTypeMapper.fromId(null)).isNull();
    }
}
