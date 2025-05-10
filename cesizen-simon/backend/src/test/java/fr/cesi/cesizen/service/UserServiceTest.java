package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.SignupRequest;
import fr.cesi.cesizen.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
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

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setRoles(new HashSet<>(Arrays.asList("USER")));
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(signupRequest);

        assertNotNull(createdUser);
        assertEquals(testUser, createdUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameTaken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(signupRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailTaken() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(signupRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(1L);

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_AdminUser() {
        User adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void updateLastLogin_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateLastLogin("testuser");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void existsByUsername_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByEmail_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void changePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(1L, "currentPassword", "newPassword");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_WrongCurrentPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, 
            () -> userService.changePassword(1L, "wrongPassword", "newPassword"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void generatePasswordResetToken_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String token = userService.generatePasswordResetToken("test@example.com");

        assertNotNull(token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_Success() {
        String token = "validToken";
        testUser.setResetPasswordToken(token);
        testUser.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.resetPassword(token, "newPassword");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_InvalidToken() {
        when(userRepository.findByResetPasswordToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> userService.resetPassword("invalidToken", "newPassword"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_ExpiredToken() {
        String token = "expiredToken";
        testUser.setResetPasswordToken(token);
        testUser.setResetPasswordTokenExpiry(LocalDateTime.now().minusHours(1));

        when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, 
            () -> userService.resetPassword(token, "newPassword"));
        verify(userRepository, never()).save(any(User.class));
    }
} 