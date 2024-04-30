package io.github.mschieder.spring.boot.openjpa.autoconfiguration;


import java.util.Map;

@FunctionalInterface
public interface OpenJpaPropertiesCustomizer {

    /**
     * Customize the specified JPA vendor properties.
     * @param openJpaProperties the JPA vendor properties to customize
     */
    void customize(Map<String, Object> openJpaProperties);
}
