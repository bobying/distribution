package com.cloud.distribution.web.rest;

import com.cloud.distribution.DistributionApp;

import com.cloud.distribution.config.SecurityBeanOverrideConfiguration;

import com.cloud.distribution.domain.Merchant;
import com.cloud.distribution.domain.MerchantType;
import com.cloud.distribution.domain.MerchantAuditStatus;
import com.cloud.distribution.domain.MerchantStatus;
import com.cloud.distribution.domain.Merchant;
import com.cloud.distribution.repository.MerchantRepository;
import com.cloud.distribution.service.MerchantService;
import com.cloud.distribution.repository.search.MerchantSearchRepository;
import com.cloud.distribution.service.dto.MerchantDTO;
import com.cloud.distribution.service.mapper.MerchantMapper;
import com.cloud.distribution.web.rest.errors.ExceptionTranslator;
import com.cloud.distribution.service.dto.MerchantCriteria;
import com.cloud.distribution.service.MerchantQueryService;

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
 * Test class for the MerchantResource REST controller.
 *
 * @see MerchantResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DistributionApp.class, SecurityBeanOverrideConfiguration.class})
public class MerchantResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final Long DEFAULT_LEVEL = 1L;
    private static final Long UPDATED_LEVEL = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantSearchRepository merchantSearchRepository;

    @Autowired
    private MerchantQueryService merchantQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMerchantMockMvc;

    private Merchant merchant;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MerchantResource merchantResource = new MerchantResource(merchantService, merchantQueryService);
        this.restMerchantMockMvc = MockMvcBuilders.standaloneSetup(merchantResource)
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
    public static Merchant createEntity(EntityManager em) {
        Merchant merchant = new Merchant()
            .name(DEFAULT_NAME)
            .desc(DEFAULT_DESC)
            .level(DEFAULT_LEVEL)
            .userId(DEFAULT_USER_ID)
            .address(DEFAULT_ADDRESS)
            .mobile(DEFAULT_MOBILE);
        return merchant;
    }

    @Before
    public void initTest() {
        merchantSearchRepository.deleteAll();
        merchant = createEntity(em);
    }

    @Test
    @Transactional
    public void createMerchant() throws Exception {
        int databaseSizeBeforeCreate = merchantRepository.findAll().size();

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);
        restMerchantMockMvc.perform(post("/api/merchants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantDTO)))
            .andExpect(status().isCreated());

        // Validate the Merchant in the database
        List<Merchant> merchantList = merchantRepository.findAll();
        assertThat(merchantList).hasSize(databaseSizeBeforeCreate + 1);
        Merchant testMerchant = merchantList.get(merchantList.size() - 1);
        assertThat(testMerchant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMerchant.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testMerchant.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testMerchant.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testMerchant.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testMerchant.getMobile()).isEqualTo(DEFAULT_MOBILE);

        // Validate the Merchant in Elasticsearch
        Merchant merchantEs = merchantSearchRepository.findOne(testMerchant.getId());
        assertThat(merchantEs).isEqualToComparingFieldByField(testMerchant);
    }

    @Test
    @Transactional
    public void createMerchantWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = merchantRepository.findAll().size();

        // Create the Merchant with an existing ID
        merchant.setId(1L);
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantMockMvc.perform(post("/api/merchants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        List<Merchant> merchantList = merchantRepository.findAll();
        assertThat(merchantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMerchants() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList
        restMerchantMockMvc.perform(get("/api/merchants?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())));
    }

    @Test
    @Transactional
    public void getMerchant() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get the merchant
        restMerchantMockMvc.perform(get("/api/merchants/{id}", merchant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(merchant.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL.intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()));
    }

    @Test
    @Transactional
    public void getAllMerchantsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where name equals to DEFAULT_NAME
        defaultMerchantShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the merchantList where name equals to UPDATED_NAME
        defaultMerchantShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMerchantShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the merchantList where name equals to UPDATED_NAME
        defaultMerchantShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllMerchantsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where name is not null
        defaultMerchantShouldBeFound("name.specified=true");

        // Get all the merchantList where name is null
        defaultMerchantShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantsByLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where level equals to DEFAULT_LEVEL
        defaultMerchantShouldBeFound("level.equals=" + DEFAULT_LEVEL);

        // Get all the merchantList where level equals to UPDATED_LEVEL
        defaultMerchantShouldNotBeFound("level.equals=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    public void getAllMerchantsByLevelIsInShouldWork() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where level in DEFAULT_LEVEL or UPDATED_LEVEL
        defaultMerchantShouldBeFound("level.in=" + DEFAULT_LEVEL + "," + UPDATED_LEVEL);

        // Get all the merchantList where level equals to UPDATED_LEVEL
        defaultMerchantShouldNotBeFound("level.in=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    public void getAllMerchantsByLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where level is not null
        defaultMerchantShouldBeFound("level.specified=true");

        // Get all the merchantList where level is null
        defaultMerchantShouldNotBeFound("level.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantsByLevelIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where level greater than or equals to DEFAULT_LEVEL
        defaultMerchantShouldBeFound("level.greaterOrEqualThan=" + DEFAULT_LEVEL);

        // Get all the merchantList where level greater than or equals to UPDATED_LEVEL
        defaultMerchantShouldNotBeFound("level.greaterOrEqualThan=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    public void getAllMerchantsByLevelIsLessThanSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where level less than or equals to DEFAULT_LEVEL
        defaultMerchantShouldNotBeFound("level.lessThan=" + DEFAULT_LEVEL);

        // Get all the merchantList where level less than or equals to UPDATED_LEVEL
        defaultMerchantShouldBeFound("level.lessThan=" + UPDATED_LEVEL);
    }


    @Test
    @Transactional
    public void getAllMerchantsByUserIdIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where userId equals to DEFAULT_USER_ID
        defaultMerchantShouldBeFound("userId.equals=" + DEFAULT_USER_ID);

        // Get all the merchantList where userId equals to UPDATED_USER_ID
        defaultMerchantShouldNotBeFound("userId.equals=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMerchantsByUserIdIsInShouldWork() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultMerchantShouldBeFound("userId.in=" + DEFAULT_USER_ID + "," + UPDATED_USER_ID);

        // Get all the merchantList where userId equals to UPDATED_USER_ID
        defaultMerchantShouldNotBeFound("userId.in=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMerchantsByUserIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where userId is not null
        defaultMerchantShouldBeFound("userId.specified=true");

        // Get all the merchantList where userId is null
        defaultMerchantShouldNotBeFound("userId.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantsByUserIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where userId greater than or equals to DEFAULT_USER_ID
        defaultMerchantShouldBeFound("userId.greaterOrEqualThan=" + DEFAULT_USER_ID);

        // Get all the merchantList where userId greater than or equals to UPDATED_USER_ID
        defaultMerchantShouldNotBeFound("userId.greaterOrEqualThan=" + UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void getAllMerchantsByUserIdIsLessThanSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where userId less than or equals to DEFAULT_USER_ID
        defaultMerchantShouldNotBeFound("userId.lessThan=" + DEFAULT_USER_ID);

        // Get all the merchantList where userId less than or equals to UPDATED_USER_ID
        defaultMerchantShouldBeFound("userId.lessThan=" + UPDATED_USER_ID);
    }


    @Test
    @Transactional
    public void getAllMerchantsByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where address equals to DEFAULT_ADDRESS
        defaultMerchantShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the merchantList where address equals to UPDATED_ADDRESS
        defaultMerchantShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllMerchantsByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultMerchantShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the merchantList where address equals to UPDATED_ADDRESS
        defaultMerchantShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllMerchantsByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where address is not null
        defaultMerchantShouldBeFound("address.specified=true");

        // Get all the merchantList where address is null
        defaultMerchantShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantsByMobileIsEqualToSomething() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where mobile equals to DEFAULT_MOBILE
        defaultMerchantShouldBeFound("mobile.equals=" + DEFAULT_MOBILE);

        // Get all the merchantList where mobile equals to UPDATED_MOBILE
        defaultMerchantShouldNotBeFound("mobile.equals=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    public void getAllMerchantsByMobileIsInShouldWork() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where mobile in DEFAULT_MOBILE or UPDATED_MOBILE
        defaultMerchantShouldBeFound("mobile.in=" + DEFAULT_MOBILE + "," + UPDATED_MOBILE);

        // Get all the merchantList where mobile equals to UPDATED_MOBILE
        defaultMerchantShouldNotBeFound("mobile.in=" + UPDATED_MOBILE);
    }

    @Test
    @Transactional
    public void getAllMerchantsByMobileIsNullOrNotNull() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList where mobile is not null
        defaultMerchantShouldBeFound("mobile.specified=true");

        // Get all the merchantList where mobile is null
        defaultMerchantShouldNotBeFound("mobile.specified=false");
    }

    @Test
    @Transactional
    public void getAllMerchantsByMerchantTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        MerchantType merchantType = MerchantTypeResourceIntTest.createEntity(em);
        em.persist(merchantType);
        em.flush();
        merchant.setMerchantType(merchantType);
        merchantRepository.saveAndFlush(merchant);
        Long merchantTypeId = merchantType.getId();

        // Get all the merchantList where merchantType equals to merchantTypeId
        defaultMerchantShouldBeFound("merchantTypeId.equals=" + merchantTypeId);

        // Get all the merchantList where merchantType equals to merchantTypeId + 1
        defaultMerchantShouldNotBeFound("merchantTypeId.equals=" + (merchantTypeId + 1));
    }


    @Test
    @Transactional
    public void getAllMerchantsByMerchantAuditStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        MerchantAuditStatus merchantAuditStatus = MerchantAuditStatusResourceIntTest.createEntity(em);
        em.persist(merchantAuditStatus);
        em.flush();
        merchant.setMerchantAuditStatus(merchantAuditStatus);
        merchantRepository.saveAndFlush(merchant);
        Long merchantAuditStatusId = merchantAuditStatus.getId();

        // Get all the merchantList where merchantAuditStatus equals to merchantAuditStatusId
        defaultMerchantShouldBeFound("merchantAuditStatusId.equals=" + merchantAuditStatusId);

        // Get all the merchantList where merchantAuditStatus equals to merchantAuditStatusId + 1
        defaultMerchantShouldNotBeFound("merchantAuditStatusId.equals=" + (merchantAuditStatusId + 1));
    }


    @Test
    @Transactional
    public void getAllMerchantsByMerchantStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        MerchantStatus merchantStatus = MerchantStatusResourceIntTest.createEntity(em);
        em.persist(merchantStatus);
        em.flush();
        merchant.setMerchantStatus(merchantStatus);
        merchantRepository.saveAndFlush(merchant);
        Long merchantStatusId = merchantStatus.getId();

        // Get all the merchantList where merchantStatus equals to merchantStatusId
        defaultMerchantShouldBeFound("merchantStatusId.equals=" + merchantStatusId);

        // Get all the merchantList where merchantStatus equals to merchantStatusId + 1
        defaultMerchantShouldNotBeFound("merchantStatusId.equals=" + (merchantStatusId + 1));
    }


    @Test
    @Transactional
    public void getAllMerchantsByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        Merchant parent = MerchantResourceIntTest.createEntity(em);
        em.persist(parent);
        em.flush();
        merchant.setParent(parent);
        merchantRepository.saveAndFlush(merchant);
        Long parentId = parent.getId();

        // Get all the merchantList where parent equals to parentId
        defaultMerchantShouldBeFound("parentId.equals=" + parentId);

        // Get all the merchantList where parent equals to parentId + 1
        defaultMerchantShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultMerchantShouldBeFound(String filter) throws Exception {
        restMerchantMockMvc.perform(get("/api/merchants?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultMerchantShouldNotBeFound(String filter) throws Exception {
        restMerchantMockMvc.perform(get("/api/merchants?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingMerchant() throws Exception {
        // Get the merchant
        restMerchantMockMvc.perform(get("/api/merchants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMerchant() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);
        merchantSearchRepository.save(merchant);
        int databaseSizeBeforeUpdate = merchantRepository.findAll().size();

        // Update the merchant
        Merchant updatedMerchant = merchantRepository.findOne(merchant.getId());
        // Disconnect from session so that the updates on updatedMerchant are not directly saved in db
        em.detach(updatedMerchant);
        updatedMerchant
            .name(UPDATED_NAME)
            .desc(UPDATED_DESC)
            .level(UPDATED_LEVEL)
            .userId(UPDATED_USER_ID)
            .address(UPDATED_ADDRESS)
            .mobile(UPDATED_MOBILE);
        MerchantDTO merchantDTO = merchantMapper.toDto(updatedMerchant);

        restMerchantMockMvc.perform(put("/api/merchants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantDTO)))
            .andExpect(status().isOk());

        // Validate the Merchant in the database
        List<Merchant> merchantList = merchantRepository.findAll();
        assertThat(merchantList).hasSize(databaseSizeBeforeUpdate);
        Merchant testMerchant = merchantList.get(merchantList.size() - 1);
        assertThat(testMerchant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMerchant.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testMerchant.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testMerchant.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testMerchant.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testMerchant.getMobile()).isEqualTo(UPDATED_MOBILE);

        // Validate the Merchant in Elasticsearch
        Merchant merchantEs = merchantSearchRepository.findOne(testMerchant.getId());
        assertThat(merchantEs).isEqualToComparingFieldByField(testMerchant);
    }

    @Test
    @Transactional
    public void updateNonExistingMerchant() throws Exception {
        int databaseSizeBeforeUpdate = merchantRepository.findAll().size();

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMerchantMockMvc.perform(put("/api/merchants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(merchantDTO)))
            .andExpect(status().isCreated());

        // Validate the Merchant in the database
        List<Merchant> merchantList = merchantRepository.findAll();
        assertThat(merchantList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMerchant() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);
        merchantSearchRepository.save(merchant);
        int databaseSizeBeforeDelete = merchantRepository.findAll().size();

        // Get the merchant
        restMerchantMockMvc.perform(delete("/api/merchants/{id}", merchant.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean merchantExistsInEs = merchantSearchRepository.exists(merchant.getId());
        assertThat(merchantExistsInEs).isFalse();

        // Validate the database is empty
        List<Merchant> merchantList = merchantRepository.findAll();
        assertThat(merchantList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMerchant() throws Exception {
        // Initialize the database
        merchantRepository.saveAndFlush(merchant);
        merchantSearchRepository.save(merchant);

        // Search the merchant
        restMerchantMockMvc.perform(get("/api/_search/merchants?query=id:" + merchant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL.intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Merchant.class);
        Merchant merchant1 = new Merchant();
        merchant1.setId(1L);
        Merchant merchant2 = new Merchant();
        merchant2.setId(merchant1.getId());
        assertThat(merchant1).isEqualTo(merchant2);
        merchant2.setId(2L);
        assertThat(merchant1).isNotEqualTo(merchant2);
        merchant1.setId(null);
        assertThat(merchant1).isNotEqualTo(merchant2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MerchantDTO.class);
        MerchantDTO merchantDTO1 = new MerchantDTO();
        merchantDTO1.setId(1L);
        MerchantDTO merchantDTO2 = new MerchantDTO();
        assertThat(merchantDTO1).isNotEqualTo(merchantDTO2);
        merchantDTO2.setId(merchantDTO1.getId());
        assertThat(merchantDTO1).isEqualTo(merchantDTO2);
        merchantDTO2.setId(2L);
        assertThat(merchantDTO1).isNotEqualTo(merchantDTO2);
        merchantDTO1.setId(null);
        assertThat(merchantDTO1).isNotEqualTo(merchantDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(merchantMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(merchantMapper.fromId(null)).isNull();
    }
}
