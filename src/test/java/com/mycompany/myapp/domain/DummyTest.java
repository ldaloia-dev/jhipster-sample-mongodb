package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DummyTestSamples.*;
import static com.mycompany.myapp.domain.FooTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DummyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dummy.class);
        Dummy dummy1 = getDummySample1();
        Dummy dummy2 = new Dummy();
        assertThat(dummy1).isNotEqualTo(dummy2);

        dummy2.setId(dummy1.getId());
        assertThat(dummy1).isEqualTo(dummy2);

        dummy2 = getDummySample2();
        assertThat(dummy1).isNotEqualTo(dummy2);
    }

    @Test
    void fooTest() {
        Dummy dummy = getDummyRandomSampleGenerator();
        Foo fooBack = getFooRandomSampleGenerator();

        dummy.addFoo(fooBack);
        assertThat(dummy.getFoos()).containsOnly(fooBack);
        assertThat(fooBack.getDummy()).isEqualTo(dummy);

        dummy.removeFoo(fooBack);
        assertThat(dummy.getFoos()).doesNotContain(fooBack);
        assertThat(fooBack.getDummy()).isNull();

        dummy.foos(new HashSet<>(Set.of(fooBack)));
        assertThat(dummy.getFoos()).containsOnly(fooBack);
        assertThat(fooBack.getDummy()).isEqualTo(dummy);

        dummy.setFoos(new HashSet<>());
        assertThat(dummy.getFoos()).doesNotContain(fooBack);
        assertThat(fooBack.getDummy()).isNull();
    }
}
