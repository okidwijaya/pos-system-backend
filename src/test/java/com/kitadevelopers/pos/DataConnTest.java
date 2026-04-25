package com.kitadevelopers.pos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PosApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DataConnTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testConnection() throws Exception {
        System.out.println("Connection started");
        assertThat(dataSource.getConnection()).isNotNull();
        System.out.println("Connection validated for your project!");
    }
}