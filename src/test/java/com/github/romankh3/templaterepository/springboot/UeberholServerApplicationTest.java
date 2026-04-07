package com.github.romankh3.templaterepository.springboot;

import com.example.ueberholserver.UeberholServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = UeberholServerApplication.class)
class UeberholServerApplicationTest {
    @Test
    void testLoadContext() {
        assertTrue(true);
    }
}
