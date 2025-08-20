package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Dummy.
 */
@Document(collection = "dummy")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dummy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @DBRef
    @Field("foo")
    @JsonIgnoreProperties(value = { "dummy" }, allowSetters = true)
    private Set<Foo> foos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Dummy id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Dummy name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Dummy description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Foo> getFoos() {
        return this.foos;
    }

    public void setFoos(Set<Foo> foos) {
        if (this.foos != null) {
            this.foos.forEach(i -> i.setDummy(null));
        }
        if (foos != null) {
            foos.forEach(i -> i.setDummy(this));
        }
        this.foos = foos;
    }

    public Dummy foos(Set<Foo> foos) {
        this.setFoos(foos);
        return this;
    }

    public Dummy addFoo(Foo foo) {
        this.foos.add(foo);
        foo.setDummy(this);
        return this;
    }

    public Dummy removeFoo(Foo foo) {
        this.foos.remove(foo);
        foo.setDummy(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dummy)) {
            return false;
        }
        return getId() != null && getId().equals(((Dummy) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dummy{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
