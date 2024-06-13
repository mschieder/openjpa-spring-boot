package io.github.mschieder.spring.boot.openjpa.tuple;

import io.github.mschieder.spring.boot.openjpa.autoconfiguration.OpenJpaProperties;
import io.github.mschieder.spring.boot.openjpa.entitygraph.EntityGraphEntityManager;
import io.github.mschieder.spring.boot.openjpa.entitygraph.EntityGraphFactory;
import io.github.mschieder.spring.boot.openjpa.nativequery.NativeQueryCreator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
        private final EntityGraphFactory entityGraphFactory;
        private final OpenJpaProperties openJpaProperties;

        public Handler(EntityManager entityManager, EntityGraphFactory entityGraphFactory, OpenJpaProperties openJpaProperties) {
            this.entityManager = entityManager;
            this.entityGraphFactory = entityGraphFactory;
            this.openJpaProperties = openJpaProperties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            EntityGraphEntityManager entityGraphEntityManager = new EntityGraphEntityManager(entityGraphFactory);

            try {
                Object result = null;
                if (openJpaProperties.isTupleResultClassSupport() &&
                        ("createNativeQuery".equals(method.getName()) || "createQuery".equals(method.getName()))
                        && method.getParameterCount() == 2
                        && args[1] == Tuple.class) {
                    result = method.invoke(entityManager, args[0], SimpleTupleImpl.class);
                } else if (openJpaProperties.isEntityGraphSupport()) {
                    if ("createEntityGraph".equals(method.getName())) {
                        if (args[0] instanceof String graphName) {
                            result = entityGraphEntityManager.createEntityGraph(graphName);
                        } else {
                            result = entityGraphEntityManager.createEntityGraph((Class<?>) args[0]);
                        }
                    } else if ("getEntityGraph".equals(method.getName())) {
                        result = entityGraphEntityManager.getEntityGraph((String) args[0]);
                    } else if ("getEntityGraphs".equals(method.getName())) {
                        result = entityGraphEntityManager.getEntityGraphs((Class<?>) args[0]);
                    }
                }
                if (result == null) {
                    if ("createQuery".equals(method.getName())) {
                        Query query = (Query) method.invoke(entityManager, args);
                        result = QueryCreator.create(query, openJpaProperties);
                    } else {
                        result = method.invoke(entityManager, args);
                    }
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

    public static EntityManager create(EntityManager entityManager, EntityGraphFactory entityGraphFactory, OpenJpaProperties openJpaProperties) {
        return (EntityManager)
                Proxy.newProxyInstance(
                        entityManager.getClass().getClassLoader(),
                        new Class[]{EntityManager.class, OpenJPAEntityManagerSPI.class},
                        new Handler(entityManager, entityGraphFactory, openJpaProperties));
    }
}
