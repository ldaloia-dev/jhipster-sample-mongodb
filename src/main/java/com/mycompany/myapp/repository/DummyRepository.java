package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Dummy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Dummy entity.
 */
@Repository
public interface DummyRepository extends MongoRepository<Dummy, String> {}
