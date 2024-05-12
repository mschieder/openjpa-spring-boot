/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mschieder.spring.boot.openjpa.autoconfiguration;


import io.github.mschieder.spring.boot.openjpa.OpenJpaVendorAdapter;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeHint.Builder;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.SchemaManagementProvider;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link JpaBaseConfiguration} implementation for Hibernate.
 *
 * @author Phillip Webb
 * @author Josh Long
 * @author Manuel Doninger
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenJpaProperties.class)
//@ConditionalOnSingleCandidate(DataSource.class)
class OpenJpaConfiguration extends JpaBaseConfiguration {
    @Autowired
    private OpenJpaProperties openJpaProperties;
    private static final Log logger = LogFactory.getLog(OpenJpaConfiguration.class);

    private static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";

    private static final String PROVIDER_DISABLES_AUTOCOMMIT = "hibernate.connection.provider_disables_autocommit";

    /**
     * {@code NoJtaPlatform} implementations for various Hibernate versions.
     */
    private static final String[] NO_JTA_PLATFORM_CLASSES = {
            "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform",
            "org.hibernate.service.jta.platform.internal.NoJtaPlatform"};

    private final OpenJpaProperties openjpaProperties;

    private final OpenJpaDefaultDdlAutoProvider defaultDdlAutoProvider;

    private final DataSourcePoolMetadataProvider poolMetadataProvider;

    private final List<OpenJpaPropertiesCustomizer> openJpaPropertiesCustomizers;

    OpenJpaConfiguration(DataSource dataSource, JpaProperties jpaProperties,
                         ConfigurableListableBeanFactory beanFactory, ObjectProvider<JtaTransactionManager> jtaTransactionManager,
                         OpenJpaProperties openjpaProperties,
                         ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders,
                         ObjectProvider<SchemaManagementProvider> providers,
                         ObjectProvider<OpenJpaPropertiesCustomizer> openJpaPropertiesCustomizers) {
        super(dataSource, jpaProperties, jtaTransactionManager);
        this.openjpaProperties = openjpaProperties;
        this.defaultDdlAutoProvider = new OpenJpaDefaultDdlAutoProvider(providers);
        this.poolMetadataProvider = new CompositeDataSourcePoolMetadataProvider(metadataProviders.getIfAvailable());
        this.openJpaPropertiesCustomizers = determineOpenJpaPropertiesCustomizers(beanFactory,
                openJpaPropertiesCustomizers.orderedStream().toList());
    }

    private List<OpenJpaPropertiesCustomizer> determineOpenJpaPropertiesCustomizers(
            ConfigurableListableBeanFactory beanFactory,
            List<OpenJpaPropertiesCustomizer> hibernatePropertiesCustomizers) {
        List<OpenJpaPropertiesCustomizer> customizers = new ArrayList<>();
        if (ClassUtils.isPresent("org.hibernate.resource.beans.container.spi.BeanContainer",
                getClass().getClassLoader())) {
//            customizers.add((properties) -> properties.put(AvailableSettings.BEAN_CONTAINER,
//                    new SpringBeanContainer(beanFactory)));
        }

        customizers.addAll(hibernatePropertiesCustomizers);
        return customizers;
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new OpenJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        Supplier<String> defaultDdlMode = () -> this.defaultDdlAutoProvider.getDefaultDdlAuto(getDataSource());
        return new LinkedHashMap<>(this.openjpaProperties.determineOpenJpaProperties(
                getProperties().getProperties(), new OpenJpaSettings().ddlAuto(defaultDdlMode)
                        .openJpaPropertiesCustomizers(this.openJpaPropertiesCustomizers)));
    }

    @Override
    protected void customizeVendorProperties(Map<String, Object> vendorProperties) {
        super.customizeVendorProperties(vendorProperties);
        if (!vendorProperties.containsKey(JTA_PLATFORM)) {
            configureJtaPlatform(vendorProperties);
        }
        if (!vendorProperties.containsKey(PROVIDER_DISABLES_AUTOCOMMIT)) {
            configureProviderDisablesAutocommit(vendorProperties);
        }
    }

    private void configureJtaPlatform(Map<String, Object> vendorProperties) throws LinkageError {
//        JtaTransactionManager jtaTransactionManager = getJtaTransactionManager();
//        // Make sure Hibernate doesn't attempt to auto-detect a JTA platform
//        if (jtaTransactionManager == null) {
//            vendorProperties.put(JTA_PLATFORM, getNoJtaPlatformManager());
//        }
//        // As of Hibernate 5.2, Hibernate can fully integrate with the WebSphere
//        // transaction manager on its own.
//        else if (!runningOnWebSphere()) {
//            configureSpringJtaPlatform(vendorProperties, jtaTransactionManager);
//        }
    }

    private void configureProviderDisablesAutocommit(Map<String, Object> vendorProperties) {
        if (isDataSourceAutoCommitDisabled() && !isJta()) {
            vendorProperties.put(PROVIDER_DISABLES_AUTOCOMMIT, "true");
        }
    }

    private boolean isDataSourceAutoCommitDisabled() {
        DataSourcePoolMetadata poolMetadata = this.poolMetadataProvider.getDataSourcePoolMetadata(getDataSource());
        return poolMetadata != null && Boolean.FALSE.equals(poolMetadata.getDefaultAutoCommit());
    }

    private boolean runningOnWebSphere() {
        return ClassUtils.isPresent("com.ibm.websphere.jtaextensions.ExtendedJTATransaction",
                getClass().getClassLoader());
    }

    private void configureSpringJtaPlatform(Map<String, Object> vendorProperties,
                                            JtaTransactionManager jtaTransactionManager) {
        try {
            vendorProperties.put(JTA_PLATFORM, new SpringJtaPlatform(jtaTransactionManager));
        } catch (LinkageError ex) {
            // NoClassDefFoundError can happen if Hibernate 4.2 is used and some
            // containers (e.g. JBoss EAP 6) wrap it in the superclass LinkageError
            if (!isUsingJndi()) {
                throw new IllegalStateException(
                        "Unable to set Hibernate JTA platform, are you using the correct version of Hibernate?", ex);
            }
            // Assume that Hibernate will use JNDI
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to set Hibernate JTA platform : " + ex.getMessage());
            }
        }
    }

    private boolean isUsingJndi() {
        try {
            return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
        } catch (Error ex) {
            return false;
        }
    }

    private Object getNoJtaPlatformManager() {
        for (String candidate : NO_JTA_PLATFORM_CLASSES) {
            try {
                return Class.forName(candidate).getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                // Continue searching
            }
        }
        throw new IllegalStateException(
                "No available JtaPlatform candidates amongst " + Arrays.toString(NO_JTA_PLATFORM_CLASSES));
    }


    static class HibernateRuntimeHints implements RuntimeHintsRegistrar {

        private static final Consumer<Builder> INVOKE_DECLARED_CONSTRUCTORS = TypeHint
                .builtWith(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            for (String noJtaPlatformClass : NO_JTA_PLATFORM_CLASSES) {
                hints.reflection().registerType(TypeReference.of(noJtaPlatformClass), INVOKE_DECLARED_CONSTRUCTORS);
            }
            hints.reflection().registerType(SpringImplicitNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
            // hints.reflection().registerType(CamelCaseToUnderscoresNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
        }

    }

    @Bean
    @Primary
    @Override
    @ConditionalOnMissingBean({LocalContainerEntityManagerFactoryBean.class, EntityManagerFactory.class})
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder,
                                                                       PersistenceManagedTypes persistenceManagedTypes) {
        var defined = super.entityManagerFactory(factoryBuilder, persistenceManagedTypes);
        if (openJpaProperties.isNativeQueryReturnPositionalParameters() || openJpaProperties.isTupleResultClassSupport()) {
            return new ProxiedLocalContainerEntityManagerFactoryBean(defined, openJpaProperties);
        } else {
            return defined;
        }
    }
}

