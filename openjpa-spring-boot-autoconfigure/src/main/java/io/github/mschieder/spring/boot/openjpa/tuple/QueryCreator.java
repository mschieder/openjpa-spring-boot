package io.github.mschieder.spring.boot.openjpa.tuple;

import io.github.mschieder.spring.boot.openjpa.autoconfiguration.OpenJpaProperties;
import io.github.mschieder.spring.boot.openjpa.entitygraph.EntityGraphQuery;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class QueryCreator {
    private static class Handler implements InvocationHandler {

        private final Query query;
        private final EntityGraphQuery entityGraphQuery;
        private final OpenJpaProperties openJpaProperties;


        public Handler(Query query, OpenJpaProperties openJpaProperties) {
            this.query = query;
            this.entityGraphQuery = new EntityGraphQuery(OpenJPAPersistence.cast(query).getEntityManager().getEntityManagerFactory());
            this.openJpaProperties = openJpaProperties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("setHint".equals(method.getName()) && openJpaProperties.isEntityGraphSupport()) {
                return entityGraphQuery.setHint((String) args[0], args[1], (TypedQuery<?>) query);

            } else if ("unwrap".equals(method.getName())) {
                return Proxy.isProxyClass(query.getClass()) ? query.unwrap((Class<?>) args[0]) : query;
            }
            try {
                return method.invoke(query, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }

        }
    }

    private QueryCreator() {
    }

    public static Query create(Query query, OpenJpaProperties openJpaProperties) {
        return (Query)
                Proxy.newProxyInstance(
                        query.getClass().getClassLoader(),
                        new Class[]{Query.class, TypedQuery.class},
                        new QueryCreator.Handler(query, openJpaProperties));
    }
}
