package io.github.mschieder.spring.boot.openjpa.tuple;

import io.github.mschieder.spring.boot.openjpa.autoconfiguration.OpenJpaProperties;
import io.github.mschieder.spring.boot.openjpa.entitygraph.EntityGraphEntityManagerFactory;
import io.github.mschieder.spring.boot.openjpa.entitygraph.EntityGraphFactory;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * creates an OpenJPA {@link EntityManagerFactory} proxy to provide an {@link EntityManager}
 * with {@link jakarta.persistence.Tuple} support for native queries.
 */
public class OpenjpaEntityManagerFactoryCreator {
    private static class Handler implements InvocationHandler {

        private final EntityManagerFactory entityManagerFactory;
        private final EntityGraphFactory entityGraphFactory;
        private final OpenJpaProperties openJpaProperties;

        public Handler(EntityManagerFactory entityManagerFactory, EntityGraphFactory entityGraphFactory, OpenJpaProperties openJpaProperties) {
            this.entityManagerFactory = entityManagerFactory;
            this.entityGraphFactory = entityGraphFactory;
            this.openJpaProperties = openJpaProperties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;
            try {
                if (("createEntityManager".equals(method.getName()))) {
                    var entityManager = (EntityManager) method.invoke(entityManagerFactory, args);
                    result = OpenjpaEntityManagerCreator.create(entityManager, entityGraphFactory, openJpaProperties);
                } else if ("addNamedEntityGraph".equals(method.getName())) {
                    new EntityGraphEntityManagerFactory(entityGraphFactory).addNamedEntityGraph((String) args[0], (EntityGraph<? extends Object>) args[1]);
                } else {
                    result = method.invoke(entityManagerFactory, args);
                }
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            return result;
        }
    }

    private OpenjpaEntityManagerFactoryCreator() {
    }

    public static EntityManagerFactory create(EntityManagerFactory entityManagerFactory, EntityGraphFactory entityGraphFactory, OpenJpaProperties openJpaProperties) {
        return (EntityManagerFactory)
                Proxy.newProxyInstance(
                        entityManagerFactory.getClass().getClassLoader(),
                        new Class[]{EntityManagerFactory.class, OpenJPAEntityManagerFactorySPI.class},
                        new Handler(entityManagerFactory, entityGraphFactory, openJpaProperties));
    }
}
