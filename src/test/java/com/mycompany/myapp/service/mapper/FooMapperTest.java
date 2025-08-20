package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.FooAsserts.*;
import static com.mycompany.myapp.domain.FooTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FooMapperTest {

    private FooMapper fooMapper;

    @BeforeEach
    void setUp() {
        fooMapper = new FooMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFooSample1();
        var actual = fooMapper.toEntity(fooMapper.toDto(expected));
        assertFooAllPropertiesEquals(expected, actual);
    }
}
