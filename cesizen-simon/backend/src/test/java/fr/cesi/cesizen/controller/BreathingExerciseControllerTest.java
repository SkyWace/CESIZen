package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.dto.BreathingExerciseDto;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import fr.cesi.cesizen.repository.ExerciseFeedbackRepository;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.BreathingExerciseService;
import fr.cesi.cesizen.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BreathingExerciseControllerTest {

    @Mock
    private BreathingExerciseService breathingExerciseService;

    @Mock
    private BreathingExerciseRepository exerciseRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExerciseFeedbackRepository feedbackRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BreathingExerciseController breathingExerciseController;

    private BreathingExercise testExercise;
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
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);

        testExercise = new BreathingExercise();
        testExercise.setIdExercice(1L);
        testExercise.setNom("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setType("Test Type");
        testExercise.setDureeInspiration(4);
        testExercise.setDureeExpiration(6);
        testExercise.setDureeApnee(2);
        testExercise.setNombreCycles(5);
        testExercise.setCreator(testUser);
        testExercise.setIsOfficial(false);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", 
            "Test", "User", authorities);
    }

    @Test
    void getOfficialExercises_Success() {
        List<BreathingExercise> exercises = Arrays.asList(testExercise);
        when(exerciseRepository.findByIsOfficialTrue()).thenReturn(exercises);
        when(feedbackRepository.findAverageRatingByExercise(any(BreathingExercise.class))).thenReturn(4.5);

        List<BreathingExerciseDto> result = breathingExerciseController.getOfficialExercises();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExercise.getIdExercice(), result.get(0).getIdExercice());
        assertEquals(testExercise.getNom(), result.get(0).getNom());
        assertEquals(4.5, result.get(0).getAverageRating());
        verify(exerciseRepository).findByIsOfficialTrue();
    }

    @Test
    void getUserExercises_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);

        List<BreathingExercise> exercises = Arrays.asList(testExercise);
        when(exerciseRepository.findByCreator(testUser)).thenReturn(exercises);

        List<BreathingExerciseDto> result = breathingExerciseController.getUserExercises();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testExercise.getIdExercice(), result.get(0).getIdExercice());
        assertEquals(testExercise.getNom(), result.get(0).getNom());
        verify(exerciseRepository).findByCreator(testUser);
    }

    @Test
    void getExerciseById_Success() {
        when(breathingExerciseService.getExerciseById(1L)).thenReturn(testExercise);

        ResponseEntity<BreathingExerciseDto> response = breathingExerciseController.getExerciseById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testExercise.getIdExercice(), response.getBody().getIdExercice());
        assertEquals(testExercise.getNom(), response.getBody().getNom());
        verify(breathingExerciseService).getExerciseById(1L);
    }

    @Test
    void createExercise_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(breathingExerciseService.createExercise(any(BreathingExercise.class))).thenReturn(testExercise);

        ResponseEntity<BreathingExerciseDto> response = breathingExerciseController.createExercise(testExercise);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testExercise.getIdExercice(), response.getBody().getIdExercice());
        assertEquals(testExercise.getNom(), response.getBody().getNom());
        verify(breathingExerciseService).createExercise(testExercise);
    }

    @Test
    void createOfficialExercise_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(breathingExerciseService.createExercise(any(BreathingExercise.class))).thenReturn(testExercise);

        ResponseEntity<BreathingExerciseDto> response = breathingExerciseController.createOfficialExercise(testExercise);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testExercise.getIdExercice(), response.getBody().getIdExercice());
        assertEquals(testExercise.getNom(), response.getBody().getNom());
        verify(breathingExerciseService).createExercise(testExercise);
    }

    @Test
    void updateExercise_Success() {
        when(breathingExerciseService.updateExercise(anyLong(), any(BreathingExercise.class))).thenReturn(testExercise);

        ResponseEntity<BreathingExerciseDto> response = breathingExerciseController.updateExercise(1L, testExercise);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testExercise.getIdExercice(), response.getBody().getIdExercice());
        assertEquals(testExercise.getNom(), response.getBody().getNom());
        verify(breathingExerciseService).updateExercise(1L, testExercise);
    }

    @Test
    void deleteExercise_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(breathingExerciseService.getExerciseById(1L)).thenReturn(testExercise);
        doNothing().when(breathingExerciseService).deleteExercise(1L);

        ResponseEntity<Void> response = breathingExerciseController.deleteExercise(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(breathingExerciseService).deleteExercise(1L);
    }
}