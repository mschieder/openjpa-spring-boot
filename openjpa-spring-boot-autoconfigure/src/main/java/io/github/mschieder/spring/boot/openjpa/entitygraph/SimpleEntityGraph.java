package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;

import java.util.List;
import java.util.Objects;

public class SimpleEntityGraph<T> extends AbstractGraph<T> implements EntityGraph<T> {

    private final String name;

    public SimpleEntityGraph(Class<T> entityRootType, EntityManagerFactory entityManagerFactory) {
        this(entityRootType, null, entityManagerFactory);
    }

    public SimpleEntityGraph(Class<T> entityRootType, String name, EntityManagerFactory entityManagerFactory) {
        super(entityRootType, entityManagerFactory);
        this.name = name;
    }

    public SimpleEntityGraph(SimpleEntityGraph<T> graph) {
        this(graph, graph.name);
    }

    public SimpleEntityGraph(SimpleEntityGraph<T> graph, String name) {
        this(graph.entityRootType, name, graph.entityManagerFactory);
        this.attributeNodes.addAll(graph.attributeNodes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addAttributeNodes(String... attributeName) {
        super.addAttributeNodes(attributeName);
    }

    @SafeVarargs
    @Override
    public final void addAttributeNodes(Attribute<T, ?>... attribute) {
        super.addAttributeNodes(attribute);
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
        return super.getAttributeNodes();
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
