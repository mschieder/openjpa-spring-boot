package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ShoppingOrder {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal totalAmount;
    private String currency;

    @ManyToOne
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Person customer;

    public ShoppingOrder(BigDecimal totalAmount, String currency) {
        this.totalAmount = totalAmount;
        this.currency = currency;
    }
}
