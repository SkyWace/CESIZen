package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.BreathingExercise;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.repository.BreathingExerciseRepository;
import fr.cesi.cesizen.repository.ExerciseFeedbackRepository;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.BreathingExerciseService;
import fr.cesi.cesizen.service.UserService;
import fr.cesi.cesizen.dto.BreathingExerciseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "Breathing Exercises", description = "Breathing exercises management APIs")
@RateLimiter(name = "backendA")
public class BreathingExerciseController {
    private static final Logger logger = LoggerFactory.getLogger(BreathingExerciseController.class);

    private final BreathingExerciseService breathingExerciseService;
    private final BreathingExerciseRepository exerciseRepository;
    private final UserService userService;
    private final ExerciseFeedbackRepository feedbackRepository;

    @Operation(
        summary = "Get all breathing exercises",
        description = "Retrieves a list of all breathing exercises"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all exercises",
            content = @Content(schema = @Schema(implementation = BreathingExerciseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<BreathingExerciseDto> getOfficialExercises() {
        return exerciseRepository.findByIsOfficialTrue().stream()
            .map(ex -> {
                BreathingExerciseDto dto = new BreathingExerciseDto(ex);
                Double avg = feedbackRepository.findAverageRatingByExercise(ex);
                dto.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null); // arrondi à 1 décimale
                return dto;
            })
            .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public List<BreathingExerciseDto> getUserExercises() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());
        return exerciseRepository.findByCreator(user).stream()
            .map(BreathingExerciseDto::new)
            .collect(Collectors.toList());
    }

    @Operation(
        summary = "Get exercise by ID",
        description = "Retrieves a specific breathing exercise by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the exercise",
            content = @Content(schema = @Schema(implementation = BreathingExerciseDto.class))),
        @ApiResponse(responseCode = "404", description = "Exercise not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BreathingExerciseDto> getExerciseById(@PathVariable("id") Long idExercice) {
        try {
            logger.debug("Received request to get exercise with id: {}", idExercice);
            BreathingExercise exercise = breathingExerciseService.getExerciseById(idExercice);
            logger.debug("Successfully retrieved exercise with id: {}", idExercice);
            return ResponseEntity.ok(new BreathingExerciseDto(exercise));
        } catch (EntityNotFoundException e) {
            logger.warn("Exercise not found with id: {}", idExercice);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving exercise with id: {}", idExercice, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Create new exercise",
        description = "Creates a new breathing exercise"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the exercise",
            content = @Content(schema = @Schema(implementation = BreathingExerciseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BreathingExerciseDto> createExercise(
            @Valid @RequestBody BreathingExercise exercise) {
        try {
            logger.debug("Received request to create new exercise");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserById(userDetails.getId());
            exercise.setCreator(user);
            BreathingExercise createdExercise = breathingExerciseService.createExercise(exercise);
            logger.debug("Successfully created exercise with id: {}", createdExercise.getIdExercice());
            return ResponseEntity.ok(new BreathingExerciseDto(createdExercise));
        } catch (Exception e) {
            logger.error("Error creating exercise", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/official")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BreathingExerciseDto> createOfficialExercise(
            @Valid @RequestBody BreathingExercise exercise) {
        try {
            logger.debug("Received request to create new official exercise");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserById(userDetails.getId());
            exercise.setCreator(user);
            exercise.setIsOfficial(true);
            BreathingExercise createdExercise = breathingExerciseService.createExercise(exercise);
            logger.debug("Successfully created official exercise with id: {}", createdExercise.getIdExercice());
            return ResponseEntity.ok(new BreathingExerciseDto(createdExercise));
        } catch (Exception e) {
            logger.error("Error creating official exercise", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Update exercise",
        description = "Updates an existing breathing exercise"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the exercise",
            content = @Content(schema = @Schema(implementation = BreathingExerciseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Exercise not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<BreathingExerciseDto> updateExercise(
            @PathVariable("id") Long idExercice,
            @Valid @RequestBody BreathingExercise exercise) {
        try {
            logger.debug("Received request to update exercise with id: {}", idExercice);
            BreathingExercise updatedExercise = breathingExerciseService.updateExercise(idExercice, exercise);
            logger.debug("Successfully updated exercise with id: {}", idExercice);
            return ResponseEntity.ok(new BreathingExerciseDto(updatedExercise));
        } catch (EntityNotFoundException e) {
            logger.warn("Exercise not found with id: {}", idExercice);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating exercise with id: {}", idExercice, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
        summary = "Delete exercise",
        description = "Deletes a breathing exercise"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the exercise"),
        @ApiResponse(responseCode = "404", description = "Exercise not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteExercise(@PathVariable("id") Long idExercice) {
        try {
            logger.debug("Received request to delete exercise with id: {}", idExercice);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserById(userDetails.getId());

            BreathingExercise exercise = breathingExerciseService.getExerciseById(idExercice);

            boolean isAdmin = user.getRoles().contains("ROLE_ADMIN") || user.getRoles().contains("ROLE_SUPER_ADMIN");
            boolean isCreator = exercise.getCreator() != null && exercise.getCreator().getId().equals(user.getId());

            if (!isAdmin && !isCreator) {
                logger.warn("User {} is not authorized to delete exercise {}", user.getId(), idExercice);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            breathingExerciseService.deleteExercise(idExercice);
            logger.debug("Successfully deleted exercise with id: {}", idExercice);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            logger.warn("Exercise not found with id: {}", idExercice);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting exercise with id: {}", idExercice, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 