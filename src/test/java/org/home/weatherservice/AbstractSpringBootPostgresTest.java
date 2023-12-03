package org.home.weatherservice;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.sql.init.DatabaseInitializationMode.ALWAYS;
import static org.springframework.boot.sql.init.DatabaseInitializationMode.NEVER;


/**
 * Класс-предок для тестов, которые можно запускать в общем контексте и с общей базой.
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Testcontainers
public abstract class AbstractSpringBootPostgresTest {

    static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:15")
            .withInitScript("create-db.sql")
            .withUsername("test_user")
            .withPassword("7PAeQ9YEsSaMTaZe");

    private static boolean initializeDatasource = true;

    static {
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        String initializationMode = initializeDatasource ? ALWAYS.name() : NEVER.name();
        registry.add("spring.datasource.initialization-mode", () -> initializationMode);
        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", CONTAINER::getUsername);
        registry.add("spring.datasource.password", CONTAINER::getPassword);
        initializeDatasource = false;
    }

}
