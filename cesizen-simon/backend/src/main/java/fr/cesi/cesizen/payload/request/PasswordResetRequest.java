package fr.cesi.cesizen.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max = 120)
    private String newPassword;
}