package io.github.mschieder.spring.boot.openjpa.tuple;

import jakarta.persistence.TupleElement;

class SimpleTupleElementImpl<X> implements TupleElement<X> {
    private final Class<? extends X> javaType;
    private final String alias;

    public SimpleTupleElementImpl(Class<? extends X> javaType, String alias) {
        this.javaType = javaType;
        this.alias = alias;
    }

    @Override
    public Class<? extends X> getJavaType() {
        return javaType;
    }

    @Override
    public String getAlias() {
        return alias;
    }

}
