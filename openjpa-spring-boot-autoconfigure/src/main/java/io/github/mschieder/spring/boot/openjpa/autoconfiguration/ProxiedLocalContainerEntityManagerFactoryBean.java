package io.github.mschieder.spring.boot.openjpa.autoconfiguration;

import io.github.mschieder.spring.boot.openjpa.tuple.OpenjpaEntityManagerFactoryCreator;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class ProxiedLocalContainerEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean {

    private final OpenJpaProperties openJpaProperties;

    public ProxiedLocalContainerEntityManagerFactoryBean(LocalContainerEntityManagerFactoryBean defined, OpenJpaProperties openJpaProperties) {
       this.openJpaProperties = openJpaProperties;
        setBootstrapExecutor(defined.getBootstrapExecutor());
        setDataSource(defined.getDataSource());
        setJpaPropertyMap(defined.getJpaPropertyMap());
        setJpaVendorAdapter(defined.getJpaVendorAdapter());
        setPersistenceProvider(defined.getPersistenceProvider());
    }

    @Override
    protected EntityManagerFactory createEntityManagerFactoryProxy(EntityManagerFactory emf) {
        var springProxy = super.createEntityManagerFactoryProxy(emf);
        return OpenjpaEntityManagerFactoryCreator.create(springProxy, openJpaProperties);
    }

}
