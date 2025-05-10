package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.SignupRequest;
import fr.cesi.cesizen.payload.request.ProfileUpdateRequest;
import fr.cesi.cesizen.payload.request.PasswordUpdateRequest;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.UserService;
import fr.cesi.cesizen.service.strategy.ProfileInfoUpdateStrategy;
import fr.cesi.cesizen.service.strategy.PasswordUpdateStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ProfileInfoUpdateStrategy profileInfoUpdateStrategy;

    @Mock
    private PasswordUpdateStrategy passwordUpdateStrategy;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", 
            "Test", "User", null);
    }

    @Test
    void createUser_Success() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password");

        when(userService.createUser(any(SignupRequest.class))).thenReturn(testUser);

        ResponseEntity<?> response = userController.createUser(signupRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).createUser(signupRequest);
    }

    @Test
    void getCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<User> response = userController.getCurrentUser();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(users, response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUser, response.getBody());
        verify(userService).updateUser(1L, updatedUser);
    }

    @Test
    void deleteUser_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(userService).deleteUser(1L);
    }

    @Test
    void updateCurrentUser_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setEmail("updated@example.com");

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.existsByUsername("updateduser")).thenReturn(false);
        when(profileInfoUpdateStrategy.update(any(User.class), any(ProfileUpdateRequest.class)))
            .thenReturn(testUser);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.updateCurrentUser(updateRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService).updateUser(1L, testUser);
    }

    @Test
    void updatePassword_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        PasswordUpdateRequest passwordRequest = new PasswordUpdateRequest();
        passwordRequest.setCurrentPassword("oldPassword");
        passwordRequest.setNewPassword("newPassword");

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(passwordUpdateStrategy.update(any(User.class), any(PasswordUpdateRequest.class)))
            .thenReturn(testUser);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = userController.updatePassword(passwordRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testUser, response.getBody());
        verify(userService).updateUser(1L, testUser);
    }
} 