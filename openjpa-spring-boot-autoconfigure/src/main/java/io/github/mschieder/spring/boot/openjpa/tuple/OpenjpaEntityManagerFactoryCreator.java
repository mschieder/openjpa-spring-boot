package io.github.mschieder.spring.boot.openjpa.tuple;

import io.github.mschieder.spring.boot.openjpa.autoconfiguration.OpenJpaProperties;
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
        private final OpenJpaProperties openJpaProperties;

        public Handler(EntityManagerFactory entityManagerFactory, OpenJpaProperties openJpaProperties) {
            this.entityManagerFactory = entityManagerFactory;
            this.openJpaProperties = openJpaProperties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (("createEntityManager".equals(method.getName()))) {
                var entityManager = (EntityManager) method.invoke(entityManagerFactory, args);
                return OpenjpaEntityManagerCreator.create(entityManager, openJpaProperties);
            }
            try {
                return method.invoke(entityManagerFactory, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }

        }
    }

    private OpenjpaEntityManagerFactoryCreator() {
    }

    public static EntityManagerFactory create(EntityManagerFactory entityManagerFactory, OpenJpaProperties openJpaProperties) {
        return (EntityManagerFactory)
                Proxy.newProxyInstance(
                        entityManagerFactory.getClass().getClassLoader(),
                        new Class[]{EntityManagerFactory.class, OpenJPAEntityManagerFactorySPI.class},
                        new Handler(entityManagerFactory, openJpaProperties));
    }
}
