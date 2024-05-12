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

        public Handler(QueryImpl<?> query) {
            this.query = query;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if ("getParameters".equals(method.getName())) {
                    Set<Parameter<?>> parameters = new HashSet<>();
                    var queryString = query.getQueryString();
                    Pattern p = Pattern.compile("\\?(\\d+)");
                    var matcher = p.matcher(queryString);
                    while (matcher.find()) {
                        var position = Integer.valueOf(matcher.group(1));
                        parameters.add(new ParameterImpl<>(position, Void.class));
                    }
                    return parameters;
                }

                return method.invoke(query, args);
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
