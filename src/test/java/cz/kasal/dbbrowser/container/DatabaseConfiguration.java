package cz.kasal.dbbrowser.container;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Contains Beans required to successfully run tests
 * against containerized PostgreSQL database
 */
@TestConfiguration
public class DatabaseConfiguration {

    /**
     * Provides configuration of test postgres container
     *
     * @return fully configured postgresqlContainer bean
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer postgresqlContainer() {
        PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:13.0")
                .withUsername("postgres")
                .withPassword("password")
                .withDatabaseName("test")
                .withExposedPorts(35432)
                .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\n", 2));
        container.setPortBindings(Arrays.asList("35432:5432"));
        return container;
    }


    /**
     * Configures datasource used for tests
     *
     * @param postgres represents testcontainer postgresql
     * @return fully configured test datasource
     */
    @Bean
    public DataSource dataSource(PostgreSQLContainer postgres) {
        DataSourceBuilder ds = DataSourceBuilder.create();
        ds.driverClassName("org.postgresql.Driver");
        ds.url(postgres.getJdbcUrl());
        ds.username(postgres.getUsername());
        ds.password(postgres.getPassword());
        return ds.build();
    }


    /**
     * Configures liquibase using test datasource. This bean needs postgresql container
     * to be fully initialized and accepting connections
     *
     * @param dataSource test datasource
     * @return configured liquibase bean
     */
    @Bean
    @DependsOn(value = {"postgresqlContainer", "dataSource"})
    public SpringLiquibase springLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDropFirst(true);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema("public");
        liquibase.setChangeLog("classpath:/db/changelog/db.changelog-master.xml");
        return liquibase;
    }
}
