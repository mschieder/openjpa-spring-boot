package io.github.mschieder.spring.boot.openjpa.tuple;

import io.github.mschieder.spring.boot.openjpa.autoconfiguration.OpenJpaProperties;
import io.github.mschieder.spring.boot.openjpa.nativequery.NativeQueryCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;
import org.apache.openjpa.persistence.QueryImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * creates an OpenJPA {@link EntityManager} with basic {@link Tuple} support.
 */
public class OpenjpaEntityManagerCreator {
    private static class Handler implements InvocationHandler {

        private final EntityManager entityManager;
        private final OpenJpaProperties openJpaProperties;

        public Handler(EntityManager entityManager, OpenJpaProperties openJpaProperties) {
            this.entityManager = entityManager;
            this.openJpaProperties = openJpaProperties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object result;
                if (openJpaProperties.isTupleResultClassSupport() &&
                        ("createNativeQuery".equals(method.getName()) || "createQuery".equals(method.getName()))
                        && method.getParameterCount() == 2
                        && args[1] == Tuple.class) {
                    result = method.invoke(entityManager, args[0], SimpleTupleImpl.class);
                } else {
                    result = method.invoke(entityManager, args);
                }

                if ((openJpaProperties.isNativeQueryReturnPositionalParameters() && ("createNativeQuery".equals(method.getName())))) {
                    result = NativeQueryCreator.create((QueryImpl<?>) result);
                }
                return result;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }

        }
    }

    private OpenjpaEntityManagerCreator() {
    }

    public static EntityManager create(EntityManager entityManager, OpenJpaProperties openJpaProperties) {
        return (EntityManager)
                Proxy.newProxyInstance(
                        entityManager.getClass().getClassLoader(),
                        new Class[]{EntityManager.class, OpenJPAEntityManagerSPI.class},
                        new Handler(entityManager, openJpaProperties));
    }
}
