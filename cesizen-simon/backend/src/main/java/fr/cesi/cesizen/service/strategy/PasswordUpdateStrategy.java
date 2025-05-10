package fr.cesi.cesizen.service.strategy;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.request.PasswordUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUpdateStrategy implements ProfileUpdateStrategy {
    private final PasswordEncoder passwordEncoder;

    public PasswordUpdateStrategy(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User update(User existingUser, Object updateRequest) {
        if (!(updateRequest instanceof PasswordUpdateRequest)) {
            throw new IllegalArgumentException("Invalid request type for password update");
        }

        PasswordUpdateRequest request = (PasswordUpdateRequest) updateRequest;

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Mettre à jour le mot de passe
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));

        return existingUser;
    }
} 