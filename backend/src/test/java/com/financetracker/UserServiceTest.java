package com.financetracker;

import com.financetracker.dto.AuthResponse;
import com.financetracker.dto.LoginRequest;
import com.financetracker.dto.RegisterRequest;
import com.financetracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testRegisterAndLogin() {
        RegisterRequest registerReq = new RegisterRequest("Test User", "test@example.com", "password123");
        AuthResponse regResponse = userService.register(registerReq);

        assertTrue(regResponse.isSuccess());
        assertNotNull(regResponse.getUser());
        assertEquals("Test User", regResponse.getUser().getName());
        assertEquals("test@example.com", regResponse.getUser().getEmail());

        LoginRequest loginReq = new LoginRequest("test@example.com", "password123");
        AuthResponse loginResponse = userService.login(loginReq);

        assertTrue(loginResponse.isSuccess());
        assertEquals("test@example.com", loginResponse.getUser().getEmail());
    }

    @Test
    void testLoginWithInvalidPassword() {
        RegisterRequest registerReq = new RegisterRequest("John Doe", "john@example.com", "secret123");
        userService.register(registerReq);

        LoginRequest loginReq = new LoginRequest("john@example.com", "wrongpassword");
        assertThrows(RuntimeException.class, () -> userService.login(loginReq));
    }
}
