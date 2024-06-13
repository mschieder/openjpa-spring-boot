package io.github.mschieder.spring.boot.openjpa.autoconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ConfigurationProperties("spring.jpa.openjpa")
public class OpenJpaProperties {
    /**
     * DDL mode. This is actually a shortcut for the "hibernate.hbm2ddl.auto" property.
     * Defaults to "create-drop" when using an embedded database and no schema manager was
     * detected. Otherwise, defaults to "none".
     */
    private String ddlAuto;

    /**
     * Query.getParameters(): returns of the positional parameters of a native query. [spec: "This method is not required to be supported for native queries."] (default: true)
     */
    private boolean nativeQueryReturnPositionalParameters = true;

    /**
     * adds basic Tuple support for EntityManager.createQuery() and EntityManager.createNativeQuery() [spec: "only for CriteriaQueries"] (default: true)
     */
    private boolean tupleResultClassSupport = true;

    /**
     * adds JPA 2.1 support for Entity Graphs (default: true)
     */
    private boolean entityGraphSupport = true;


    public String getDdlAuto() {
        return this.ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public boolean isNativeQueryReturnPositionalParameters() {
        return nativeQueryReturnPositionalParameters;
    }

    public void setNativeQueryReturnPositionalParameters(boolean nativeQueryReturnPositionalParameters) {
        this.nativeQueryReturnPositionalParameters = nativeQueryReturnPositionalParameters;
    }

    public boolean isTupleResultClassSupport() {
        return tupleResultClassSupport;
    }

    public void setTupleResultClassSupport(boolean tupleResultClassSupport) {
        this.tupleResultClassSupport = tupleResultClassSupport;
    }

    public boolean isEntityGraphSupport() {
        return entityGraphSupport;
    }

    public void setEntityGraphSupport(boolean entityGraphSupport) {
        this.entityGraphSupport = entityGraphSupport;
    }

    public Map<String, Object> determineOpenJpaProperties(Map<String, String> jpaProperties,
                                                          OpenJpaSettings settings) {


        return getAdditionalProperties(jpaProperties, settings);
    }

    private Map<String, Object> getAdditionalProperties(Map<String, String> existing, OpenJpaSettings settings) {
        Map<String, Object> result = new HashMap<>(existing);

        String ddlAutoResult = determineDdlAuto(existing, settings::getDdlAuto);
        if (StringUtils.hasText(ddlAutoResult) && !"none".equals(ddlAutoResult)) {
            result.put(OpenJpaConstants.SYNCHRONIZE_MAPPINGS, ddlAutoResult);
        } else {
            result.remove(OpenJpaConstants.SYNCHRONIZE_MAPPINGS);
        }
        Collection<OpenJpaPropertiesCustomizer> customizers = settings.getOpenJpaPropertiesCustomizers();
        if (!ObjectUtils.isEmpty(customizers)) {
            customizers.forEach(customizer -> customizer.customize(result));
        }
        return result;
    }


    private String determineDdlAuto(Map<String, String> existing, Supplier<String> defaultDdlAuto) {
        String configuredDdlAuto = existing.get(OpenJpaConstants.SYNCHRONIZE_MAPPINGS);
        if (configuredDdlAuto != null) {
            return configuredDdlAuto;
        }
        if (this.ddlAuto != null) {
            return this.ddlAuto;
        }
        return defaultDdlAuto.get();
    }
}
