package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.BreathingExerciseHistory;
import fr.cesi.cesizen.payload.request.BreathingExerciseHistoryRequest;
import fr.cesi.cesizen.security.services.UserDetailsImpl;
import fr.cesi.cesizen.service.BreathingExerciseHistoryService;
import fr.cesi.cesizen.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import fr.cesi.cesizen.dto.BreathingExerciseHistoryDto;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/breathing-history")
@Tag(name = "Breathing Exercise History", description = "Historique des exercices de respiration")
@RateLimiter(name = "backendA")
public class BreathingExerciseHistoryController {
    private final BreathingExerciseHistoryService historyService;
    private final UserService userService;

    public BreathingExerciseHistoryController(BreathingExerciseHistoryService historyService, UserService userService) {
        this.historyService = historyService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BreathingExerciseHistory> saveHistory(@Valid @RequestBody BreathingExerciseHistoryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var user = userService.getUserById(userDetails.getId());
        var history = historyService.saveHistory(user, request.getExerciseId());
        return ResponseEntity.ok(history);
    }

    @GetMapping
    public ResponseEntity<?> getMyHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        var user = userService.getUserById(userDetails.getId());
        var history = historyService.getHistoryForUser(user);
        List<BreathingExerciseHistoryDto> dtos = history.stream().map(h ->
            new BreathingExerciseHistoryDto(
                h.getId(),
                h.getExercise().getIdExercice(),
                h.getExercise().getNom(),
                (h.getExercise().getDureeInspiration() + h.getExercise().getDureeExpiration() + (h.getExercise().getDureeApnee() != null ? h.getExercise().getDureeApnee() : 0)) * h.getExercise().getNombreCycles(),
                h.getCompletedAt()
            )
        ).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
} 