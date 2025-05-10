package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.dto.ExerciseFeedbackDto;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.ExerciseFeedbackService;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseFeedbackControllerTest {

    @Mock
    private ExerciseFeedbackService feedbackService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ExerciseFeedbackController feedbackController;

    private User testUser;
    private BreathingExercise testExercise;
    private ExerciseFeedback testFeedback;
    private ExerciseFeedbackDto testFeedbackDto;
    private UserDetailsImpl userDetails;
    private Map<String, Object> feedbackPayload;

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

        testFeedback = new ExerciseFeedback();
        testFeedback.setId(1L);
        testFeedback.setUser(testUser);
        testFeedback.setExercise(testExercise);
        testFeedback.setComment("Test Comment");
        testFeedback.setRating(5);
        testFeedback.setCreatedAt(LocalDateTime.now());

        testFeedbackDto = new ExerciseFeedbackDto(testFeedback);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new UserDetailsImpl(1L, "testuser", "test@example.com", "password", 
            "Test", "User", authorities);

        feedbackPayload = new HashMap<>();
        feedbackPayload.put("comment", "Test Comment");
        feedbackPayload.put("rating", 5);
    }

    @Test
    void getFeedbacks_Success() {
        List<ExerciseFeedbackDto> feedbacks = Arrays.asList(testFeedbackDto);
        when(feedbackService.getFeedbacksForExercise(1L)).thenReturn(feedbacks);

        ResponseEntity<List<ExerciseFeedbackDto>> response = feedbackController.getFeedbacks(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedbacks, response.getBody());
        verify(feedbackService).getFeedbacksForExercise(1L);
    }

    @Test
    void createFeedback_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(feedbackService.createFeedback(any(User.class), anyLong(), anyString(), anyInt())).thenReturn(testFeedbackDto);

        ResponseEntity<ExerciseFeedbackDto> response = feedbackController.createFeedback(1L, feedbackPayload);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testFeedbackDto, response.getBody());
        verify(feedbackService).createFeedback(testUser, 1L, "Test Comment", 5);
    }

    @Test
    void createFeedback_InvalidRating() {
        feedbackPayload.put("rating", 6); // Invalid rating (> 5)

        ResponseEntity<ExerciseFeedbackDto> response = feedbackController.createFeedback(1L, feedbackPayload);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        verify(feedbackService, never()).createFeedback(any(), anyLong(), anyString(), anyInt());
    }

    @Test
    void deleteFeedback_Success() {
        doNothing().when(feedbackService).deleteFeedback(1L);

        ResponseEntity<Void> response = feedbackController.deleteFeedback(1L, 1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(feedbackService).deleteFeedback(1L);
    }

    @Test
    void getAllFeedbacks_Success() {
        List<ExerciseFeedbackDto> feedbacks = Arrays.asList(testFeedbackDto);
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        ResponseEntity<List<ExerciseFeedbackDto>> response = feedbackController.getAllFeedbacks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedbacks, response.getBody());
        verify(feedbackService).getAllFeedbacks();
    }
}