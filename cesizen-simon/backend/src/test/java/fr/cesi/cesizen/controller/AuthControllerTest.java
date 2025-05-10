package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.JwtResponse;
import fr.cesi.cesizen.payload.LoginRequest;
import fr.cesi.cesizen.payload.SignupRequest;
import fr.cesi.cesizen.repository.UserRepository;
import fr.cesi.cesizen.security.JwtTokenProvider;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthController authController;

    private User testUser;
    private UserDetailsImpl userDetails;
    private LoginRequest loginRequest;
    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);

        Set<SimpleGrantedAuthority> authorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());

        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "encodedPassword",
            "Test", "User", authorities);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
    }

    @Test
    void authenticateUser_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("jwtToken");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwtToken", jwtResponse.getAccessToken());
        assertEquals(testUser.getId(), jwtResponse.getId());
        assertEquals(testUser.getUsername(), jwtResponse.getUsername());
        assertEquals(testUser.getEmail(), jwtResponse.getEmail());
        assertEquals(testUser.getFirstName(), jwtResponse.getFirstName());
        assertEquals(testUser.getLastName(), jwtResponse.getLastName());
        assertEquals(new HashSet<>(testUser.getRoles()), new HashSet<>(jwtResponse.getRoles()));
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully!", response.getBody());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameTaken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error: Username is already taken!", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailTaken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Error: Email is already in use!", response.getBody());
        verify(userRepository, never()).save(any(User.class));
    }
} 
