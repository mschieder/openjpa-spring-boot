package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.metamodel.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class EntityGraphFactory {
    private final EntityManagerFactory entityManagerFactory;
    private final Map<String, SimpleEntityGraph<?>> entityGraphsByName = new HashMap<>();
    private final Map<Class<?>, Set<EntityGraph<?>>> entityGraphsByClass = new HashMap<>();

    public EntityGraphFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        scanMetalmodel();
    }

    private void scanMetalmodel() {
        entityManagerFactory.getMetamodel().getEntities().stream().map(EntityType::getJavaType).forEach(this::scanClass);
    }

    private void scanClass(Class<?> javaType) {
        Stream.of(javaType.getAnnotationsByType(NamedEntityGraph.class))
                .forEach(entityGraph -> scanNamedEntityGraph(entityGraph, javaType));
    }

    private void scanNamedEntityGraph(NamedEntityGraph namedEntityGraph, Class<?> javaType) {
        var name = namedEntityGraph.name();

        var entityGraph = new SimpleEntityGraph<>(javaType, name, entityManagerFactory);
        scanAttributeNodes(namedEntityGraph.attributeNodes(), entityGraph, namedEntityGraph);
        addEntityGraphInternal(name, entityGraph);
    }

    private void scanAttributeNodes(NamedAttributeNode[] nodes, AbstractGraph<?> parent, NamedEntityGraph namedEntityGraph) {
        Stream.of(nodes).forEach(attributeNode -> {
            if (!attributeNode.subgraph().isBlank()) {
                var namedSubGraph = findSubGraph(attributeNode.subgraph(), namedEntityGraph)
                        .orElseThrow(() -> new IllegalArgumentException("unknown subgraph: " + attributeNode.subgraph()));

                SimpleSubgraph<?> subGraph = (SimpleSubgraph<?>) parent.addSubgraph(attributeNode.value());
                scanAttributeNodes(namedSubGraph.attributeNodes(), subGraph, namedEntityGraph);
            } else {
                parent.addAttributeNodes(attributeNode.value());
            }
        });
    }


    private Optional<NamedSubgraph> findSubGraph(String subGraphName, NamedEntityGraph namedEntityGraph) {
        return Stream.of(namedEntityGraph.subgraphs()).filter(subGraph -> subGraphName.equals(subGraph.name())).findFirst();
    }

    public Optional<SimpleEntityGraph<?>> findEntityGraph(String name) {
        return Optional.ofNullable(entityGraphsByName.get(name));
    }

    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return new SimpleEntityGraph<>(rootType, entityManagerFactory);
    }

    public List<EntityGraph<?>> getEntityGraphs(Class<?> entityClass) {
        return new ArrayList<>(entityGraphsByClass.getOrDefault(entityClass, new HashSet<>()));
    }

    public <T> void addNamedEntityGraph(String graphName, SimpleEntityGraph<T> entityGraph) {
        var copy = new SimpleEntityGraph<T>(entityGraph, graphName);
        addEntityGraphInternal(graphName, copy);
    }

    private void addEntityGraphInternal(String graphName, SimpleEntityGraph<?> entityGraph) {
        entityGraphsByName.put(graphName, entityGraph);
        var entityGraphs = entityGraphsByClass.computeIfAbsent(entityGraph.getEntityRootType(), k -> new HashSet<>());
        entityGraphs.add(entityGraph);
    }

    public boolean isEntityClass(Class<?> aClass) {
        return entityManagerFactory.getMetamodel().getEntities().stream().anyMatch(e -> aClass.equals(e.getJavaType()));
    }

}
