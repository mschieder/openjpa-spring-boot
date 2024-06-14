package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;

import java.util.List;
import java.util.Objects;

public class SimpleSubgraph<T> extends AbstractGraph<T> implements Subgraph<T> {

    public SimpleSubgraph(Class<T> entityRootType, EntityManagerFactory entityManagerFactory) {
        super(entityRootType, entityManagerFactory);
    }

    @Override
    public void addAttributeNodes(String... attributeName) {
        super.addAttributeNodes(attributeName);
    }

    @Override
    public void addAttributeNodes(Attribute<T, ?>... attribute) {
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
    public List<AttributeNode<?>> getAttributeNodes() {
        return super.getAttributeNodes();
    }

    @Override
    public Class<T> getClassType() {
        return super.getEntityRootType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeNodes, entityRootType);
    }

    @Override
    public String toString() {
        return "SimpleEntityGraph{" +
                "attributeNodes=" + attributeNodes +
                ", entityRootType=" + entityRootType +
                '}';
    }
}
