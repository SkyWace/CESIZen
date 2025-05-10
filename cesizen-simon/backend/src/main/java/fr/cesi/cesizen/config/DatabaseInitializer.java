package fr.cesi.cesizen.config;

import fr.cesi.cesizen.model.ERole;
import fr.cesi.cesizen.model.Role;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.RoleRepository;
import fr.cesi.cesizen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        // Create roles if they don't exist
        Arrays.stream(ERole.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                Role newRole = new Role();
                newRole.setName(role);
                roleRepository.save(newRole);
                logger.info("Created role: {}", role);
            }
        });
    }

    private void initAdminUser() {
        // Check if admin user exists
        if (!userRepository.existsByUsername("admin")) {
            logger.info("Creating default admin user");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@cesizen.com");
            admin.setPassword("$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setActive(true);

            // Get the SUPER_ADMIN role from the database
            Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Super Admin role not found"));

            Set<String> roles = new HashSet<>();
            roles.add(superAdminRole.getName().name());
            admin.setRoles(roles);

            userRepository.save(admin);
            logger.info("Default admin user created successfully with role: {}", superAdminRole.getName());
        } else {
            logger.info("Admin user already exists");
        }
    }
}