package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Subgraph;
import jakarta.persistence.metamodel.Attribute;
import org.apache.openjpa.persistence.meta.Members;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AbstractGraph<T> {
    protected final Class<T> entityRootType;
    protected final Set<AttributeNode<?>> attributeNodes = new HashSet<>();
    protected final EntityManagerFactory entityManagerFactory;

    public AbstractGraph(Class<T> entityRootType, EntityManagerFactory entityManagerFactory) {
        this.entityRootType = entityRootType;
        this.entityManagerFactory = entityManagerFactory;
    }

    private AttributeNode<?> addAttributeNode(AttributeNode<?> attributeNode) {
        attributeNodes.add(attributeNode);
        return attributeNode;
    }

    public void addAttributeNodes(String... attributeName) {
        Arrays.stream(attributeName)
                .map(SimpleAttributeNode::new)
                .map(AttributeNode.class::cast)
                .forEach(this::addAttributeNode);
    }

    public void addAttributeNodes(Attribute<T, ?>... attribute) {
        addAttributeNodes(Arrays.stream(attribute)
                .map(Attribute::getName)
                .toArray(String[]::new));
    }

    public List<AttributeNode<?>> getAttributeNodes() {
        return new ArrayList<>(attributeNodes);
    }

    public <X> Subgraph<X> addSubgraph(String attributeName) {
        Attribute<T, X> attribute = (Attribute<T, X>) findAttribute(attributeName)
                .orElseThrow(() -> new IllegalArgumentException(attributeName + " is not an attribute of " + entityRootType.getName()));
        return addSubgraph(attribute);
    }

    private <X> Optional<Attribute<T, X>> findAttribute(String attributeName) {
        return Optional.ofNullable((Attribute<T, X>) entityManagerFactory.getMetamodel().entity(entityRootType).getAttribute(attributeName));
    }


    public <X> Subgraph<X> addSubgraph(String attributeName, Class<X> type) {
        var subGraph = new SimpleSubgraph<X>(type, entityManagerFactory);
        SimpleAttributeNode<?> attributeNode = (SimpleAttributeNode<?>) findAttributeNode(attributeName)
                .orElseGet(() -> addAttributeNode(new SimpleAttributeNode<>(attributeName)));
        attributeNode.addSubGraph(type, subGraph);
        return subGraph;
    }

    public <X> Subgraph<? extends X> addSubgraph(Attribute<T, X> attribute, Class<? extends X> type) {
        return addSubgraph(attribute.getName(), type);
    }

    public <X> Subgraph<X> addSubgraph(Attribute<T, X> attribute) {
        Class<X> type = (Class<X>) (attribute.isCollection() ? ((Members.SetAttributeImpl) (attribute)).getElementType().getJavaType() : attribute.getJavaType());
        return addSubgraph(attribute.getName(), type);
    }

    private Optional<AttributeNode<?>> findAttributeNode(String name) {
        return attributeNodes.stream().filter(a -> name.equals(a.getAttributeName())).findFirst();
    }

    public Class<T> getEntityRootType() {
        return entityRootType;
    }

}
