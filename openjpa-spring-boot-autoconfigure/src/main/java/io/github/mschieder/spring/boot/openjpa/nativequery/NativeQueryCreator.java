package io.github.mschieder.spring.boot.openjpa.nativequery;

import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import org.apache.openjpa.persistence.ParameterImpl;
import org.apache.openjpa.persistence.QueryImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class NativeQueryCreator {
    private static class Handler implements InvocationHandler {

        private final QueryImpl<?> query;
        private static final Pattern POSITIONAL_PATTERN = Pattern.compile("\\?(\\d+)");


        public Handler(QueryImpl<?> query) {
            this.query = query;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Object result;
                if ("getParameters".equals(method.getName())) {
                    Set<Parameter<?>> parameters = new HashSet<>();
                    var matcher = POSITIONAL_PATTERN.matcher(query.getQueryString());
                    while (matcher.find()) {
                        var position = Integer.valueOf(matcher.group(1));
                        parameters.add(new ParameterImpl<>(position, Void.class));
                    }
                    result = parameters;
                } else if ("unwrap".equals(method.getName())) {
                    return Proxy.isProxyClass(query.getClass()) ? query.unwrap((Class<?>) args[0]) : query;
                } else {
                    result = method.invoke(query, args);
                }
                return result;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    public static Query create(QueryImpl query) {
        return (Query)
                Proxy.newProxyInstance(
                        query.getClass().getClassLoader(),
                        new Class[]{Query.class},
                        new Handler(query));
    }


    private NativeQueryCreator() {
    }


}
