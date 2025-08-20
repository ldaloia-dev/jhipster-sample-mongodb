package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FooRepository;
import com.mycompany.myapp.service.FooQueryService;
import com.mycompany.myapp.service.FooService;
import com.mycompany.myapp.service.criteria.FooCriteria;
import com.mycompany.myapp.service.dto.FooDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Foo}.
 */
@RestController
@RequestMapping("/api/foos")
public class FooResource {

    private static final Logger LOG = LoggerFactory.getLogger(FooResource.class);

    private static final String ENTITY_NAME = "foo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FooService fooService;

    private final FooRepository fooRepository;

    private final FooQueryService fooQueryService;

    public FooResource(FooService fooService, FooRepository fooRepository, FooQueryService fooQueryService) {
        this.fooService = fooService;
        this.fooRepository = fooRepository;
        this.fooQueryService = fooQueryService;
    }

    /**
     * {@code POST  /foos} : Create a new foo.
     *
     * @param fooDTO the fooDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fooDTO, or with status {@code 400 (Bad Request)} if the foo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FooDTO> createFoo(@Valid @RequestBody FooDTO fooDTO) throws URISyntaxException {
        LOG.debug("REST request to save Foo : {}", fooDTO);
        if (fooDTO.getId() != null) {
            throw new BadRequestAlertException("A new foo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fooDTO = fooService.save(fooDTO);
        return ResponseEntity.created(new URI("/api/foos/" + fooDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fooDTO.getId()))
            .body(fooDTO);
    }

    /**
     * {@code PUT  /foos/:id} : Updates an existing foo.
     *
     * @param id the id of the fooDTO to save.
     * @param fooDTO the fooDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fooDTO,
     * or with status {@code 400 (Bad Request)} if the fooDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fooDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FooDTO> updateFoo(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FooDTO fooDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Foo : {}, {}", id, fooDTO);
        if (fooDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fooDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fooRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fooDTO = fooService.update(fooDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fooDTO.getId()))
            .body(fooDTO);
    }

    /**
     * {@code PATCH  /foos/:id} : Partial updates given fields of an existing foo, field will ignore if it is null
     *
     * @param id the id of the fooDTO to save.
     * @param fooDTO the fooDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fooDTO,
     * or with status {@code 400 (Bad Request)} if the fooDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fooDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fooDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FooDTO> partialUpdateFoo(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FooDTO fooDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Foo partially : {}, {}", id, fooDTO);
        if (fooDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fooDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fooRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FooDTO> result = fooService.partialUpdate(fooDTO);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fooDTO.getId()));
    }

    /**
     * {@code GET  /foos} : get all the foos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of foos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FooDTO>> getAllFoos(
        FooCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Foos by criteria: {}", criteria);

        Page<FooDTO> page = fooQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /foos/count} : count all the dummies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParameters(FooCriteria criteria) {
        LOG.debug("REST request to count Foos by criteria: {}", criteria);
        return ResponseEntity.ok().body(fooQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /foos/:id} : get the "id" foo.
     *
     * @param id the id of the fooDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fooDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FooDTO> getFoo(@PathVariable("id") String id) {
        LOG.debug("REST request to get Foo : {}", id);
        Optional<FooDTO> fooDTO = fooService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fooDTO);
    }

    /**
     * {@code DELETE  /foos/:id} : delete the "id" foo.
     *
     * @param id the id of the fooDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoo(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Foo : {}", id);
        fooService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
