package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.ERole;
import fr.cesi.cesizen.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        // Clear the repository before each test
        roleRepository.deleteAll();
    }

    @Test
    public void testSaveRole() {
        // Create a new role
        Role role = new Role();
        role.setName(ERole.ROLE_USER);

        // Save the role
        Role savedRole = roleRepository.save(role);

        // Verify the role was saved
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(ERole.ROLE_USER);
    }

    @Test
    public void testFindRoleById() {
        // Create and save a new role
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        Role savedRole = roleRepository.save(role);

        // Find the role by ID
        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        // Verify the role was found
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getId()).isEqualTo(savedRole.getId());
        assertThat(foundRole.get().getName()).isEqualTo(ERole.ROLE_USER);
    }

    @Test
    public void testUpdateRole() {
        // Create and save a new role
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        Role savedRole = roleRepository.save(role);

        // Update the role
        savedRole.setName(ERole.ROLE_ADMIN);
        Role updatedRole = roleRepository.save(savedRole);

        // Verify the role was updated
        assertThat(updatedRole.getId()).isEqualTo(savedRole.getId());
        assertThat(updatedRole.getName()).isEqualTo(ERole.ROLE_ADMIN);
    }

    @Test
    public void testDeleteRole() {
        // Create and save a new role
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        Role savedRole = roleRepository.save(role);

        // Delete the role
        roleRepository.delete(savedRole);

        // Verify the role was deleted
        Optional<Role> deletedRole = roleRepository.findById(savedRole.getId());
        assertThat(deletedRole).isEmpty();
    }

    @Test
    public void testFindByName() {
        // Create and save roles with different names
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
        roleRepository.save(userRole);

        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        roleRepository.save(adminRole);

        Role superAdminRole = new Role();
        superAdminRole.setName(ERole.ROLE_SUPER_ADMIN);
        roleRepository.save(superAdminRole);

        // Find roles by name
        Optional<Role> foundUserRole = roleRepository.findByName(ERole.ROLE_USER);
        Optional<Role> foundAdminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
        Optional<Role> foundSuperAdminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN);
        Optional<Role> foundModeratorRole = roleRepository.findByName(ERole.ROLE_MODERATOR);

        // Verify roles were found by name
        assertThat(foundUserRole).isPresent();
        assertThat(foundUserRole.get().getName()).isEqualTo(ERole.ROLE_USER);

        assertThat(foundAdminRole).isPresent();
        assertThat(foundAdminRole.get().getName()).isEqualTo(ERole.ROLE_ADMIN);

        assertThat(foundSuperAdminRole).isPresent();
        assertThat(foundSuperAdminRole.get().getName()).isEqualTo(ERole.ROLE_SUPER_ADMIN);

        assertThat(foundModeratorRole).isEmpty();
    }

    @Test
    public void testExistsByName() {
        // Create and save roles with different names
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
        roleRepository.save(userRole);

        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);
        roleRepository.save(adminRole);

        // Check if roles exist by name
        Boolean userRoleExists = roleRepository.existsByName(ERole.ROLE_USER);
        Boolean adminRoleExists = roleRepository.existsByName(ERole.ROLE_ADMIN);
        Boolean moderatorRoleExists = roleRepository.existsByName(ERole.ROLE_MODERATOR);
        Boolean superAdminRoleExists = roleRepository.existsByName(ERole.ROLE_SUPER_ADMIN);

        // Verify existence check works correctly
        assertThat(userRoleExists).isTrue();
        assertThat(adminRoleExists).isTrue();
        assertThat(moderatorRoleExists).isFalse();
        assertThat(superAdminRoleExists).isFalse();
    }
}