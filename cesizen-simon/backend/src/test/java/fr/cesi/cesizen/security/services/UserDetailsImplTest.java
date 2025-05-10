package fr.cesi.cesizen.security.services;

import fr.cesi.cesizen.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void testBuild_WithRolesWithPrefix() {
        // Create a user with roles that already have the ROLE_ prefix
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        user.setRoles(roles);

        // Build UserDetailsImpl from the user
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // Verify the UserDetailsImpl properties
        assertEquals(1L, userDetails.getId());
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("test@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertEquals("Test", userDetails.getFirstName());
        assertEquals("User", userDetails.getLastName());

        // Verify the authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testBuild_WithRolesWithoutPrefix() {
        // Create a user with roles that don't have the ROLE_ prefix
        User user = new User();
        user.setId(2L);
        user.setUsername("anotheruser");
        user.setEmail("another@example.com");
        user.setPassword("password");
        user.setFirstName("Another");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");
        user.setRoles(roles);

        // Build UserDetailsImpl from the user
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // Verify the UserDetailsImpl properties
        assertEquals(2L, userDetails.getId());
        assertEquals("anotheruser", userDetails.getUsername());
        assertEquals("another@example.com", userDetails.getEmail());
        assertEquals("password", userDetails.getPassword());
        assertEquals("Another", userDetails.getFirstName());
        assertEquals("User", userDetails.getLastName());

        // Verify the authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testBuild_WithMixedRoles() {
        // Create a user with a mix of roles with and without the ROLE_ prefix
        User user = new User();
        user.setId(3L);
        user.setUsername("mixeduser");
        user.setEmail("mixed@example.com");
        user.setPassword("password");
        user.setFirstName("Mixed");
        user.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ADMIN");
        user.setRoles(roles);

        // Build UserDetailsImpl from the user
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // Verify the authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testUserDetailsImplMethods() {
        // Create a UserDetailsImpl instance
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "testuser", "test@example.com", "password", 
                "Test", "User", 
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Test UserDetails interface methods
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testEquals() {
        // Create two UserDetailsImpl instances with the same ID
        UserDetailsImpl userDetails1 = new UserDetailsImpl(
                1L, "user1", "user1@example.com", "password", 
                "User", "One", 
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));

        UserDetailsImpl userDetails2 = new UserDetailsImpl(
                1L, "user2", "user2@example.com", "different", 
                "User", "Two", 
                Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        // Create a UserDetailsImpl with a different ID
        UserDetailsImpl userDetails3 = new UserDetailsImpl(
                2L, "user1", "user1@example.com", "password", 
                "User", "One", 
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Test equals method
        assertEquals(userDetails1, userDetails2); // Same ID, should be equal
        assertNotEquals(userDetails1, userDetails3); // Different ID, should not be equal
        assertNotEquals(userDetails1, null); // Null comparison
        assertNotEquals(userDetails1, new Object()); // Different class
        assertEquals(userDetails1, userDetails1); // Same instance
    }
}
