package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.persistence.jdbc.JDBCFetchPlan;

import java.util.Objects;

public class EntityGraphQuery {

    private final DefaultFetchGraphFetchPlanCreator defaultFetchGraphFetchPlanCreator;

    public EntityGraphQuery(EntityManagerFactory entityManagerFactory) {
        this.defaultFetchGraphFetchPlanCreator = new DefaultFetchGraphFetchPlanCreator(entityManagerFactory);
    }

    private enum Type {
        FETCH, LOAD;
    }


    public TypedQuery<?> setHint(String hintName, Object value, TypedQuery<?> query) {
        if ("jakarta.persistence.fetchgraph".equals(hintName)) {
            toOpenJpaFetchPlan((SimpleEntityGraph<?>) value, query, EntityGraphQuery.Type.FETCH);
        } else if ("jakarta.persistence.loadgraph".equals(hintName)) {
            toOpenJpaFetchPlan((SimpleEntityGraph<?>) value, query, EntityGraphQuery.Type.LOAD);
        } else {
            query.setHint(hintName, value);
        }
        return query;
    }


    private void toOpenJpaFetchPlan(SimpleEntityGraph<?> entityGraph, Query query, Type type) {
        OpenJPAQuery<?> openJpaQuery = OpenJPAPersistence.cast(query);
        JDBCFetchPlan fetchPlan = (JDBCFetchPlan) openJpaQuery.getFetchPlan();

        if (type == Type.FETCH) {
            // remove OpenJPAs "default" fetch group, so we can ignore EAGER configured relations
            fetchPlan.removeFetchGroup("default");
            // add all basic properties to the current fetch plan
            defaultFetchGraphFetchPlanCreator.addToFetchPlan(fetchPlan);
        }

        entityGraph.getAttributeNodes().stream()
                .map(AttributeNode::getAttributeName)
                .filter(Objects::nonNull)
                .forEach(name -> fetchPlan.addField(entityGraph.getEntityRootType(), name));
    }

}
