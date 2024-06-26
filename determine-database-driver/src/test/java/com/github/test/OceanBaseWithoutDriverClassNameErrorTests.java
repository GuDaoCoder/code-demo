package com.github.test;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("oceanbase-without-driver-class-name")
class OceanBaseWithoutDriverClassNameErrorTests {

    @Autowired
    HikariDataSource dataSource;

    @Test
    public void testDriver() {
        assertEquals("com.oceanbase.jdbc.Driver", dataSource.getDriverClassName());
    }

}
