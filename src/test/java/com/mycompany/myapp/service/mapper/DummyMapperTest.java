package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.DummyAsserts.*;
import static com.mycompany.myapp.domain.DummyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DummyMapperTest {

    private DummyMapper dummyMapper;

    @BeforeEach
    void setUp() {
        dummyMapper = new DummyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDummySample1();
        var actual = dummyMapper.toEntity(dummyMapper.toDto(expected));
        assertDummyAllPropertiesEquals(expected, actual);
    }
}
