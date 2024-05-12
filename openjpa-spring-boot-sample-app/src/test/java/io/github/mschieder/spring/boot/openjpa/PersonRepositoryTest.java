package io.github.mschieder.spring.boot.openjpa;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;

import static org.assertj.core.api.Assertions.assertThat;


class PersonRepositoryTest extends TestBase {


    public interface Lastname {
        String getLastname();
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @Test
    void testFindByLastname_Ok() {
        assertThat(personRepository.findByLastname("Doe")).hasSize(1);
    }

    @Test
    void testFindByLastname_NotFound() {
        assertThat(personRepository.findByLastname("Smith")).isEmpty();
    }

    @Test
    void testFindByExample_Ok() {
        assertThat(personRepository.findAll(Example.of(new Person("John", "Doe")))).hasSize(1);
    }

    @Test
    void testFindByLastnameProjection_Ok() {
        assertThat(personRepository.findByLastname("Doe", Lastname.class)).hasSize(1);
    }

    @Test
    void testNativeQuery() {
        assertThat(personRepository.firstNames()).containsExactly("John");
    }

    @Test
    void testNativeQueryProjection() {
        assertThat(personRepository.firstNamesProjection()).isNotEmpty();
    }

    @Test
    void testNativeQueryWithPositionalParameters() {
        assertThat(personRepository.findByLastnameNative("Doe")).isNotEmpty();
    }
}