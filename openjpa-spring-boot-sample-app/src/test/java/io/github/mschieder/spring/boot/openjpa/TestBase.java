package io.github.mschieder.spring.boot.openjpa;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@Transactional
public abstract class TestBase {
    @Autowired
    protected PersonRepository personRepository;

    @BeforeEach
    void givenJohnDoe() {
        personRepository.deleteAll();
        var person = new Person("John", "Doe")
                .addOrder(new ShoppingOrder(BigDecimal.TEN, "EUR"))
                .addOrder(new ShoppingOrder(BigDecimal.ONE, "USD"));
        person.setAddress(new Address("Amphitheatre Parkway", "Mountain View"));
        personRepository.saveAndFlush(person);
    }

}
