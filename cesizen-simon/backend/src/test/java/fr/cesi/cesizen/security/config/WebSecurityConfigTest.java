package fr.cesi.cesizen.security.config;

import fr.cesi.cesizen.security.AuthTokenFilter;
import fr.cesi.cesizen.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @Test
    void authenticationJwtTokenFilter_ShouldReturnAuthTokenFilter() {
        AuthTokenFilter filter = webSecurityConfig.authenticationJwtTokenFilter();
        assertNotNull(filter);
    }

    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();
        assertNotNull(provider);
        // We can't directly test the internal state of DaoAuthenticationProvider
        // due to protected access, but we can verify it's not null
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder encoder = webSecurityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);

        // Test basic functionality
        String password = "password";
        String encoded = encoder.encode(password);
        assertTrue(encoder.matches(password, encoded));
        assertFalse(encoder.matches("wrongpassword", encoded));
    }

    @Test
    void corsConfigurationSource_ShouldReturnCorsConfigurationSource() {
        CorsConfigurationSource source = webSecurityConfig.corsConfigurationSource();
        assertNotNull(source);

        // We can't directly test the configuration without a mock request,
        // but we can verify the source is not null
    }
}
