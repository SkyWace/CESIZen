package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BreathingExerciseRepositoryTest {

    @Autowired
    private BreathingExerciseRepository breathingExerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // Clear the repository before each test
        breathingExerciseRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
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
    }

    @Test
    public void testSaveBreathingExercise() {
        // Create a new breathing exercise
        BreathingExercise exercise = new BreathingExercise();
        exercise.setNom("Test Exercise");
        exercise.setDescription("Test Description");
        exercise.setType("Test Type");
        exercise.setDureeInspiration(4);
        exercise.setDureeExpiration(6);
        exercise.setDureeApnee(2);
        exercise.setNombreCycles(5);
        exercise.setCreator(testUser);
        exercise.setIsOfficial(false);

        // Save the exercise
        BreathingExercise savedExercise = breathingExerciseRepository.save(exercise);

        // Verify the exercise was saved
        assertThat(savedExercise).isNotNull();
        assertThat(savedExercise.getIdExercice()).isNotNull();
        assertThat(savedExercise.getNom()).isEqualTo("Test Exercise");
        assertThat(savedExercise.getDescription()).isEqualTo("Test Description");
        assertThat(savedExercise.getType()).isEqualTo("Test Type");
        assertThat(savedExercise.getDureeInspiration()).isEqualTo(4);
        assertThat(savedExercise.getDureeExpiration()).isEqualTo(6);
        assertThat(savedExercise.getDureeApnee()).isEqualTo(2);
        assertThat(savedExercise.getNombreCycles()).isEqualTo(5);
        assertThat(savedExercise.getCreator().getId()).isEqualTo(testUser.getId());
        assertThat(savedExercise.isOfficial()).isFalse();
    }

    @Test
    public void testFindBreathingExerciseById() {
        // Create and save a new breathing exercise
        BreathingExercise exercise = new BreathingExercise();
        exercise.setNom("Test Exercise");
        exercise.setDescription("Test Description");
        exercise.setType("Test Type");
        exercise.setDureeInspiration(4);
        exercise.setDureeExpiration(6);
        exercise.setDureeApnee(2);
        exercise.setNombreCycles(5);
        exercise.setCreator(testUser);
        exercise.setIsOfficial(false);
        BreathingExercise savedExercise = breathingExerciseRepository.save(exercise);

        // Find the exercise by ID
        Optional<BreathingExercise> foundExercise = breathingExerciseRepository.findById(savedExercise.getIdExercice());

        // Verify the exercise was found
        assertThat(foundExercise).isPresent();
        assertThat(foundExercise.get().getIdExercice()).isEqualTo(savedExercise.getIdExercice());
        assertThat(foundExercise.get().getNom()).isEqualTo("Test Exercise");
    }

    @Test
    public void testUpdateBreathingExercise() {
        // Create and save a new breathing exercise
        BreathingExercise exercise = new BreathingExercise();
        exercise.setNom("Test Exercise");
        exercise.setDescription("Test Description");
        exercise.setType("Test Type");
        exercise.setDureeInspiration(4);
        exercise.setDureeExpiration(6);
        exercise.setDureeApnee(2);
        exercise.setNombreCycles(5);
        exercise.setCreator(testUser);
        exercise.setIsOfficial(false);
        BreathingExercise savedExercise = breathingExerciseRepository.save(exercise);

        // Update the exercise
        savedExercise.setNom("Updated Exercise");
        savedExercise.setDureeInspiration(5);
        savedExercise.setIsOfficial(true);
        BreathingExercise updatedExercise = breathingExerciseRepository.save(savedExercise);

        // Verify the exercise was updated
        assertThat(updatedExercise.getIdExercice()).isEqualTo(savedExercise.getIdExercice());
        assertThat(updatedExercise.getNom()).isEqualTo("Updated Exercise");
        assertThat(updatedExercise.getDureeInspiration()).isEqualTo(5);
        assertThat(updatedExercise.isOfficial()).isTrue();
    }

    @Test
    public void testDeleteBreathingExercise() {
        // Create and save a new breathing exercise
        BreathingExercise exercise = new BreathingExercise();
        exercise.setNom("Test Exercise");
        exercise.setDescription("Test Description");
        exercise.setType("Test Type");
        exercise.setDureeInspiration(4);
        exercise.setDureeExpiration(6);
        exercise.setDureeApnee(2);
        exercise.setNombreCycles(5);
        exercise.setCreator(testUser);
        exercise.setIsOfficial(false);
        BreathingExercise savedExercise = breathingExerciseRepository.save(exercise);

        // Delete the exercise
        breathingExerciseRepository.delete(savedExercise);

        // Verify the exercise was deleted
        Optional<BreathingExercise> deletedExercise = breathingExerciseRepository.findById(savedExercise.getIdExercice());
        assertThat(deletedExercise).isEmpty();
    }

    @Test
    public void testFindByIsOfficialTrue() {
        // Create and save official and non-official exercises
        BreathingExercise officialExercise = new BreathingExercise();
        officialExercise.setNom("Official Exercise");
        officialExercise.setDescription("Official Description");
        officialExercise.setType("Official Type");
        officialExercise.setDureeInspiration(4);
        officialExercise.setDureeExpiration(6);
        officialExercise.setDureeApnee(2);
        officialExercise.setNombreCycles(5);
        officialExercise.setCreator(testUser);
        officialExercise.setIsOfficial(true);
        breathingExerciseRepository.save(officialExercise);

        BreathingExercise nonOfficialExercise = new BreathingExercise();
        nonOfficialExercise.setNom("Non-Official Exercise");
        nonOfficialExercise.setDescription("Non-Official Description");
        nonOfficialExercise.setType("Non-Official Type");
        nonOfficialExercise.setDureeInspiration(3);
        nonOfficialExercise.setDureeExpiration(5);
        nonOfficialExercise.setDureeApnee(1);
        nonOfficialExercise.setNombreCycles(4);
        nonOfficialExercise.setCreator(testUser);
        nonOfficialExercise.setIsOfficial(false);
        breathingExerciseRepository.save(nonOfficialExercise);

        // Find official exercises
        List<BreathingExercise> officialExercises = breathingExerciseRepository.findByIsOfficialTrue();

        // Verify only official exercises were found
        assertThat(officialExercises).hasSize(1);
        assertThat(officialExercises.get(0).getNom()).isEqualTo("Official Exercise");
        assertThat(officialExercises.get(0).isOfficial()).isTrue();
    }

    @Test
    public void testFindByCreator() {
        // Create another user
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        anotherUser.setRoles(roles);
        anotherUser = userRepository.save(anotherUser);

        // Create and save exercises for different users
        BreathingExercise testUserExercise = new BreathingExercise();
        testUserExercise.setNom("Test User Exercise");
        testUserExercise.setDescription("Test User Description");
        testUserExercise.setType("Test User Type");
        testUserExercise.setDureeInspiration(4);
        testUserExercise.setDureeExpiration(6);
        testUserExercise.setDureeApnee(2);
        testUserExercise.setNombreCycles(5);
        testUserExercise.setCreator(testUser);
        testUserExercise.setIsOfficial(false);
        breathingExerciseRepository.save(testUserExercise);

        BreathingExercise anotherUserExercise = new BreathingExercise();
        anotherUserExercise.setNom("Another User Exercise");
        anotherUserExercise.setDescription("Another User Description");
        anotherUserExercise.setType("Another User Type");
        anotherUserExercise.setDureeInspiration(3);
        anotherUserExercise.setDureeExpiration(5);
        anotherUserExercise.setDureeApnee(1);
        anotherUserExercise.setNombreCycles(4);
        anotherUserExercise.setCreator(anotherUser);
        anotherUserExercise.setIsOfficial(false);
        breathingExerciseRepository.save(anotherUserExercise);

        // Find exercises by creator
        List<BreathingExercise> testUserExercises = breathingExerciseRepository.findByCreator(testUser);
        List<BreathingExercise> anotherUserExercises = breathingExerciseRepository.findByCreator(anotherUser);

        // Verify exercises were found by creator
        assertThat(testUserExercises).hasSize(1);
        assertThat(testUserExercises.get(0).getNom()).isEqualTo("Test User Exercise");
        assertThat(testUserExercises.get(0).getCreator().getId()).isEqualTo(testUser.getId());

        assertThat(anotherUserExercises).hasSize(1);
        assertThat(anotherUserExercises.get(0).getNom()).isEqualTo("Another User Exercise");
        assertThat(anotherUserExercises.get(0).getCreator().getId()).isEqualTo(anotherUser.getId());
    }
}