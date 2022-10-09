package pl.newsler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

@Slf4j
public class NLEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String driverClass = environment.getProperty("spring.datasource.driverClassName");
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        try {
            DriverManager.registerDriver((Driver) Class.forName(driverClass).getConstructor().newInstance());
            try (Connection connection = DriverManager.getConnection(url, username, password);) {
//                environment.getPropertySources().addFirst();
                log.info("Configuration properties were loaded from the database via manual connection creation");
            }
        } catch (Exception e) {
            log.error("Error creating properties from database with manual connection creation.", e);
        }
    }
}
