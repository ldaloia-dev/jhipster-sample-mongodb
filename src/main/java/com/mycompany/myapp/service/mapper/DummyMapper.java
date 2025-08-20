package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Dummy;
import com.mycompany.myapp.service.dto.DummyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Dummy} and its DTO {@link DummyDTO}.
 */
@Mapper(componentModel = "spring")
public interface DummyMapper extends EntityMapper<DummyDTO, Dummy> {}
