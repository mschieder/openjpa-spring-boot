/*
 * Copyright 2002-2014 the original author or authors.
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

package io.github.mschieder.spring.boot.openjpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceProvider;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAEntityManagerSPI;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.springframework.orm.jpa.JpaVendorAdapter} implementation for Apache OpenJPA.
 * Developed and tested against OpenJPA 2.2.
 *
 * <p>Exposes OpenJPA's persistence provider and EntityManager extension interface,
 * and adapts {@link org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter}'s common configuration settings.
 * No support for the detection of annotated packages (through
 * {@link org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo#getManagedPackages()})
 * since OpenJPA doesn't use package-level metadata.
 *
 * @author Juergen Hoeller
 * @author Costin Leau
 * @see OpenJpaDialect
 * @see org.apache.openjpa.persistence.PersistenceProviderImpl
 * @see org.apache.openjpa.persistence.OpenJPAEntityManager
 * @since 2.0
 */
public class OpenJpaVendorAdapter extends AbstractJpaVendorAdapter {

    private final PersistenceProvider persistenceProvider = new PersistenceProviderImpl();

    private final OpenJpaDialect jpaDialect = new OpenJpaDialect();


    @Override
    public PersistenceProvider getPersistenceProvider() {
        return this.persistenceProvider;
    }

    @Override
    public String getPersistenceProviderRootPackage() {
        return "org.apache.openjpa";
    }

    @Override
    public Map<String, Object> getJpaPropertyMap() {
        Map<String, Object> jpaProperties = new HashMap<>();

        // use slf4j for logging
        jpaProperties.put("openjpa.Log", "slf4j");

        if (getDatabasePlatform() != null) {
            jpaProperties.put("openjpa.jdbc.DBDictionary", getDatabasePlatform());
        } else {
            String databaseDictonary = determineDatabaseDictionary(getDatabase());
            if (databaseDictonary != null) {
                jpaProperties.put("openjpa.jdbc.DBDictionary", databaseDictonary);
            }
        }

        if (isGenerateDdl()) {
            jpaProperties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
        }
        if (isShowSql()) {
            LoggingSystemFactory.fromSpringFactories().getLoggingSystem(this.getClass().getClassLoader())
                    .setLogLevel("openjpa.jdbc.SQL", LogLevel.TRACE);
        }
        return jpaProperties;

    }

    /**
     * Determine the OpenJPA database dictionary name for the given database.
     *
     * @param database the specified database
     * @return the OpenJPA database dictionary name, or {@code null} if none found
     */
    protected String determineDatabaseDictionary(Database database) {
        return switch (database) {
            case DB2 -> "db2";
            case DERBY -> "derby";
            case H2 -> "h2";
            case HSQL -> "hsql(SimulateLocking=true)";
            case INFORMIX -> "informix";
            case MYSQL -> "mysql";
            case ORACLE -> "oracle";
            case POSTGRESQL -> "postgres";
            case SQL_SERVER -> "sqlserver";
            case SYBASE -> "sybase";
            default -> null;
        };
    }

    @Override
    public OpenJpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    @Override
    public Class<? extends EntityManagerFactory> getEntityManagerFactoryInterface() {
        return OpenJPAEntityManagerFactorySPI.class;
    }

    @Override
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return OpenJPAEntityManagerSPI.class;
    }

}




