package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.service.criteria.FooCriteria;
import com.mycompany.myapp.service.dto.FooDTO;
import com.mycompany.myapp.service.mapper.FooMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FooQueryService extends QueryServiceMongoDB {

    private final Logger LOG = LoggerFactory.getLogger(FooQueryService.class);

    private final MongoTemplate mongoTemplate;

    private final FooMapper fooMapper;

    public FooQueryService(MongoTemplate mongoTemplate, FooMapper fooMapper) {
        this.mongoTemplate = mongoTemplate;
        this.fooMapper = fooMapper;
    }

    /**
     * Return a {@link Page} of {@link FooDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FooDTO> findByCriteria(FooCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Query query = createQuery(criteria, null);
        List<FooDTO> result = mongoTemplate.find(query, Foo.class).stream().map(fooMapper::toDto).collect(Collectors.toList());
        return PageableExecutionUtils.getPage(result, page, () -> countByCriteria(criteria));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FooCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Query query = createQuery(criteria, null);
        return mongoTemplate.count(query, Foo.class);
    }

    /**
     * Function to convert {@link FooCriteria} to a {@link Query}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Query} of the entity.
     */
    protected Query createQuery(FooCriteria criteria, Pageable page) {
        List<Criteria> and = new ArrayList<>();
        Query query;
        if (criteria != null) {
            if (criteria.getId() != null) {
                and.add(buildStringCriteria(criteria.getId(), "id"));
            }
            if (criteria.getName() != null) {
                and.add(buildStringCriteria(criteria.getName(), "name"));
            }
            if (criteria.getDescription() != null) {
                and.add(buildStringCriteria(criteria.getDescription(), "description"));
            }
            query = new Query(and.isEmpty() ? new Criteria() : new Criteria().andOperator(and));
            if (page != null) {
                query.with(page);
            }
            return query;
        }
        return new Query();
    }
}
