package fr.cesi.cesizen.security.services;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);
    }

    @Test
    void loadUserByUsername_Success() {
        // Mock the repository to return the test user
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Call the service method
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Verify the result
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Verify the repository was called
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Mock the repository to return empty
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentuser");
        });

        // Verify the exception message
        assertEquals("User Not Found with username: nonexistentuser", exception.getMessage());

        // Verify the repository was called
        verify(userRepository).findByUsername("nonexistentuser");
    }
}