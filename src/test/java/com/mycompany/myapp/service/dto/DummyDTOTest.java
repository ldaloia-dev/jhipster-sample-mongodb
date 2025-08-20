package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DummyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DummyDTO.class);
        DummyDTO dummyDTO1 = new DummyDTO();
        dummyDTO1.setId("id1");
        DummyDTO dummyDTO2 = new DummyDTO();
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
        dummyDTO2.setId(dummyDTO1.getId());
        assertThat(dummyDTO1).isEqualTo(dummyDTO2);
        dummyDTO2.setId("id2");
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
        dummyDTO1.setId(null);
        assertThat(dummyDTO1).isNotEqualTo(dummyDTO2);
    }
}
