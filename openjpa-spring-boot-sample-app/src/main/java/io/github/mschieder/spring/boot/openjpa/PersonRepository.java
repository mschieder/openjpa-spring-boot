package io.github.mschieder.spring.boot.openjpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.SortedSet;

public interface PersonRepository extends JpaRepository<Person, Long> {

    interface Firstname {
        String getFirstname();
    }

    List<Person> findByLastname(String lastname);

    <R> List<R> findByLastname(String lastname, Class<R> resultClass);

    @Query(nativeQuery = true, value = "SELECT DISTINCT FIRSTNAME FROM PERSON ORDER BY FIRSTNAME ASC")
    SortedSet<String> firstNames();
    @Query(nativeQuery = true, value = "SELECT DISTINCT FIRSTNAME FROM PERSON ORDER BY FIRSTNAME ASC")
    List<Firstname> firstNamesProjection();


}
