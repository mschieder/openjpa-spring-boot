package io.github.mschieder.spring.boot.openjpa;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    public interface Lastname {
        String getLastname();
    }

    @BeforeEach
    void givenJohnDoe() {
        personRepository.save(new Person("John", "Doe"));
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
    void testNativeQuery(){
        assertThat(personRepository.firstNames()).containsExactly("John");
    }

    @Test
    void testNativeQueryProjection(){
        assertThat(personRepository.firstNamesProjection()).isNotEmpty();
    }
}