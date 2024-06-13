package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.EntityGraph;

import java.util.List;

public class EntityGraphEntityManager {

    private final EntityGraphFactory entityGraphFactory;

    public EntityGraphEntityManager(EntityGraphFactory entityGraphFactory) {
        this.entityGraphFactory = entityGraphFactory;
    }

    /**
     * Return a mutable EntityGraph that can be used to dynamically create an
     * EntityGraph.
     *
     * @param rootType class of entity graph
     * @return entity graph
     * @since 2.1
     */
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return entityGraphFactory.createEntityGraph(rootType);
    }

    /**
     * Return a mutable copy of the named EntityGraph.  If there
     * is no entity graph with the specified name, null is returned.
     *
     * @param graphName name of an entity graph
     * @return entity graph
     * @since 2.1
     */
    public EntityGraph<?> createEntityGraph(String graphName) {
        return entityGraphFactory.findEntityGraph(graphName).map(SimpleEntityGraph::new).orElse(null);
    }

    /**
     * Return a named EntityGraph. The returned EntityGraph
     * should be considered immutable.
     *
     * @param graphName name of an existing entity graph
     * @return named entity graph
     * @throws IllegalArgumentException if there is no EntityGraph of
     *                                  the given name
     * @since 2.1
     */
    public EntityGraph<?> getEntityGraph(String graphName) {
        return entityGraphFactory.findEntityGraph(graphName)
                .orElseThrow(() -> new IllegalArgumentException("Could not locate EntityGraph with given name : " + graphName));
    }

    /**
     * Return all named EntityGraphs that have been defined for the provided
     * class type.
     *
     * @param entityClass entity class
     * @return list of all entity graphs defined for the entity
     * @throws IllegalArgumentException if the class is not an entity
     * @since 2.1
     */
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        if (!entityGraphFactory.isEntityClass(entityClass)) {
            throw new IllegalArgumentException("class is not an entity: " + entityClass);
        }
        return (List) entityGraphFactory.getEntityGraphs(entityClass);
    }
}
