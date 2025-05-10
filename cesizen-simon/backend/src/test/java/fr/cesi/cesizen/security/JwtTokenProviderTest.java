package fr.cesi.cesizen.security;

import fr.cesi.cesizen.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Authentication authentication;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Set up test values for JWT properties
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "testSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600000); // 1 hour

        // Set up test user details
        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password",
            "Test", "User", new HashSet<>());

        // Set up mock authentication
        authentication = mock(Authentication.class);
    }

    @Test
    void generateToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT should have 3 parts
    }

    @Test
    void getUsernameFromToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertNotNull(username);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_MalformedJwtException() {
        boolean isValid = jwtTokenProvider.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        // Set expiration to 0 to create an expired token
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 0);
        String token = jwtTokenProvider.generateToken(authentication);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidSignature() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtTokenProvider.generateToken(authentication);
        // Change the secret key to invalidate the signature
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "differentSecretKey123456789012345678901234567890");
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void validateToken_UnsupportedJwtException() {
        // Create a token with an unsupported algorithm
        String unsupportedToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ0ZXN0dXNlciJ9.";

        boolean isValid = jwtTokenProvider.validateToken(unsupportedToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_IllegalArgumentException() {
        // Test with null or empty token
        boolean isValidNull = jwtTokenProvider.validateToken(null);
        boolean isValidEmpty = jwtTokenProvider.validateToken("");

        assertFalse(isValidNull);
        assertFalse(isValidEmpty);
    }
} 
