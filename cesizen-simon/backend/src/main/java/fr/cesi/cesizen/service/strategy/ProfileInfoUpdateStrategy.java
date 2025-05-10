package fr.cesi.cesizen.service.strategy;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.request.ProfileUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class ProfileInfoUpdateStrategy implements ProfileUpdateStrategy {
    @Override
    public User update(User existingUser, Object updateRequest) {
        if (!(updateRequest instanceof ProfileUpdateRequest)) {
            throw new IllegalArgumentException("Invalid request type for profile info update");
        }

        ProfileUpdateRequest request = (ProfileUpdateRequest) updateRequest;
        
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());

        return existingUser;
    }
} 