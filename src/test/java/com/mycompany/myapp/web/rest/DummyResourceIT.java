package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DummyAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Dummy;
import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.repository.DummyRepository;
import com.mycompany.myapp.repository.FooRepository;
import com.mycompany.myapp.service.dto.DummyDTO;
import com.mycompany.myapp.service.mapper.DummyMapper;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link DummyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DummyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dummies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DummyRepository dummyRepository;

    @Autowired
    private FooRepository fooRepository;

    @Autowired
    private DummyMapper dummyMapper;

    @Autowired
    private MockMvc restDummyMockMvc;

    private Dummy dummy;

    private Dummy insertedDummy;

    private Foo insertedFoo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dummy createEntity() {
        return new Dummy().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dummy createUpdatedEntity() {
        return new Dummy().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        dummy = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDummy != null) {
            dummyRepository.delete(insertedDummy);
            insertedDummy = null;
        }
        if (insertedFoo != null) {
            fooRepository.delete(insertedFoo);
            insertedFoo = null;
        }
    }

    @Test
    void getAllDummiesByFooIsEqualToSomething() throws Exception {
        // Initialize the database
        Foo foo = FooResourceIT.createEntity();
        insertedFoo = fooRepository.save(foo);

        dummy.addFoo(foo);
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name equals to
        defaultDummyFiltering("fooId.equals=" + insertedFoo.getId(), "name.equals=" + UUID.randomUUID());
    }

    @Test
    void getAllDummiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name equals to
        defaultDummyFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllDummiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name in
        defaultDummyFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllDummiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name is not null
        defaultDummyFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllDummiesByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name contains
        defaultDummyFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllDummiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList where name does not contain
        defaultDummyFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    private void defaultDummyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDummyShouldBeFound(shouldBeFound);
        defaultDummyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDummyShouldBeFound(String filter) throws Exception {
        restDummyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dummy.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restDummyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDummyShouldNotBeFound(String filter) throws Exception {
        restDummyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDummyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    void createDummy() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);
        var returnedDummyDTO = om.readValue(
            restDummyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dummyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DummyDTO.class
        );

        // Validate the Dummy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDummy = dummyMapper.toEntity(returnedDummyDTO);
        assertDummyUpdatableFieldsEquals(returnedDummy, getPersistedDummy(returnedDummy));

        insertedDummy = returnedDummy;
    }

    @Test
    void createDummyWithExistingId() throws Exception {
        // Create the Dummy with an existing ID
        dummy.setId("existing_id");
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDummyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dummyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDummies() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get all the dummyList
        restDummyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dummy.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    void getDummy() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        // Get the dummy
        restDummyMockMvc
            .perform(get(ENTITY_API_URL_ID, dummy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dummy.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingDummy() throws Exception {
        // Get the dummy
        restDummyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingDummy() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dummy
        Dummy updatedDummy = dummyRepository.findById(dummy.getId()).orElseThrow();
        updatedDummy.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        DummyDTO dummyDTO = dummyMapper.toDto(updatedDummy);

        restDummyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dummyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dummyDTO))
            )
            .andExpect(status().isOk());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDummyToMatchAllProperties(updatedDummy);
    }

    @Test
    void putNonExistingDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dummyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dummyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dummyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dummyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDummyWithPatch() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dummy using partial update
        Dummy partialUpdatedDummy = new Dummy();
        partialUpdatedDummy.setId(dummy.getId());

        partialUpdatedDummy.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restDummyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDummy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDummy))
            )
            .andExpect(status().isOk());

        // Validate the Dummy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDummyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDummy, dummy), getPersistedDummy(dummy));
    }

    @Test
    void fullUpdateDummyWithPatch() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dummy using partial update
        Dummy partialUpdatedDummy = new Dummy();
        partialUpdatedDummy.setId(dummy.getId());

        partialUpdatedDummy.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restDummyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDummy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDummy))
            )
            .andExpect(status().isOk());

        // Validate the Dummy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDummyUpdatableFieldsEquals(partialUpdatedDummy, getPersistedDummy(partialUpdatedDummy));
    }

    @Test
    void patchNonExistingDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dummyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dummyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dummyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDummy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        dummy.setId(UUID.randomUUID().toString());

        // Create the Dummy
        DummyDTO dummyDTO = dummyMapper.toDto(dummy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDummyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dummyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dummy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDummy() throws Exception {
        // Initialize the database
        insertedDummy = dummyRepository.save(dummy);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dummy
        restDummyMockMvc
            .perform(delete(ENTITY_API_URL_ID, dummy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dummyRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Dummy getPersistedDummy(Dummy dummy) {
        return dummyRepository.findById(dummy.getId()).orElseThrow();
    }

    protected void assertPersistedDummyToMatchAllProperties(Dummy expectedDummy) {
        assertDummyAllPropertiesEquals(expectedDummy, getPersistedDummy(expectedDummy));
    }

    protected void assertPersistedDummyToMatchUpdatableProperties(Dummy expectedDummy) {
        assertDummyAllUpdatablePropertiesEquals(expectedDummy, getPersistedDummy(expectedDummy));
    }
}
