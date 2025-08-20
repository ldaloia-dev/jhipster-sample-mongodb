package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Dummy;
import com.mycompany.myapp.repository.DummyRepository;
import com.mycompany.myapp.service.dto.DummyDTO;
import com.mycompany.myapp.service.mapper.DummyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Dummy}.
 */
@Service
public class DummyService {

    private static final Logger LOG = LoggerFactory.getLogger(DummyService.class);

    private final DummyRepository dummyRepository;

    private final DummyMapper dummyMapper;

    public DummyService(DummyRepository dummyRepository, DummyMapper dummyMapper) {
        this.dummyRepository = dummyRepository;
        this.dummyMapper = dummyMapper;
    }

    /**
     * Save a dummy.
     *
     * @param dummyDTO the entity to save.
     * @return the persisted entity.
     */
    public DummyDTO save(DummyDTO dummyDTO) {
        LOG.debug("Request to save Dummy : {}", dummyDTO);
        Dummy dummy = dummyMapper.toEntity(dummyDTO);
        dummy = dummyRepository.save(dummy);
        return dummyMapper.toDto(dummy);
    }

    /**
     * Update a dummy.
     *
     * @param dummyDTO the entity to save.
     * @return the persisted entity.
     */
    public DummyDTO update(DummyDTO dummyDTO) {
        LOG.debug("Request to update Dummy : {}", dummyDTO);
        Dummy dummy = dummyMapper.toEntity(dummyDTO);
        dummy = dummyRepository.save(dummy);
        return dummyMapper.toDto(dummy);
    }

    /**
     * Partially update a dummy.
     *
     * @param dummyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DummyDTO> partialUpdate(DummyDTO dummyDTO) {
        LOG.debug("Request to partially update Dummy : {}", dummyDTO);

        return dummyRepository
            .findById(dummyDTO.getId())
            .map(existingDummy -> {
                dummyMapper.partialUpdate(existingDummy, dummyDTO);

                return existingDummy;
            })
            .map(dummyRepository::save)
            .map(dummyMapper::toDto);
    }

    /**
     * Get all the dummies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<DummyDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Dummies");
        return dummyRepository.findAll(pageable).map(dummyMapper::toDto);
    }

    /**
     * Get one dummy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<DummyDTO> findOne(String id) {
        LOG.debug("Request to get Dummy : {}", id);
        return dummyRepository.findById(id).map(dummyMapper::toDto);
    }

    /**
     * Delete the dummy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Dummy : {}", id);
        dummyRepository.deleteById(id);
    }
}
