package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DummyRepository;
import com.mycompany.myapp.service.DummyQueryService;
import com.mycompany.myapp.service.DummyService;
import com.mycompany.myapp.service.criteria.DummyCriteria;
import com.mycompany.myapp.service.dto.DummyDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Dummy}.
 */
@RestController
@RequestMapping("/api/dummies")
public class DummyResource {

    private static final Logger LOG = LoggerFactory.getLogger(DummyResource.class);

    private static final String ENTITY_NAME = "dummy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DummyService dummyService;

    private final DummyRepository dummyRepository;

    private final DummyQueryService dummyQueryService;

    public DummyResource(DummyService dummyService, DummyRepository dummyRepository, DummyQueryService dummyQueryService) {
        this.dummyService = dummyService;
        this.dummyRepository = dummyRepository;
        this.dummyQueryService = dummyQueryService;
    }

    /**
     * {@code POST  /dummies} : Create a new dummy.
     *
     * @param dummyDTO the dummyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dummyDTO, or with status {@code 400 (Bad Request)} if the dummy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DummyDTO> createDummy(@RequestBody DummyDTO dummyDTO) throws URISyntaxException {
        LOG.debug("REST request to save Dummy : {}", dummyDTO);
        if (dummyDTO.getId() != null) {
            throw new BadRequestAlertException("A new dummy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dummyDTO = dummyService.save(dummyDTO);
        return ResponseEntity.created(new URI("/api/dummies/" + dummyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dummyDTO.getId()))
            .body(dummyDTO);
    }

    /**
     * {@code PUT  /dummies/:id} : Updates an existing dummy.
     *
     * @param id the id of the dummyDTO to save.
     * @param dummyDTO the dummyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dummyDTO,
     * or with status {@code 400 (Bad Request)} if the dummyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dummyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DummyDTO> updateDummy(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody DummyDTO dummyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Dummy : {}, {}", id, dummyDTO);
        if (dummyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dummyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dummyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dummyDTO = dummyService.update(dummyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dummyDTO.getId()))
            .body(dummyDTO);
    }

    /**
     * {@code PATCH  /dummies/:id} : Partial updates given fields of an existing dummy, field will ignore if it is null
     *
     * @param id the id of the dummyDTO to save.
     * @param dummyDTO the dummyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dummyDTO,
     * or with status {@code 400 (Bad Request)} if the dummyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dummyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dummyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DummyDTO> partialUpdateDummy(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody DummyDTO dummyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Dummy partially : {}, {}", id, dummyDTO);
        if (dummyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dummyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dummyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DummyDTO> result = dummyService.partialUpdate(dummyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dummyDTO.getId())
        );
    }

    /**
     * {@code GET  /dummies} : get all the dummies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dummies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DummyDTO>> getAllDummies(
        DummyCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Dummies by criteria: {}", criteria);

        Page<DummyDTO> page = dummyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /dummies/count} : count all the dummies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDummies(DummyCriteria criteria) {
        LOG.debug("REST request to count Dummies by criteria: {}", criteria);
        return ResponseEntity.ok().body(dummyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dummies/:id} : get the "id" dummy.
     *
     * @param id the id of the dummyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dummyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DummyDTO> getDummy(@PathVariable("id") String id) {
        LOG.debug("REST request to get Dummy : {}", id);
        Optional<DummyDTO> dummyDTO = dummyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dummyDTO);
    }

    /**
     * {@code DELETE  /dummies/:id} : delete the "id" dummy.
     *
     * @param id the id of the dummyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDummy(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Dummy : {}", id);
        dummyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
