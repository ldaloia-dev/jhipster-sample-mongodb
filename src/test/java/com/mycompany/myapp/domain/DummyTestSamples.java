package com.mycompany.myapp.domain;

import java.util.UUID;

public class DummyTestSamples {

    public static Dummy getDummySample1() {
        return new Dummy().id("id1").name("name1").description("description1");
    }

    public static Dummy getDummySample2() {
        return new Dummy().id("id2").name("name2").description("description2");
    }

    public static Dummy getDummyRandomSampleGenerator() {
        return new Dummy().id(UUID.randomUUID().toString()).name(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
