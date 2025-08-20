package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DummyTestSamples.*;
import static com.mycompany.myapp.domain.FooTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FooTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Foo.class);
        Foo foo1 = getFooSample1();
        Foo foo2 = new Foo();
        assertThat(foo1).isNotEqualTo(foo2);

        foo2.setId(foo1.getId());
        assertThat(foo1).isEqualTo(foo2);

        foo2 = getFooSample2();
        assertThat(foo1).isNotEqualTo(foo2);
    }

    @Test
    void dummyTest() {
        Foo foo = getFooRandomSampleGenerator();
        Dummy dummyBack = getDummyRandomSampleGenerator();

        foo.setDummy(dummyBack);
        assertThat(foo.getDummy()).isEqualTo(dummyBack);

        foo.dummy(null);
        assertThat(foo.getDummy()).isNull();
    }
}
