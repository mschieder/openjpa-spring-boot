package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.Subgraph;

import java.util.Map;
import java.util.Objects;

public class SimpleAttributeNode<T> implements AttributeNode<T> {

    private String attributeName;

    public SimpleAttributeNode(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public Map<Class, Subgraph> getSubgraphs() {
        return Map.of();
    }

    @Override
    public Map<Class, Subgraph> getKeySubgraphs() {
        return Map.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAttributeNode<?> that = (SimpleAttributeNode<?>) o;
        return Objects.equals(attributeName, that.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(attributeName);
    }

    @Override
    public String toString() {
        return "SimpleAttributeNode{" +
                "attributeName='" + attributeName + '\'' +
                '}';
    }
}
