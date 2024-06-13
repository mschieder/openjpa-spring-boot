package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String street;
    @Column
    private String city;
    @Column
    private Long addressId;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
}
