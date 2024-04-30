package org;

import io.github.mschieder.spring.boot.openjpa.OpenJpaDialect;
import io.github.mschieder.spring.boot.openjpa.OpenJpaVendorAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class OpenJpaConfig {//extends JpaBaseConfiguration {
//    protected OpenJpaConfig(DataSource dataSource, JpaProperties properties,
//                               ObjectProvider<JtaTransactionManager> jtaTransactionManager) {
//        super(dataSource, properties, jtaTransactionManager);
//    }
//
//    @Override
//    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
//        return new OpenJpaVendorAdapter();
//
//    }
//
//    @Override
//    protected Map<String, Object> getVendorProperties() {
//        return Map.of("openjpa.RuntimeUnenhancedClasses","supported", "openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
//
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
//        lef.setDataSource(dataSource);
//        lef.setJpaVendorAdapter(createJpaVendorAdapter());
//        lef.setPackagesToScan(this.getClass().getPackageName());
//        lef.setJpaDialect(new OpenJpaDialect());
//        return lef;
//    }
}