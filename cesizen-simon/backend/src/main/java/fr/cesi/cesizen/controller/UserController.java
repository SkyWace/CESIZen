package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.payload.SignupRequest;
import fr.cesi.cesizen.payload.request.ProfileUpdateRequest;
import fr.cesi.cesizen.payload.request.PasswordUpdateRequest;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.UserService;
import fr.cesi.cesizen.service.strategy.ProfileInfoUpdateStrategy;
import fr.cesi.cesizen.service.strategy.PasswordUpdateStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User management APIs")
@RateLimiter(name = "backendA")
public class UserController {

    private final UserService userService;
    private final ProfileInfoUpdateStrategy profileInfoUpdateStrategy;
    private final PasswordUpdateStrategy passwordUpdateStrategy;

    public UserController(UserService userService, 
                         ProfileInfoUpdateStrategy profileInfoUpdateStrategy,
                         PasswordUpdateStrategy passwordUpdateStrategy) {
        this.userService = userService;
        this.profileInfoUpdateStrategy = profileInfoUpdateStrategy;
        this.passwordUpdateStrategy = passwordUpdateStrategy;
    }

    @Operation(
        summary = "Create new user",
        description = "Creates a new user account"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created user",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            User user = userService.createUser(signupRequest);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(
        summary = "Get current user",
        description = "Retrieves the currently authenticated user's information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getUserById(userDetails.getId()));
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all users"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
        summary = "Update user",
        description = "Updates an existing user's information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        User existingUser = userService.getUserById(id);
        if (existingUser.getUsername().equals("admin")) {
            // For admin user, only allow updating non-sensitive fields
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setFirstName(userDetails.getFirstName());
            existingUser.setLastName(userDetails.getLastName());
            return ResponseEntity.ok(userService.updateUser(id, existingUser));
        }
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @Operation(
        summary = "Delete user",
        description = "Deletes a user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user.getUsername().equals("admin")) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Update current user profile",
        description = "Updates the profile of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUser(@Valid @RequestBody ProfileUpdateRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User existingUser = userService.getUserById(userDetailsImpl.getId());

        // Vérifier si le nouveau username est déjà utilisé
        if (!existingUser.getUsername().equals(updateRequest.getUsername())) {
            if (userService.existsByUsername(updateRequest.getUsername())) {
                throw new RuntimeException("Username is already taken!");
            }
        }

        User updatedUser = profileInfoUpdateStrategy.update(existingUser, updateRequest);
        return ResponseEntity.ok(userService.updateUser(updatedUser.getId(), updatedUser));
    }

    @PutMapping("/me/password")
    public ResponseEntity<User> updatePassword(@Valid @RequestBody PasswordUpdateRequest passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        User existingUser = userService.getUserById(userDetailsImpl.getId());

        User updatedUser = passwordUpdateStrategy.update(existingUser, passwordRequest);
        return ResponseEntity.ok(userService.updateUser(updatedUser.getId(), updatedUser));
    }
} 