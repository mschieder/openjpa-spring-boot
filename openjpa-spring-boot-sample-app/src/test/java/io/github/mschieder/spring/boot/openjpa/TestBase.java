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
                .addOrder(new ShoppingOrder(BigDecimal.TEN, "EUR")
                        .addOrderLine(new OrderLine(1, "Fake eyelashes"))
                        .addOrderLine(new OrderLine(2, "Deluxe edition box sets"))
                )
                .addOrder(new ShoppingOrder(BigDecimal.ONE, "USD")
                        .addOrderLine(new OrderLine(3, "Sunglasses"))
                        .addOrderLine(new OrderLine(4, "Teeth whitener"))
                );
        person.setAddress(new Address("Amphitheatre Parkway", "Mountain View"));
        personRepository.saveAndFlush(person);
    }

}
