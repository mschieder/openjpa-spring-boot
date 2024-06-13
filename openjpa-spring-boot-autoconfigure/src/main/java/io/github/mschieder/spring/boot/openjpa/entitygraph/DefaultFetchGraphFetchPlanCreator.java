package io.github.mschieder.spring.boot.openjpa.entitygraph;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import org.apache.openjpa.persistence.FetchPlan;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DefaultFetchGraphFetchPlanCreator {

    private final EntityManagerFactory entityManagerFactory;
    private Map<Class<?>, Set<String>> basicAttributeMap = new LinkedHashMap<>();

    public DefaultFetchGraphFetchPlanCreator(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        entityManagerFactory.getMetamodel().getEntities().forEach(this::scan);
    }

    private void scan(EntityType<?> entityType) {
        entityType.getAttributes().forEach(a -> scanAttribute(entityType, a));
    }

    private void scanAttribute(EntityType<?> entityType, Attribute<?, ?> attribute) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            var attributes = basicAttributeMap.computeIfAbsent(entityType.getBindableJavaType(), k -> new LinkedHashSet<>());
            attributes.add(attribute.getName());
        }
    }

    public void addToFetchPlan(FetchPlan fetchPlan) {
        basicAttributeMap.forEach((entityClass, value) -> value.forEach(attributeName -> {
            fetchPlan.addFields(entityClass, attributeName);
        }));
    }

}
