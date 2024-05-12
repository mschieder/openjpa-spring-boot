package io.github.mschieder.spring.boot.openjpa;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class TestBase {
    @Autowired
    protected PersonRepository personRepository;

    @BeforeEach
    void givenJohnDoe() {
        personRepository.deleteAll();
        personRepository.save(new Person("John", "Doe"));
    }

}
