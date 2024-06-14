package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class OrderLine {
    @Id
    @GeneratedValue
    private Long id;

    public OrderLine(Integer quantity, String item) {
        this.quantity = quantity;
        this.item = item;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ShoppingOrder order;

    private Integer quantity;
    private String item;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return id != null && Objects.equals(id, orderLine.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
