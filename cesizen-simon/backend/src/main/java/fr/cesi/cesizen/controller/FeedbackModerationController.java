package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.dto.ExerciseFeedbackDto;
import fr.cesi.cesizen.service.ExerciseFeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RateLimiter(name = "backendA")
public class FeedbackModerationController {
    private final ExerciseFeedbackService feedbackService;

    public FeedbackModerationController(ExerciseFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<ExerciseFeedbackDto>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }
} 