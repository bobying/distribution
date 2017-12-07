package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.MerchantType;
import com.cloud.distribution.repository.MerchantTypeRepository;
import com.cloud.distribution.service.MerchantTypeService;
import com.cloud.distribution.repository.search.MerchantTypeSearchRepository;
import com.cloud.distribution.service.dto.MerchantTypeDTO;
import com.cloud.distribution.service.mapper.MerchantTypeMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.MerchantTypeCriteria;
import com.cloud.distribution.service.MerchantTypeQueryService;

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
 * Test class for the MerchantTypeResource REST controller.
 *
 * @see MerchantTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class MerchantTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    @Autowired
    private MerchantTypeRepository merchantTypeRepository;

    @Autowired
    private MerchantTypeMapper merchantTypeMapper;

    @Autowired
    private MerchantTypeService merchantTypeService;

    @Autowired
    private MerchantTypeSearchRepository merchantTypeSearchRepository;

    @Autowired
    private MerchantTypeQueryService merchantTypeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMerchantTypeMockMvc;

    private MerchantType merchantType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MerchantTypeResource merchantTypeResource = new MerchantTypeResource(merchantTypeService, merchantTypeQueryService);
        this.restMerchantTypeMockMvc = MockMvcBuilders.standaloneSetup(merchantTypeResource)
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
    public static MerchantType createEntity(EntityManager em) {
        MerchantType merchantType = new MerchantType()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC);
        return merchantType;
    }

    @Before
    public void initTest() {
        merchantTypeSearchRepository.deleteAll();
        merchantType = createEntity(em);
    }

    @Test
    @Transactional
    public void createMerchantType() throws Exception {
        int databaseSizeBeforeCreate = merchantTypeRepository.findAll().size();

        // Create the MerchantType
        MerchantTypeDTO merchantTypeDTO = merchantTypeMapper.toDto(merchantType);
        restMerchantTypeMockMvc.perform(post("/api/merchant-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantType in the database
        List<MerchantType> merchantTypeList = merchantTypeRepository.findAll();
        assertThat(merchantTypeList).hasSize(databaseSizeBeforeCreate + 1);
        MerchantType testMerchantType = merchantTypeList.get(merchantTypeList.size() - 1);
        assertThat(testMerchantType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMerchantType.getDesc()).isEqualTo(DEFAULT_DESC);

        // Validate the MerchantType in Elasticsearch
        MerchantType merchantTypeEs = merchantTypeSearchRepository.findOne(testMerchantType.getId());
        assertThat(merchantTypeEs).isEqualToComparingFieldByField(testMerchantType);
    }

    @Test
    @Transactional
    public void createMerchantTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = merchantTypeRepository.findAll().size();

        // Create the MerchantType with an existing ID
        merchantType.setId(1L);
        MerchantTypeDTO merchantTypeDTO = merchantTypeMapper.toDto(merchantType);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantTypeMockMvc.perform(post("/api/merchant-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantType in the database
        List<MerchantType> merchantTypeList = merchantTypeRepository.findAll();
        assertThat(merchantTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMerchantTypes() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);

        // Get all the merchantTypeList
        restMerchantTypeMockMvc.perform(get("/api/merchant-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getMerchantType() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);

        // Get the merchantType
        restMerchantTypeMockMvc.perform(get("/api/merchant-types/{id}", merchantType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(merchantType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getAllMerchantTypesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);

        // Get all the merchantTypeList where name equals to DEFAULT_NAME
        defaultMerchantTypeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the merchantTypeList where name equals to UPDATED_NAME
        defaultMerchantTypeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantTypesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);

        // Get all the merchantTypeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMerchantTypeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the merchantTypeList where name equals to UPDATED_NAME
        defaultMerchantTypeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantTypesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);

        // Get all the merchantTypeList where name is not null
        defaultMerchantTypeShouldBeFound("name.specified=true");

        // Get all the merchantTypeList where name is null
        defaultMerchantTypeShouldNotBeFound("name.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMerchantTypeShouldBeFound(String filter) throws Exception {
        restMerchantTypeMockMvc.perform(get("/api/merchant-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMerchantTypeShouldNotBeFound(String filter) throws Exception {
        restMerchantTypeMockMvc.perform(get("/api/merchant-types?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMerchantType() throws Exception {
        // Get the merchantType
        restMerchantTypeMockMvc.perform(get("/api/merchant-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMerchantType() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);
        merchantTypeSearchRepository.save(merchantType);
        int databaseSizeBeforeUpdate = merchantTypeRepository.findAll().size();

        // Update the merchantType
        MerchantType updatedMerchantType = merchantTypeRepository.findOne(merchantType.getId());
        // Disconnect from session so that the updates on updatedMerchantType are not directly saved in db
        em.detach(updatedMerchantType);
        updatedMerchantType
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC);
        MerchantTypeDTO merchantTypeDTO = merchantTypeMapper.toDto(updatedMerchantType);

        restMerchantTypeMockMvc.perform(put("/api/merchant-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantTypeDTO)))
            .andExpect(status().isOk());

        // Validate the MerchantType in the database
        List<MerchantType> merchantTypeList = merchantTypeRepository.findAll();
        assertThat(merchantTypeList).hasSize(databaseSizeBeforeUpdate);
        MerchantType testMerchantType = merchantTypeList.get(merchantTypeList.size() - 1);
        assertThat(testMerchantType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMerchantType.getDesc()).isEqualTo(UPDATED_DESC);

        // Validate the MerchantType in Elasticsearch
        MerchantType merchantTypeEs = merchantTypeSearchRepository.findOne(testMerchantType.getId());
        assertThat(merchantTypeEs).isEqualToComparingFieldByField(testMerchantType);
    }

    @Test
    @Transactional
    public void updateNonExistingMerchantType() throws Exception {
        int databaseSizeBeforeUpdate = merchantTypeRepository.findAll().size();

        // Create the MerchantType
        MerchantTypeDTO merchantTypeDTO = merchantTypeMapper.toDto(merchantType);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMerchantTypeMockMvc.perform(put("/api/merchant-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantTypeDTO)))
            .andExpect(status().isCreated());

        // Validate the MerchantType in the database
        List<MerchantType> merchantTypeList = merchantTypeRepository.findAll();
        assertThat(merchantTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMerchantType() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);
        merchantTypeSearchRepository.save(merchantType);
        int databaseSizeBeforeDelete = merchantTypeRepository.findAll().size();

        // Get the merchantType
        restMerchantTypeMockMvc.perform(delete("/api/merchant-types/{id}", merchantType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean merchantTypeExistsInEs = merchantTypeSearchRepository.exists(merchantType.getId());
        assertThat(merchantTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<MerchantType> merchantTypeList = merchantTypeRepository.findAll();
        assertThat(merchantTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMerchantType() throws Exception {
        // Initialize the database
        merchantTypeRepository.saveAndFlush(merchantType);
        merchantTypeSearchRepository.save(merchantType);

        // Search the merchantType
        restMerchantTypeMockMvc.perform(get("/api/_search/merchant-types?query=id:" + merchantType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantType.class);
        MerchantType merchantType1 = new MerchantType();
        merchantType1.setId(1L);
        MerchantType merchantType2 = new MerchantType();
        merchantType2.setId(merchantType1.getId());
        assertThat(merchantType1).isEqualTo(merchantType2);
        merchantType2.setId(2L);
        assertThat(merchantType1).isNotEqualTo(merchantType2);
        merchantType1.setId(null);
        assertThat(merchantType1).isNotEqualTo(merchantType2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantTypeDTO.class);
        MerchantTypeDTO merchantTypeDTO1 = new MerchantTypeDTO();
        merchantTypeDTO1.setId(1L);
        MerchantTypeDTO merchantTypeDTO2 = new MerchantTypeDTO();
        assertThat(merchantTypeDTO1).isNotEqualTo(merchantTypeDTO2);
        merchantTypeDTO2.setId(merchantTypeDTO1.getId());
        assertThat(merchantTypeDTO1).isEqualTo(merchantTypeDTO2);
        merchantTypeDTO2.setId(2L);
        assertThat(merchantTypeDTO1).isNotEqualTo(merchantTypeDTO2);
        merchantTypeDTO1.setId(null);
        assertThat(merchantTypeDTO1).isNotEqualTo(merchantTypeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(merchantTypeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(merchantTypeMapper.fromId(null)).isNull();
    }
}
