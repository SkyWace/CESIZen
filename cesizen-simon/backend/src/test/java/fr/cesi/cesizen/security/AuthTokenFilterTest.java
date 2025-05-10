package fr.cesi.cesizen.security;

import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private UserDetails userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Clear the security context before each test
        SecurityContextHolder.clearContext();

        // Set up test data
        userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "password",
                "Test", "User",
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        validToken = "valid.jwt.token";
    }

    @Test
    void doFilterInternal_WithValidToken() throws ServletException, IOException {
        // Mock the request to return a valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that the authentication was set in the security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Verify that the filter chain was continued
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken() throws ServletException, IOException {
        // Mock the request to return an invalid token
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that the filter chain was continued
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken() throws ServletException, IOException {
        // Mock the request to return no token
        when(request.getHeader("Authorization")).thenReturn(null);

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that the filter chain was continued
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNonBearerToken() throws ServletException, IOException {
        // Mock the request to return a non-Bearer token
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNzd29yZA==");

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that the filter chain was continued
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithException() throws ServletException, IOException {
        // Mock the request to return a valid token but throw an exception during processing
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(validToken)).thenThrow(new RuntimeException("Test exception"));

        // Call the filter
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Verify that no authentication was set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Verify that the filter chain was continued
        verify(filterChain).doFilter(request, response);
    }
}