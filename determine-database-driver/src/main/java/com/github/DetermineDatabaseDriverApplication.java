package com.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
public class DetermineDatabaseDriverApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetermineDatabaseDriverApplication.class, args);
    }

}
