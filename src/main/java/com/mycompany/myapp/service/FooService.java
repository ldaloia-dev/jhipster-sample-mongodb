package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.repository.FooRepository;
import com.mycompany.myapp.service.dto.FooDTO;
import com.mycompany.myapp.service.mapper.FooMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Foo}.
 */
@Service
public class FooService {

    private static final Logger LOG = LoggerFactory.getLogger(FooService.class);

    private final FooRepository fooRepository;

    private final FooMapper fooMapper;

    public FooService(FooRepository fooRepository, FooMapper fooMapper) {
        this.fooRepository = fooRepository;
        this.fooMapper = fooMapper;
    }

    /**
     * Save a foo.
     *
     * @param fooDTO the entity to save.
     * @return the persisted entity.
     */
    public FooDTO save(FooDTO fooDTO) {
        LOG.debug("Request to save Foo : {}", fooDTO);
        Foo foo = fooMapper.toEntity(fooDTO);
        foo = fooRepository.save(foo);
        return fooMapper.toDto(foo);
    }

    /**
     * Update a foo.
     *
     * @param fooDTO the entity to save.
     * @return the persisted entity.
     */
    public FooDTO update(FooDTO fooDTO) {
        LOG.debug("Request to update Foo : {}", fooDTO);
        Foo foo = fooMapper.toEntity(fooDTO);
        foo = fooRepository.save(foo);
        return fooMapper.toDto(foo);
    }

    /**
     * Partially update a foo.
     *
     * @param fooDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FooDTO> partialUpdate(FooDTO fooDTO) {
        LOG.debug("Request to partially update Foo : {}", fooDTO);

        return fooRepository
            .findById(fooDTO.getId())
            .map(existingFoo -> {
                fooMapper.partialUpdate(existingFoo, fooDTO);

                return existingFoo;
            })
            .map(fooRepository::save)
            .map(fooMapper::toDto);
    }

    /**
     * Get all the foos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<FooDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Foos");
        return fooRepository.findAll(pageable).map(fooMapper::toDto);
    }

    /**
     * Get one foo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<FooDTO> findOne(String id) {
        LOG.debug("Request to get Foo : {}", id);
        return fooRepository.findById(id).map(fooMapper::toDto);
    }

    /**
     * Delete the foo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Foo : {}", id);
        fooRepository.deleteById(id);
    }
}
