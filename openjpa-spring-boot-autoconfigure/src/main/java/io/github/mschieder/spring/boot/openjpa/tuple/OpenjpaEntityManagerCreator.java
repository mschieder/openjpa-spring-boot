package io.github.mschieder.spring.boot.openjpa.tuple;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;

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

        public Handler(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (("createNativeQuery".equals(method.getName()) || "createQuery".equals(method.getName()))
                        && method.getParameterCount() == 2
                        && args[1] == Tuple.class) {
                    return method.invoke(entityManager, args[0], SimpleTupleImpl.class);
                }
                return method.invoke(entityManager, args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }

        }
    }

    private OpenjpaEntityManagerCreator() {
    }

    public static EntityManager create(EntityManager entityManager) {
        return (EntityManager)
                Proxy.newProxyInstance(
                        entityManager.getClass().getClassLoader(),
                        new Class[]{EntityManager.class, OpenJPAEntityManagerSPI.class},
                        new Handler(entityManager));
    }
}
