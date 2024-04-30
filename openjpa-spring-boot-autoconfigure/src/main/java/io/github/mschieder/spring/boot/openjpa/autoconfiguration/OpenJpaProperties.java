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

    public String getDdlAuto() {
        return this.ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
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
