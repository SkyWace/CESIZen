package fr.cesi.cesizen.service.strategy;

import fr.cesi.cesizen.model.User;

public interface ProfileUpdateStrategy {
    User update(User existingUser, Object updateRequest);
} 