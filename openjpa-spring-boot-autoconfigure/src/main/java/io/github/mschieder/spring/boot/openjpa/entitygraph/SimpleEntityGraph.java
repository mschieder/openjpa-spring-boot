package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SimpleEntityGraph<T> implements EntityGraph<T> {

    private final Set<AttributeNode<?>> attributeNodes = new HashSet<>();
    private final Class<T> entityRootType;
    private final String name;

    public SimpleEntityGraph(Class<T> entityRootType) {
        this(entityRootType, null);
    }

    public SimpleEntityGraph(Class<T> entityRootType, String name) {
        this.entityRootType = entityRootType;
        this.name = name;
    }

    public SimpleEntityGraph(SimpleEntityGraph<T> graph) {
        this.name = graph.name;
        this.entityRootType = graph.entityRootType;
        this.attributeNodes.addAll(graph.attributeNodes);
    }

    public SimpleEntityGraph(SimpleEntityGraph<T> graph, String name) {
        this.name = name;
        this.entityRootType = graph.entityRootType;
        this.attributeNodes.addAll(graph.attributeNodes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addAttributeNodes(String... attributeName) {
        Arrays.stream(attributeName)
                .map(SimpleAttributeNode::new)
                .map(AttributeNode.class::cast)
                .forEach(attributeNodes::add);
    }

    @Override
    public void addAttributeNodes(Attribute<T, ?>... attribute) {
        addAttributeNodes(Arrays.stream(attribute)
                .map(Attribute::getName)
                .toArray(String[]::new));
    }

    @Override
    public <X> Subgraph<X> addSubgraph(Attribute<T, X> attribute) {
        return null;
    }

    @Override
    public <X> Subgraph<? extends X> addSubgraph(Attribute<T, X> attribute, Class<? extends X> type) {
        return null;
    }

    @Override
    public <X> Subgraph<X> addSubgraph(String attributeName) {
        return null;
    }

    @Override
    public <X> Subgraph<X> addSubgraph(String attributeName, Class<X> type) {
        return null;
    }

    @Override
    public <X> Subgraph<X> addKeySubgraph(Attribute<T, X> attribute) {
        return null;
    }

    @Override
    public <X> Subgraph<? extends X> addKeySubgraph(Attribute<T, X> attribute, Class<? extends X> type) {
        return null;
    }

    @Override
    public <X> Subgraph<X> addKeySubgraph(String attributeName) {
        return null;
    }

    @Override
    public <X> Subgraph<X> addKeySubgraph(String attributeName, Class<X> type) {
        return null;
    }

    @Override
    public <T1> Subgraph<? extends T1> addSubclassSubgraph(Class<? extends T1> type) {
        return null;
    }

    @Override
    public List<AttributeNode<?>> getAttributeNodes() {
        return new ArrayList<>(attributeNodes);
    }

    public Class<T> getEntityRootType() {
        return entityRootType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleEntityGraph<?> that = (SimpleEntityGraph<?>) o;
        return Objects.equals(attributeNodes, that.attributeNodes) && Objects.equals(entityRootType, that.entityRootType) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeNodes, entityRootType, name);
    }

    @Override
    public String toString() {
        return "SimpleEntityGraph{" +
                "attributeNodes=" + attributeNodes +
                ", entityRootType=" + entityRootType +
                ", name='" + name + '\'' +
                '}';
    }
}
