package fr.cesi.cesizen.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetTokenRequest {
    @NotBlank
    @Email
    private String email;
}