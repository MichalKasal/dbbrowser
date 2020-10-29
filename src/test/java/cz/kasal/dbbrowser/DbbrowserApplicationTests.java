package cz.kasal.dbbrowser;

import cz.kasal.dbbrowser.container.DatabaseContainer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
class DbbrowserApplicationTests {

    @Container
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = DatabaseContainer.getInstance();

    @Test
    void contextLoads() {
    }

}
