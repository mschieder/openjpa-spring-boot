package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@NamedEntityGraph(name = "Person.adr",
        attributeNodes = @NamedAttributeNode("address"))
@NamedEntityGraph(name = "Person.adr.sub",
        attributeNodes = {
                @NamedAttributeNode("address"),
                @NamedAttributeNode(value = "orders", subgraph = "sub")
        },
        subgraphs = @NamedSubgraph(name = "sub", attributeNodes = @NamedAttributeNode(value = "orderLines")))
public class Person {

    public Person(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String firstname;
    private String lastname;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn
    private Address address;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "customer")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ShoppingOrder> orders = new HashSet<>();

    public Person addOrder(ShoppingOrder order) {
        orders.add(order);
        order.setCustomer(this);
        return this;
    }
}
