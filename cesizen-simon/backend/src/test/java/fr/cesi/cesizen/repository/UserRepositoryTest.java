package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // Clear the repository before each test
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUser() {
        // Create a new user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        // Save the user
        User savedUser = userRepository.save(user);

        // Verify the user was saved
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password");
        assertThat(savedUser.getFirstName()).isEqualTo("Test");
        assertThat(savedUser.getLastName()).isEqualTo("User");
        assertThat(savedUser.getRoles()).containsExactly("ROLE_USER");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testFindUserById() {
        // Create and save a new user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Find the user by ID
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Verify the user was found
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void testUpdateUser() {
        // Create and save a new user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Update the user
        savedUser.setUsername("updateduser");
        savedUser.setEmail("updated@example.com");
        savedUser.setFirstName("Updated");
        savedUser.setLastName("User");
        User updatedUser = userRepository.save(savedUser);

        // Verify the user was updated
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getUsername()).isEqualTo("updateduser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("User");
    }

    @Test
    public void testDeleteUser() {
        // Create and save a new user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Delete the user
        userRepository.delete(savedUser);

        // Verify the user was deleted
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    public void testFindByUsername() {
        // Create and save users with different usernames
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setFirstName("User");
        user1.setLastName("One");
        Set<String> roles1 = new HashSet<>();
        roles1.add("ROLE_USER");
        user1.setRoles(roles1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setFirstName("User");
        user2.setLastName("Two");
        Set<String> roles2 = new HashSet<>();
        roles2.add("ROLE_USER");
        user2.setRoles(roles2);
        userRepository.save(user2);

        // Find users by username
        Optional<User> foundUser1 = userRepository.findByUsername("user1");
        Optional<User> foundUser2 = userRepository.findByUsername("user2");
        Optional<User> foundUser3 = userRepository.findByUsername("user3");

        // Verify users were found by username
        assertThat(foundUser1).isPresent();
        assertThat(foundUser1.get().getUsername()).isEqualTo("user1");
        assertThat(foundUser1.get().getEmail()).isEqualTo("user1@example.com");

        assertThat(foundUser2).isPresent();
        assertThat(foundUser2.get().getUsername()).isEqualTo("user2");
        assertThat(foundUser2.get().getEmail()).isEqualTo("user2@example.com");

        assertThat(foundUser3).isEmpty();
    }

    @Test
    public void testFindByEmail() {
        // Create and save users with different emails
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        user1.setFirstName("User");
        user1.setLastName("One");
        Set<String> roles1 = new HashSet<>();
        roles1.add("ROLE_USER");
        user1.setRoles(roles1);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        user2.setFirstName("User");
        user2.setLastName("Two");
        Set<String> roles2 = new HashSet<>();
        roles2.add("ROLE_USER");
        user2.setRoles(roles2);
        userRepository.save(user2);

        // Find users by email
        Optional<User> foundUser1 = userRepository.findByEmail("user1@example.com");
        Optional<User> foundUser2 = userRepository.findByEmail("user2@example.com");
        Optional<User> foundUser3 = userRepository.findByEmail("user3@example.com");

        // Verify users were found by email
        assertThat(foundUser1).isPresent();
        assertThat(foundUser1.get().getUsername()).isEqualTo("user1");
        assertThat(foundUser1.get().getEmail()).isEqualTo("user1@example.com");

        assertThat(foundUser2).isPresent();
        assertThat(foundUser2.get().getUsername()).isEqualTo("user2");
        assertThat(foundUser2.get().getEmail()).isEqualTo("user2@example.com");

        assertThat(foundUser3).isEmpty();
    }

    @Test
    public void testExistsByUsername() {
        // Create and save a user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        userRepository.save(user);

        // Check if users exist by username
        Boolean existsTestUser = userRepository.existsByUsername("testuser");
        Boolean existsNonExistentUser = userRepository.existsByUsername("nonexistentuser");

        // Verify existence check works correctly
        assertThat(existsTestUser).isTrue();
        assertThat(existsNonExistentUser).isFalse();
    }

    @Test
    public void testExistsByEmail() {
        // Create and save a user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        userRepository.save(user);

        // Check if users exist by email
        Boolean existsTestEmail = userRepository.existsByEmail("test@example.com");
        Boolean existsNonExistentEmail = userRepository.existsByEmail("nonexistent@example.com");

        // Verify existence check works correctly
        assertThat(existsTestEmail).isTrue();
        assertThat(existsNonExistentEmail).isFalse();
    }

    @Test
    public void testFindByResetPasswordToken() {
        // Create and save a user with a reset password token
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        user.setResetPasswordToken("reset-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Find user by reset password token
        Optional<User> foundUser = userRepository.findByResetPasswordToken("reset-token");
        Optional<User> notFoundUser = userRepository.findByResetPasswordToken("invalid-token");

        // Verify user was found by reset password token
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getResetPasswordToken()).isEqualTo("reset-token");

        assertThat(notFoundUser).isEmpty();
    }
}