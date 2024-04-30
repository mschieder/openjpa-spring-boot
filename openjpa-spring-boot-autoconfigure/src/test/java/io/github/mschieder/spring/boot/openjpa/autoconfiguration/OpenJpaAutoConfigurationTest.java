package io.github.mschieder.spring.boot.openjpa.autoconfiguration;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;


class OpenJpaAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    OpenJpaAutoConfiguration.class
                    , DataSourceAutoConfiguration.class
            ));

    @Configuration
    static class UserConfiguration {

    }

    @Test
    void testDefaultConfiguration() {
        this.contextRunner.withUserConfiguration(UserConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DataSource.class);
                    assertThat(context).hasSingleBean(EntityManagerFactory.class);
                });
    }
}