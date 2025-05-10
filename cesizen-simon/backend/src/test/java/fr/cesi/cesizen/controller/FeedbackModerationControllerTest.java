package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.dto.ExerciseFeedbackDto;
import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.service.ExerciseFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackModerationControllerTest {

    @Mock
    private ExerciseFeedbackService feedbackService;

    @InjectMocks
    private FeedbackModerationController moderationController;

    private User testUser;
    private BreathingExercise testExercise;
    private ExerciseFeedback testFeedback;
    private ExerciseFeedbackDto testFeedbackDto;

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
    }

    @Test
    void getAllFeedbacks_Success() {
        List<ExerciseFeedbackDto> feedbacks = Arrays.asList(testFeedbackDto);
        when(feedbackService.getAllFeedbacks()).thenReturn(feedbacks);

        ResponseEntity<List<ExerciseFeedbackDto>> response = moderationController.getAllFeedbacks();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(feedbacks, response.getBody());
        verify(feedbackService).getAllFeedbacks();
    }
}