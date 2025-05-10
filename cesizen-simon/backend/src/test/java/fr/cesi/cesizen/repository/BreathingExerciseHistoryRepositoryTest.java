package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.BreathingExerciseHistory;
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
public class BreathingExerciseHistoryRepositoryTest {

    @Autowired
    private BreathingExerciseHistoryRepository historyRepository;

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
        historyRepository.deleteAll();
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
    public void testSaveBreathingExerciseHistory() {
        // Create a new history entry
        BreathingExerciseHistory history = new BreathingExerciseHistory();
        history.setUser(testUser);
        history.setExercise(testExercise);
        history.setCompletedAt(LocalDateTime.now());

        // Save the history entry
        BreathingExerciseHistory savedHistory = historyRepository.save(history);

        // Verify the history entry was saved
        assertThat(savedHistory).isNotNull();
        assertThat(savedHistory.getId()).isNotNull();
        assertThat(savedHistory.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedHistory.getExercise().getIdExercice()).isEqualTo(testExercise.getIdExercice());
        assertThat(savedHistory.getCompletedAt()).isNotNull();
    }

    @Test
    public void testFindBreathingExerciseHistoryById() {
        // Create and save a new history entry
        BreathingExerciseHistory history = new BreathingExerciseHistory();
        history.setUser(testUser);
        history.setExercise(testExercise);
        history.setCompletedAt(LocalDateTime.now());
        BreathingExerciseHistory savedHistory = historyRepository.save(history);

        // Find the history entry by ID
        Optional<BreathingExerciseHistory> foundHistory = historyRepository.findById(savedHistory.getId());

        // Verify the history entry was found
        assertThat(foundHistory).isPresent();
        assertThat(foundHistory.get().getId()).isEqualTo(savedHistory.getId());
        assertThat(foundHistory.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(foundHistory.get().getExercise().getIdExercice()).isEqualTo(testExercise.getIdExercice());
    }

    @Test
    public void testUpdateBreathingExerciseHistory() {
        // Create and save a new history entry
        BreathingExerciseHistory history = new BreathingExerciseHistory();
        history.setUser(testUser);
        history.setExercise(testExercise);
        LocalDateTime initialTime = LocalDateTime.now().minusHours(1);
        history.setCompletedAt(initialTime);
        BreathingExerciseHistory savedHistory = historyRepository.save(history);

        // Update the history entry
        LocalDateTime updatedTime = LocalDateTime.now();
        savedHistory.setCompletedAt(updatedTime);
        BreathingExerciseHistory updatedHistory = historyRepository.save(savedHistory);

        // Verify the history entry was updated
        assertThat(updatedHistory.getId()).isEqualTo(savedHistory.getId());
        assertThat(updatedHistory.getCompletedAt()).isEqualToIgnoringNanos(updatedTime);
    }

    @Test
    public void testDeleteBreathingExerciseHistory() {
        // Create and save a new history entry
        BreathingExerciseHistory history = new BreathingExerciseHistory();
        history.setUser(testUser);
        history.setExercise(testExercise);
        history.setCompletedAt(LocalDateTime.now());
        BreathingExerciseHistory savedHistory = historyRepository.save(history);

        // Delete the history entry
        historyRepository.delete(savedHistory);

        // Verify the history entry was deleted
        Optional<BreathingExerciseHistory> deletedHistory = historyRepository.findById(savedHistory.getId());
        assertThat(deletedHistory).isEmpty();
    }

    @Test
    public void testFindByUser() {
        // Create and save history entries for different users
        BreathingExerciseHistory testUserHistory1 = new BreathingExerciseHistory();
        testUserHistory1.setUser(testUser);
        testUserHistory1.setExercise(testExercise);
        testUserHistory1.setCompletedAt(LocalDateTime.now().minusHours(2));
        historyRepository.save(testUserHistory1);

        BreathingExerciseHistory testUserHistory2 = new BreathingExerciseHistory();
        testUserHistory2.setUser(testUser);
        testUserHistory2.setExercise(testExercise);
        testUserHistory2.setCompletedAt(LocalDateTime.now().minusHours(1));
        historyRepository.save(testUserHistory2);

        BreathingExerciseHistory anotherUserHistory = new BreathingExerciseHistory();
        anotherUserHistory.setUser(anotherUser);
        anotherUserHistory.setExercise(testExercise);
        anotherUserHistory.setCompletedAt(LocalDateTime.now());
        historyRepository.save(anotherUserHistory);

        // Find history entries by user
        List<BreathingExerciseHistory> testUserHistories = historyRepository.findByUser(testUser);
        List<BreathingExerciseHistory> anotherUserHistories = historyRepository.findByUser(anotherUser);

        // Verify history entries were found by user
        assertThat(testUserHistories).hasSize(2);
        assertThat(testUserHistories.get(0).getUser().getId()).isEqualTo(testUser.getId());
        assertThat(testUserHistories.get(1).getUser().getId()).isEqualTo(testUser.getId());

        assertThat(anotherUserHistories).hasSize(1);
        assertThat(anotherUserHistories.get(0).getUser().getId()).isEqualTo(anotherUser.getId());
    }
}