package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.Article;
import fr.cesi.cesizen.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final Path uploadDir = Paths.get("uploads/articles");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getPendingArticles() {
        return articleRepository.findByApprovalStatus(Article.ApprovalStatus.PENDING);
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public Article createArticle(Article article, MultipartFile image) {
        article.setApprovalStatus(Article.ApprovalStatus.PENDING);
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            try {
                Files.copy(image.getInputStream(), uploadDir.resolve(fileName));
                article.setImageUrl("/uploads/articles/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }
        return articleRepository.save(article);
    }

    public Article approveArticle(Long id) {
        Article article = getArticleById(id);
        article.setApprovalStatus(Article.ApprovalStatus.APPROVED);
        return articleRepository.save(article);
    }

    public Article rejectArticle(Long id) {
        Article article = getArticleById(id);
        article.setApprovalStatus(Article.ApprovalStatus.REJECTED);
        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, Article articleDetails, MultipartFile image) {
        Article article = getArticleById(id);
        
        article.setTitle(articleDetails.getTitle());
        article.setDescription(articleDetails.getDescription());
        article.setContent(articleDetails.getContent());
        article.setApprovalStatus(Article.ApprovalStatus.PENDING); // Reset approval status on update

        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (article.getImageUrl() != null) {
                try {
                    Files.deleteIfExists(uploadDir.resolve(article.getImageUrl().replace("/uploads/articles/", "")));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete old image", e);
                }
            }

            // Save new image
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            try {
                Files.copy(image.getInputStream(), uploadDir.resolve(fileName));
                article.setImageUrl("/uploads/articles/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store new image", e);
            }
        }

        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        Article article = getArticleById(id);
        
        // Delete image if exists
        if (article.getImageUrl() != null) {
            try {
                Files.deleteIfExists(uploadDir.resolve(article.getImageUrl().replace("/uploads/articles/", "")));
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image", e);
            }
        }
        
        articleRepository.delete(article);
    }

    public Page<Article> getApprovedArticles(int page, int size) {
        return articleRepository.findByApprovalStatus(Article.ApprovalStatus.APPROVED, PageRequest.of(page, size));
    }
} 