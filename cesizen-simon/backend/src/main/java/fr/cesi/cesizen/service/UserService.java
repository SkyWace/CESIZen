package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.SignupRequest;
import fr.cesi.cesizen.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        
        // Ensure roles have ROLE_ prefix
        Set<String> roles = signupRequest.getRoles().stream()
            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
            .collect(Collectors.toSet());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        // Don't allow updating admin's roles
        if (user.getUsername().equals("admin")) {
            userDetails.setRoles(user.getRoles());
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setRoles(userDetails.getRoles());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (user.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }
        userRepository.delete(user);
    }

    public void updateLastLogin(String username) {
        User user = getUserByUsername(username);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Transactional
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = getUserById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user);

        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }
}