package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Foo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FooDTO implements Serializable {

    private String id;

    @NotNull
    private String name;

    private String description;

    private Instant start;

    private DummyDTO dummy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public DummyDTO getDummy() {
        return dummy;
    }

    public void setDummy(DummyDTO dummy) {
        this.dummy = dummy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FooDTO)) {
            return false;
        }

        FooDTO fooDTO = (FooDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, fooDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FooDTO{" +
            "id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", start='" + getStart() + "'" +
            ", dummy=" + getDummy() +
            "}";
    }
}
