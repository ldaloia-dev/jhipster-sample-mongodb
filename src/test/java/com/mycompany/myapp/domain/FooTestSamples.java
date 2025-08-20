package com.mycompany.myapp.domain;

import java.util.UUID;

public class FooTestSamples {

    public static Foo getFooSample1() {
        return new Foo().id("id1").name("name1").description("description1");
    }

    public static Foo getFooSample2() {
        return new Foo().id("id2").name("name2").description("description2");
    }

    public static Foo getFooRandomSampleGenerator() {
        return new Foo().id(UUID.randomUUID().toString()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
