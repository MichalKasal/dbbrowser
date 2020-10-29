package cz.kasal.dbbrowser.container;

import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

public class DatabaseContainer extends PostgreSQLContainer<DatabaseContainer> {

    private static DatabaseContainer container;

    private DatabaseContainer() {
        super();
    }

    public static DatabaseContainer getInstance() {
        if (container == null) {
            container = new DatabaseContainer()
                    .withUsername("postgres")
                    .withPassword("password")
                    .withDatabaseName("test")
                    .withStartupTimeout(Duration.ofSeconds(300));
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

}
