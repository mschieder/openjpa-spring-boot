package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityManagerTest extends TestBase {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testNativeQueryWithPositionalParameters() {
        Query query = entityManager.createNativeQuery("select * from PERSON where lastname = ?1");
        assertThat(query.getParameters()).isNotEmpty();
    }

    @Test
    void testTupleNative() {
        List<Tuple> result = entityManager.createNativeQuery("select lastname from Person", Tuple.class)
                .getResultList();

        assertThat(result).hasSize(1)
                .satisfiesExactly(t -> {
                    assertThat(t.getElements()).hasSize(1);
                    assertThat(t.get(0)).isEqualTo("Doe");
                    assertThat(t.getElements().get(0).getAlias()).isEqualToIgnoringCase("LASTNAME");
                });
    }

    @Test
    void testTupleJpql() {
        assertThat(entityManager.createQuery("select p.lastname, p.firstname from Person p", Tuple.class)
                .getResultList())
                .isNotEmpty();
        //    .containsExactly(new SimpleTupleImpl("LASTNAME", "Doe"));
    }
}
