package io.github.mschieder.spring.boot.openjpa.tuple;

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

        public Handler(EntityManagerFactory entityManagerFactory) {
            this.entityManagerFactory = entityManagerFactory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (("createEntityManager".equals(method.getName()))) {
                var entityManager = (EntityManager) method.invoke(entityManagerFactory, args);
                return OpenjpaEntityManagerCreator.create(entityManager);
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

    public static EntityManagerFactory create(EntityManagerFactory entityManagerFactory) {
        return (EntityManagerFactory)
                Proxy.newProxyInstance(
                        entityManagerFactory.getClass().getClassLoader(),
                        new Class[]{EntityManagerFactory.class, OpenJPAEntityManagerFactorySPI.class},
                        new Handler(entityManagerFactory));
    }
}
