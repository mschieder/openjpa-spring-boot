package io.github.mschieder.spring.boot.openjpa;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;

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
}