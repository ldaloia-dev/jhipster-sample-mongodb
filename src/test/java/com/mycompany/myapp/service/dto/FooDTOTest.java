package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FooDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FooDTO.class);
        FooDTO fooDTO1 = new FooDTO();
        fooDTO1.setId("id1");
        FooDTO fooDTO2 = new FooDTO();
        assertThat(fooDTO1).isNotEqualTo(fooDTO2);
        fooDTO2.setId(fooDTO1.getId());
        assertThat(fooDTO1).isEqualTo(fooDTO2);
        fooDTO2.setId("id2");
        assertThat(fooDTO1).isNotEqualTo(fooDTO2);
        fooDTO1.setId(null);
        assertThat(fooDTO1).isNotEqualTo(fooDTO2);
    }
}
