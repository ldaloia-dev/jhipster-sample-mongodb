package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Dummy;
import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.service.dto.DummyDTO;
import com.mycompany.myapp.service.dto.FooDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Foo} and its DTO {@link FooDTO}.
 */
@Mapper(componentModel = "spring")
public interface FooMapper extends EntityMapper<FooDTO, Foo> {
    @Mapping(target = "dummy", source = "dummy", qualifiedByName = "dummyId")
    FooDTO toDto(Foo s);

    @Named("dummyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DummyDTO toDtoDummyId(Dummy dummy);
}
