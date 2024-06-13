package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.EntityGraph;

public class EntityGraphEntityManagerFactory {

    private final EntityGraphFactory entityGraphFactory;

    public EntityGraphEntityManagerFactory(EntityGraphFactory entityGraphFactory) {
        this.entityGraphFactory = entityGraphFactory;
    }

    /**
     * Add a named copy of the EntityGraph to the
     * EntityManagerFactory.  If an entity graph with the same name
     * already exists, it is replaced.
     *
     * @param graphName   name for the entity graph
     * @param entityGraph entity graph
     * @since 2.1
     */
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        entityGraphFactory.addNamedEntityGraph(graphName, (SimpleEntityGraph<? extends Object>) entityGraph);
    }
}
