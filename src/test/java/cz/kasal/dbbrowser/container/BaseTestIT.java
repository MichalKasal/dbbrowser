package cz.kasal.dbbrowser.container;

import org.junit.ClassRule;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;

@ActiveProfiles({"test"})
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(value = {
        "classpath:application.properties",
        "classpath:application-test.properties"
})
public abstract class BaseTestIT {

    @Container
    @ClassRule
    public static DatabaseContainer postgres = DatabaseContainer
            .getInstance();

}