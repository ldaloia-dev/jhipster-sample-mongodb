package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FooAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.repository.FooRepository;
import com.mycompany.myapp.service.dto.FooDTO;
import com.mycompany.myapp.service.mapper.FooMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link FooResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FooResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/foos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FooRepository fooRepository;

    @Autowired
    private FooMapper fooMapper;

    @Autowired
    private MockMvc restFooMockMvc;

    private Foo foo;

    private Foo insertedFoo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createEntity() {
        return new Foo().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).start(DEFAULT_START);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createUpdatedEntity() {
        return new Foo().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).start(UPDATED_START);
    }

    @BeforeEach
    void initTest() {
        foo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFoo != null) {
            fooRepository.delete(insertedFoo);
            insertedFoo = null;
        }
    }

    @Test
    void getAllFoosByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList where name equals to
        defaultFooFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllFoosByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList where name in
        defaultFooFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllFoosByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList where name is not null
        defaultFooFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllFoosByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList where name contains
        defaultFooFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllFoosByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList where name does not contain
        defaultFooFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    private void defaultFooFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFooShouldBeFound(shouldBeFound);
        defaultFooShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFooShouldBeFound(String filter) throws Exception {
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(foo.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFooShouldNotBeFound(String filter) throws Exception {
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    void createFoo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);
        var returnedFooDTO = om.readValue(
            restFooMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FooDTO.class
        );

        // Validate the Foo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFoo = fooMapper.toEntity(returnedFooDTO);
        assertFooUpdatableFieldsEquals(returnedFoo, getPersistedFoo(returnedFoo));

        insertedFoo = returnedFoo;
    }

    @Test
    void createFooWithExistingId() throws Exception {
        // Create the Foo with an existing ID
        foo.setId("existing_id");
        FooDTO fooDTO = fooMapper.toDto(foo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        foo.setName(null);

        // Create the Foo, which fails.
        FooDTO fooDTO = fooMapper.toDto(foo);

        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllFoos() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get all the fooList
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(foo.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].start").value(hasItem(DEFAULT_START.toString())));
    }

    @Test
    void getFoo() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        // Get the foo
        restFooMockMvc
            .perform(get(ENTITY_API_URL_ID, foo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(foo.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.start").value(DEFAULT_START.toString()));
    }

    @Test
    void getNonExistingFoo() throws Exception {
        // Get the foo
        restFooMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingFoo() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the foo
        Foo updatedFoo = fooRepository.findById(foo.getId()).orElseThrow();
        updatedFoo.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).start(UPDATED_START);
        FooDTO fooDTO = fooMapper.toDto(updatedFoo);

        restFooMockMvc
            .perform(put(ENTITY_API_URL_ID, fooDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isOk());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFooToMatchAllProperties(updatedFoo);
    }

    @Test
    void putNonExistingFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(put(ENTITY_API_URL_ID, fooDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fooDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFooWithPatch() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).start(UPDATED_START);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFooUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFoo, foo), getPersistedFoo(foo));
    }

    @Test
    void fullUpdateFooWithPatch() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).start(UPDATED_START);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFooUpdatableFieldsEquals(partialUpdatedFoo, getPersistedFoo(partialUpdatedFoo));
    }

    @Test
    void patchNonExistingFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fooDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fooDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fooDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFoo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        foo.setId(UUID.randomUUID().toString());

        // Create the Foo
        FooDTO fooDTO = fooMapper.toDto(foo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fooDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFoo() throws Exception {
        // Initialize the database
        insertedFoo = fooRepository.save(foo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the foo
        restFooMockMvc.perform(delete(ENTITY_API_URL_ID, foo.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fooRepository.count();
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

    protected Foo getPersistedFoo(Foo foo) {
        return fooRepository.findById(foo.getId()).orElseThrow();
    }

    protected void assertPersistedFooToMatchAllProperties(Foo expectedFoo) {
        assertFooAllPropertiesEquals(expectedFoo, getPersistedFoo(expectedFoo));
    }

    protected void assertPersistedFooToMatchUpdatableProperties(Foo expectedFoo) {
        assertFooAllUpdatablePropertiesEquals(expectedFoo, getPersistedFoo(expectedFoo));
    }
}
