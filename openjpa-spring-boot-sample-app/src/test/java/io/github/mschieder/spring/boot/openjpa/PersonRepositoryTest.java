package io.github.mschieder.spring.boot.openjpa;


import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;


class PersonRepositoryTest extends TestBase {


    public interface Lastname {
        String getLastname();
    }

    public interface Names {
        @Value("#{target.firstname + ' ' + target.lastname}")
        String getFullName();

        String getFirstname();

        String getLastname();
    }

    @Nested
    class FindByExampleTests {
        @Test
        void testFindByExample_Ok() {
            assertThat(personRepository.findAll(Example.of(new Person("John", "Doe")))).hasSize(1);
        }
    }


    @Nested
    class NativeQueryTests {
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

    @Nested
    class JpqlQueryTests {
        @Test
        void testFindByLastname_Ok() {
            assertThat(personRepository.findByLastname("Doe")).hasSize(1);
        }

        @Test
        void testFindByLastname_NotFound() {
            assertThat(personRepository.findByLastname("Smith")).isEmpty();
        }
    }

    @Nested
    class QueryDerivedFromMethodNameTests {

    }

    @Nested
    class InterfaceBasedProjectionTests {

        @Test
        void testClosedProjectionJpql() {
            assertThat(personRepository.findByLastname("Doe", Lastname.class)).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getLastname()).isEqualTo("Doe");
            });
        }

        @Test
        void testClosedProjectionNative() {
            assertThat(personRepository.findByLastnameNative("Doe", Lastname.class)).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getLastname()).isEqualTo("Doe");
            });
        }

        @Test
        void testOpenProjectionJpql() {
            assertThat(personRepository.findByLastname("Doe", Names.class)).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getFirstname()).isEqualTo("John");
                assertThat(name.getLastname()).isEqualTo("Doe");
                assertThat(name.getFullName()).isEqualTo("John Doe");
            });
        }

        @Test
        void testOpenProjectionNative() {
            assertThat(personRepository.findByLastnameNative("Doe", Names.class)).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getFirstname()).isEqualTo("John");
                assertThat(name.getLastname()).isEqualTo("Doe");
                assertThat(name.getFullName()).isEqualTo("John Doe");
            });
        }
    }

    @Nested
    class ClassBasedProjectionTests {
        public record NamesOnly(String firstname, String lastname) {
        }

        @Data
        @AllArgsConstructor
        public static class PersonDto {
            private String firstname;
            private String lastname;
        }

        @lombok.Value
        public static class PersonValueDto {
            String firstname;
            String lastname;
        }

        @Test
        void testRecordProjectionJpql() {
            assertThat(personRepository.findByLastname("Doe", NamesOnly.class)).hasSize(1).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.firstname()).isEqualTo("John");
                assertThat(name.lastname()).isEqualTo("Doe");
            });
        }

        @Test
        void testDtoClassProjectionJpql() {
            assertThat(personRepository.findByLastname("Doe", PersonDto.class)).hasSize(1).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getFirstname()).isEqualTo("John");
                assertThat(name.getLastname()).isEqualTo("Doe");
            });
        }

        @Test
        void testDtoValueClassProjectionJpql() {
            assertThat(personRepository.findByLastname("Doe", PersonValueDto.class)).hasSize(1).hasSize(1).satisfiesExactly(name -> {
                assertThat(name.getFirstname()).isEqualTo("John");
                assertThat(name.getLastname()).isEqualTo("Doe");
            });
        }

    }

    @Autowired
    EntityManager entityManager;

    @Nested
    @Transactional
    class EntityGraphTest {

        @BeforeEach
        void setup() {
            // ensure, that all caches are cleared
            entityManager.clear();
        }

        @Test
        void testAdhocFetchGraph_Ok() {
            var johnDoe = personRepository.findByLastnameAdhocFetchGraph("Doe");
            TestTransaction.end();
            assertAttributeLoaded(johnDoe.getAddress()::getStreet);
            assertAttributeNotLoaded(johnDoe::getOrders);
        }


        @Test
        void testNamedFetchGraph_Ok() {
            var johnDoe = personRepository.findByLastnameNamedFetchGraph("Doe");
            TestTransaction.end();
            assertAttributeLoaded(johnDoe.getAddress()::getStreet);
            assertAttributeNotLoaded(johnDoe::getOrders);
        }

        @Test
        void testNamedFetchGraphWithSubGraph_Ok() {
            var johnDoe = personRepository.findByLastnameNamedFetchGraphWithSubGraph("Doe");
            TestTransaction.end();
            assertAttributeLoaded(johnDoe.getAddress()::getStreet);
            assertAttributeLoaded(johnDoe::getOrders);
            johnDoe.getOrders().forEach(order -> assertAttributeLoaded(order::getOrderLines));
        }

        @Test
        void testAdhocLoadGraph_Ok() {
            var johnDoe = personRepository.findByLastnameAdhocLoadGraph("Doe");
            TestTransaction.end();
            assertAttributeLoaded(johnDoe.getAddress()::getStreet);
            assertAttributeLoaded(johnDoe::getOrders);
        }


        @Test
        void testNamedLoadGraph_Ok() {
            var johnDoe = personRepository.findByLastnameNamedLoadGraph("Doe");
            TestTransaction.end();
            assertAttributeLoaded(johnDoe.getAddress()::getStreet);
            assertAttributeLoaded(johnDoe::getOrders);
        }

        void assertAttributeLoaded(Supplier<?> getter) {
            var value = getter.get();
            if (value instanceof Collection<?> c) {
                assertThat(c.size()).isNotNegative();
            }
            assertThat(getter.get()).isNotNull();
        }

        void assertAttributeNotLoaded(Supplier<?> getter) {
            Object value = null;
            try {
                value = getter.get();
                if (value instanceof Collection<?> c) {
                    assertThat(c).isEmpty();
                }
            } catch (RuntimeException e) {
                //LazyInitializaionException
                if ("org.hibernate.LazyInitializationException".equals(e.getClass().getName())) {
                    value = null;
                } else {
                    throw e;
                }
            }
            assertThat(value).isNull();
        }
    }
}