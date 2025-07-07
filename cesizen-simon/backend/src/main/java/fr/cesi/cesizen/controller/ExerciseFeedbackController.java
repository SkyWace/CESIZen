package fr.cesi.cesizen.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import fr.cesi.cesizen.dto.ExerciseFeedbackDto;
import fr.cesi.cesizen.model.User;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.ExerciseFeedbackService;
import fr.cesi.cesizen.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises/{exerciseId}/feedback")
@RateLimiter(name = "backendA")
public class ExerciseFeedbackController {
    private final ExerciseFeedbackService feedbackService;
    private final UserService userService;

    public ExerciseFeedbackController(ExerciseFeedbackService feedbackService, UserService userService) {
        this.feedbackService = feedbackService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseFeedbackDto>> getFeedbacks(@PathVariable Long exerciseId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForExercise(exerciseId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExerciseFeedbackDto> createFeedback(
            @PathVariable Long exerciseId,
            @Valid @RequestBody Map<String, Object> payload) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.getUserById(userDetails.getId());

        String comment = (String) payload.get("comment");
        Integer rating = (Integer) payload.get("rating");

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().build();
        }

        ExerciseFeedbackDto feedback = feedbackService.createFeedback(user, exerciseId, comment, rating);
        return ResponseEntity.ok(feedback);
    }

    @DeleteMapping("/{feedbackId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long exerciseId, @PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/api/feedback", produces = "application/json")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<ExerciseFeedbackDto>> getAllFeedbacks() {
        List<ExerciseFeedbackDto> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }
} 