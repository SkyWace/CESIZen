package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.MentalHealthInfo;
import fr.cesi.cesizen.model.MentalHealthInfo.Category;
import fr.cesi.cesizen.service.MentalHealthInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mental-health")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:8101", "http://localhost:5173", "http://localhost:5174", "http://localhost:3000"}, maxAge = 3600)
@Tag(name = "Mental Health Information", description = "Mental health information management APIs")
@RateLimiter(name = "backendA")
public class MentalHealthInfoController {

    private final MentalHealthInfoService mentalHealthInfoService;

    public MentalHealthInfoController(MentalHealthInfoService mentalHealthInfoService) {
        this.mentalHealthInfoService = mentalHealthInfoService;
    }

    @Operation(
        summary = "Get all mental health information",
        description = "Retrieves a list of all mental health information entries"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public ResponseEntity<List<MentalHealthInfo>> getAllInfo() {
        return ResponseEntity.ok(mentalHealthInfoService.getAllInfo());
    }

    @Operation(
        summary = "Get information by ID",
        description = "Retrieves a specific mental health information entry by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "404", description = "Information not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MentalHealthInfo> getInfoById(@PathVariable Long id) {
        return ResponseEntity.ok(mentalHealthInfoService.getInfoById(id));
    }

    @Operation(
        summary = "Get information by category",
        description = "Retrieves mental health information entries filtered by category"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MentalHealthInfo>> getInfoByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(mentalHealthInfoService.getInfoByCategory(category));
    }

    @Operation(
        summary = "Get featured information",
        description = "Retrieves all featured mental health information entries"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved featured information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/featured")
    public ResponseEntity<List<MentalHealthInfo>> getFeaturedInfo() {
        return ResponseEntity.ok(mentalHealthInfoService.getFeaturedInfo());
    }

    @Operation(
        summary = "Search information",
        description = "Searches for mental health information by title"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<MentalHealthInfo>> searchInfo(@RequestParam String query) {
        return ResponseEntity.ok(mentalHealthInfoService.searchInfo(query));
    }

    @Operation(
        summary = "Create new information",
        description = "Creates a new mental health information entry"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created the information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MentalHealthInfo> createInfo(@Valid @RequestBody MentalHealthInfo info) {
        return ResponseEntity.ok(mentalHealthInfoService.createInfo(info));
    }

    @Operation(
        summary = "Update information",
        description = "Updates an existing mental health information entry"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the information",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Information not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MentalHealthInfo> updateInfo(
            @PathVariable Long id,
            @Valid @RequestBody MentalHealthInfo info) {
        return ResponseEntity.ok(mentalHealthInfoService.updateInfo(id, info));
    }

    @Operation(
        summary = "Delete information",
        description = "Deletes a mental health information entry"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the information"),
        @ApiResponse(responseCode = "404", description = "Information not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteInfo(@PathVariable Long id) {
        mentalHealthInfoService.deleteInfo(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Toggle featured status",
        description = "Toggles the featured status of a mental health information entry"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully toggled featured status",
            content = @Content(schema = @Schema(implementation = MentalHealthInfo.class))),
        @ApiResponse(responseCode = "404", description = "Information not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PutMapping("/{id}/toggle-featured")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MentalHealthInfo> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(mentalHealthInfoService.toggleFeatured(id));
    }
} 