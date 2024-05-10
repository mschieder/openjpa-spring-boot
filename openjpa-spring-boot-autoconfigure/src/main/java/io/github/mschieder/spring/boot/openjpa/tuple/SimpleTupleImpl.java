package io.github.mschieder.spring.boot.openjpa.tuple;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SimpleTupleImpl implements Tuple {

    private List<Object> values = new ArrayList<>();
    private final Map<String, Object> aliasToValueMap = new LinkedHashMap<>();

    /**
     * generic put method for OpenJPA
     */
    public void put(Object alias, Object tuple) {
        aliasToValueMap.put(lookupKey(alias), tuple);
        this.values.add(tuple);
    }

    @Override
    public <X> X get(String alias, Class<X> type) {
        final Object value = get(alias);
        return type.cast(value);
    }

    @Override
    public Object get(String alias) {
        var tuple = aliasToValueMap.get(lookupKey(alias));
        if (tuple == null) {
            throw new IllegalArgumentException(alias);
        }
        return tuple;
    }

    @Override
    public <X> X get(int i, Class<X> type) {
        final Object untyped = get(i);

        return (untyped != null) ? type.cast(untyped) : null;
    }

    @Override
    public Object get(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("index must not be < 0");
        }
        if (i >= values.size()) {
            throw new IllegalArgumentException("index > max. size");
        }
        return values.get(i);
    }

    @Override
    public List<TupleElement<?>> getElements() {
        List<TupleElement<?>> result = new ArrayList<>();
        aliasToValueMap.forEach((k, v) -> result.add(new SimpleTupleElementImpl<>(v != null ? v.getClass() : Object.class, k)));
        return result;
    }

    @Override
    public <X> X get(TupleElement<X> tupleElement) {
        return get(tupleElement.getAlias(), tupleElement.getJavaType());
    }


    @Override
    public Object[] toArray() {
        return values.toArray(new Object[values.size()]);
    }

    @Override
    public String toString() {
        return values.toString();
    }

    private String lookupKey(Object alias) {
        return alias.toString().toLowerCase(Locale.ROOT);
    }

}
