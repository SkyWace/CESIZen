package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.Article;
import fr.cesi.cesizen.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@RateLimiter(name = "backendA")
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<Article> getPendingArticles() {
        return articleService.getPendingArticles();
    }

    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Article createArticle(
            @RequestPart("article") Article article,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return articleService.createArticle(article, image);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Article updateArticle(
            @PathVariable Long id,
            @RequestPart("article") Article article,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return articleService.updateArticle(id, article, image);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Article approveArticle(@PathVariable Long id) {
        return articleService.approveArticle(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Article rejectArticle(@PathVariable Long id) {
        return articleService.rejectArticle(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<Article>> getApprovedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.getApprovedArticles(page, size));
    }
} 