package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.ExerciseFeedback;
import fr.cesi.cesizen.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ExerciseFeedbackRepositoryTest {

    @Autowired
    private ExerciseFeedbackRepository feedbackRepository;

    @Autowired
    private BreathingExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User anotherUser;
    private BreathingExercise testExercise;

    @BeforeEach
    public void setUp() {
        // Clear the repositories before each test
        feedbackRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        testUser.setRoles(roles);
        testUser = userRepository.save(testUser);

        anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser.setRoles(roles);
        anotherUser = userRepository.save(anotherUser);

        // Create a test exercise
        testExercise = new BreathingExercise();
        testExercise.setNom("Test Exercise");
        testExercise.setDescription("Test Description");
        testExercise.setType("Test Type");
        testExercise.setDureeInspiration(4);
        testExercise.setDureeExpiration(6);
        testExercise.setDureeApnee(2);
        testExercise.setNombreCycles(5);
        testExercise.setCreator(testUser);
        testExercise.setIsOfficial(false);
        testExercise = exerciseRepository.save(testExercise);
    }

    @Test
    public void testSaveExerciseFeedback() {
        // Create a new feedback
        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(testUser);
        feedback.setExercise(testExercise);
        feedback.setComment("Great exercise!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());

        // Save the feedback
        ExerciseFeedback savedFeedback = feedbackRepository.save(feedback);

        // Verify the feedback was saved
        assertThat(savedFeedback).isNotNull();
        assertThat(savedFeedback.getId()).isNotNull();
        assertThat(savedFeedback.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedFeedback.getExercise().getIdExercice()).isEqualTo(testExercise.getIdExercice());
        assertThat(savedFeedback.getComment()).isEqualTo("Great exercise!");
        assertThat(savedFeedback.getRating()).isEqualTo(5);
        assertThat(savedFeedback.getCreatedAt()).isNotNull();
    }

    @Test
    public void testFindExerciseFeedbackById() {
        // Create and save a new feedback
        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(testUser);
        feedback.setExercise(testExercise);
        feedback.setComment("Great exercise!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());
        ExerciseFeedback savedFeedback = feedbackRepository.save(feedback);

        // Find the feedback by ID
        Optional<ExerciseFeedback> foundFeedback = feedbackRepository.findById(savedFeedback.getId());

        // Verify the feedback was found
        assertThat(foundFeedback).isPresent();
        assertThat(foundFeedback.get().getId()).isEqualTo(savedFeedback.getId());
        assertThat(foundFeedback.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundFeedback.get().getExercise().getIdExercice()).isEqualTo(testExercise.getIdExercice());
        assertThat(foundFeedback.get().getComment()).isEqualTo("Great exercise!");
        assertThat(foundFeedback.get().getRating()).isEqualTo(5);
    }

    @Test
    public void testUpdateExerciseFeedback() {
        // Create and save a new feedback
        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(testUser);
        feedback.setExercise(testExercise);
        feedback.setComment("Good exercise");
        feedback.setRating(4);
        feedback.setCreatedAt(LocalDateTime.now());
        ExerciseFeedback savedFeedback = feedbackRepository.save(feedback);

        // Update the feedback
        savedFeedback.setComment("Great exercise!");
        savedFeedback.setRating(5);
        ExerciseFeedback updatedFeedback = feedbackRepository.save(savedFeedback);

        // Verify the feedback was updated
        assertThat(updatedFeedback.getId()).isEqualTo(savedFeedback.getId());
        assertThat(updatedFeedback.getComment()).isEqualTo("Great exercise!");
        assertThat(updatedFeedback.getRating()).isEqualTo(5);
    }

    @Test
    public void testDeleteExerciseFeedback() {
        // Create and save a new feedback
        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(testUser);
        feedback.setExercise(testExercise);
        feedback.setComment("Great exercise!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());
        ExerciseFeedback savedFeedback = feedbackRepository.save(feedback);

        // Delete the feedback
        feedbackRepository.delete(savedFeedback);

        // Verify the feedback was deleted
        Optional<ExerciseFeedback> deletedFeedback = feedbackRepository.findById(savedFeedback.getId());
        assertThat(deletedFeedback).isEmpty();
    }

    @Test
    public void testFindByExerciseOrderByCreatedAtDesc() {
        // Create and save feedbacks with different creation times
        ExerciseFeedback olderFeedback = new ExerciseFeedback();
        olderFeedback.setUser(testUser);
        olderFeedback.setExercise(testExercise);
        olderFeedback.setComment("Good exercise");
        olderFeedback.setRating(4);
        olderFeedback.setCreatedAt(LocalDateTime.now().minusHours(2));
        feedbackRepository.save(olderFeedback);

        ExerciseFeedback newerFeedback = new ExerciseFeedback();
        newerFeedback.setUser(anotherUser);
        newerFeedback.setExercise(testExercise);
        newerFeedback.setComment("Great exercise!");
        newerFeedback.setRating(5);
        newerFeedback.setCreatedAt(LocalDateTime.now().minusHours(1));
        feedbackRepository.save(newerFeedback);

        // Find feedbacks by exercise ordered by creation time
        List<ExerciseFeedback> feedbacks = feedbackRepository.findByExerciseOrderByCreatedAtDesc(testExercise);

        // Verify feedbacks are ordered by creation time (newest first)
        assertThat(feedbacks).hasSize(2);
        assertThat(feedbacks.get(0).getId()).isEqualTo(newerFeedback.getId());
        assertThat(feedbacks.get(1).getId()).isEqualTo(olderFeedback.getId());
    }

    @Test
    public void testFindAverageRatingByExercise() {
        // Create and save feedbacks with different ratings
        ExerciseFeedback feedback1 = new ExerciseFeedback();
        feedback1.setUser(testUser);
        feedback1.setExercise(testExercise);
        feedback1.setComment("Good exercise");
        feedback1.setRating(4);
        feedback1.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback1);

        ExerciseFeedback feedback2 = new ExerciseFeedback();
        feedback2.setUser(anotherUser);
        feedback2.setExercise(testExercise);
        feedback2.setComment("Great exercise!");
        feedback2.setRating(5);
        feedback2.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback2);

        // Find average rating for the exercise
        Double averageRating = feedbackRepository.findAverageRatingByExercise(testExercise);

        // Verify the average rating is correct
        assertThat(averageRating).isEqualTo(4.5);
    }

    @Test
    public void testFindByUserAndExercise() {
        // Create and save a feedback
        ExerciseFeedback feedback = new ExerciseFeedback();
        feedback.setUser(testUser);
        feedback.setExercise(testExercise);
        feedback.setComment("Great exercise!");
        feedback.setRating(5);
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        // Find feedback by user and exercise
        ExerciseFeedback foundFeedback = feedbackRepository.findByUserAndExercise(testUser, testExercise);

        // Verify the feedback was found
        assertThat(foundFeedback).isNotNull();
        assertThat(foundFeedback.getId()).isEqualTo(feedback.getId());
        assertThat(foundFeedback.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundFeedback.getExercise().getIdExercice()).isEqualTo(testExercise.getIdExercice());
        assertThat(foundFeedback.getComment()).isEqualTo("Great exercise!");
        assertThat(foundFeedback.getRating()).isEqualTo(5);
    }
}