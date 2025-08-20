package com.mycompany.myapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.RangeFilter;
import tech.jhipster.service.filter.StringFilter;

@Transactional(readOnly = true)
public abstract class QueryServiceMongoDB {

    protected <X> Criteria buildCriteria(Filter<X> filter, String field) {
        if (filter.getEquals() != null) {
            return equalsCriteria(field, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(field, filter.getNotIn());
        } else if (filter.getNotEquals() != null) {
            return notEqualsCriteria(field, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(field, filter.getSpecified());
        }
        return null;
    }

    protected <X> Criteria equalsCriteria(String field, final X value) {
        return Criteria.where(field).is(value);
    }

    protected <X> Criteria valueIn(String field, final Collection<X> values) {
        return Criteria.where(field).in(values);
    }

    protected <X> Criteria valueNotIn(String field, final X value) {
        return Criteria.where(field).nin(value);
    }

    protected <X> Criteria notEqualsCriteria(String field, final X value) {
        return Criteria.where(field).ne(value);
    }

    protected Criteria byFieldSpecified(String field, final boolean specified) {
        return Criteria.where(field).exists(specified);
    }

    protected <X extends Comparable<? super X>> List<Criteria> buildRangeCriteria(RangeFilter<X> filter, String field) {
        return buildCriteria(filter, field);
    }

    protected <X extends Comparable<? super X>> List<Criteria> buildCriteria(RangeFilter<X> filter, String field) {
        if (filter.getEquals() != null) {
            return Arrays.asList(equalsCriteria(field, filter.getEquals()));
        } else if (filter.getIn() != null) {
            return Arrays.asList(valueIn(field, filter.getIn()));
        }
        List<Criteria> result = new ArrayList<>();
        if (filter.getSpecified() != null) {
            result.add(byFieldSpecified(field, filter.getSpecified()));
        }
        if (filter.getNotEquals() != null) {
            result.add(notEqualsCriteria(field, filter.getNotEquals()));
        }
        if (filter.getNotIn() != null) {
            result.add(valueNotIn(field, filter.getNotIn()));
        }
        if (filter.getGreaterThan() != null) {
            result.add(greaterThan(field, filter.getGreaterThan()));
        }
        if (filter.getGreaterThanOrEqual() != null) {
            result.add(greaterThanOrEqualTo(field, filter.getGreaterThanOrEqual()));
        }
        if (filter.getLessThan() != null) {
            result.add(lessThan(field, filter.getLessThan()));
        }
        if (filter.getLessThanOrEqual() != null) {
            result.add(lessThanOrEqualTo(field, filter.getLessThanOrEqual()));
        }
        return result;
    }

    protected <X extends Comparable<? super X>> Criteria greaterThan(String field, final X value) {
        return Criteria.where(field).gt(value);
    }

    protected <X extends Comparable<? super X>> Criteria greaterThanOrEqualTo(String field, final X value) {
        return Criteria.where(field).gte(value);
    }

    protected <X extends Comparable<? super X>> Criteria lessThan(String field, final X value) {
        return Criteria.where(field).lt(value);
    }

    protected <X extends Comparable<? super X>> Criteria lessThanOrEqualTo(String field, final X value) {
        return Criteria.where(field).lte(value);
    }

    protected Criteria buildStringCriteria(StringFilter filter, String field) {
        return buildCriteria(filter, field);
    }

    protected Criteria buildCriteria(StringFilter filter, String field) {
        if (filter.getEquals() != null) {
            return equalsCriteria(field, filter.getEquals());
        } else if (filter.getIn() != null) {
            return valueIn(field, filter.getIn());
        } else if (filter.getNotIn() != null) {
            return valueNotIn(field, filter.getNotIn());
        } else if (filter.getContains() != null) {
            return containSpecification(field, filter.getContains());
        } else if (filter.getDoesNotContain() != null) {
            return doesNotContainSpecification(field, filter.getDoesNotContain());
        } else if (filter.getNotEquals() != null) {
            return notEqualsCriteria(field, filter.getNotEquals());
        } else if (filter.getSpecified() != null) {
            return byFieldSpecified(field, filter.getSpecified());
        }
        return null;
    }

    protected Criteria containSpecification(String field, final String value) {
        return Criteria.where(field).regex(value);
    }

    protected Criteria doesNotContainSpecification(String field, String value) {
        return Criteria.where(field).not().regex(value);
    }

    protected Criteria buildReferringEntitySpecification(Filter<?> filter, String... path) {
        return buildCriteria(filter, String.join(".", path));
    }
}
