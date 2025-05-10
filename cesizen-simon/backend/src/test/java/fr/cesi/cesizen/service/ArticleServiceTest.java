package fr.cesi.cesizen.service;

import fr.cesi.cesizen.model.Article;
import fr.cesi.cesizen.repository.ArticleRepository;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Spy
    @InjectMocks
    private ArticleService articleService;

    @TempDir
    Path tempDir;

    private Article testArticle;
    private MockMultipartFile testImage;

    @BeforeEach
    void setUp() {
        // Set up the upload directory to use the temp directory
        try {
            Field uploadDirField = ArticleService.class.getDeclaredField("uploadDir");
            uploadDirField.setAccessible(true);
            uploadDirField.set(articleService, tempDir);
        } catch (Exception e) {
            fail("Failed to set up temp directory: " + e.getMessage());
        }

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setDescription("Test Description");
        testArticle.setContent("Test Content");
        testArticle.setImageUrl("/uploads/articles/test-image.jpg");
        testArticle.setCreatedAt(LocalDateTime.now());
        testArticle.setUpdatedAt(LocalDateTime.now());
        testArticle.setApprovalStatus(Article.ApprovalStatus.PENDING);

        testImage = new MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    @Test
    void getAllArticles_Success() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleRepository.findAll()).thenReturn(articles);

        List<Article> result = articleService.getAllArticles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArticle, result.get(0));
        verify(articleRepository).findAll();
    }

    @Test
    void getPendingArticles_Success() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleRepository.findByApprovalStatus(Article.ApprovalStatus.PENDING)).thenReturn(articles);

        List<Article> result = articleService.getPendingArticles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArticle, result.get(0));
        verify(articleRepository).findByApprovalStatus(Article.ApprovalStatus.PENDING);
    }

    @Test
    void getArticleById_Success() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));

        Article result = articleService.getArticleById(1L);

        assertNotNull(result);
        assertEquals(testArticle, result);
        verify(articleRepository).findById(1L);
    }

    @Test
    void getArticleById_NotFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> articleService.getArticleById(1L));
        verify(articleRepository).findById(1L);
    }

    @Test
    void createArticle_Success() {
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.createArticle(testArticle, null);

        assertNotNull(result);
        assertEquals(testArticle, result);
        assertEquals(Article.ApprovalStatus.PENDING, result.getApprovalStatus());
        verify(articleRepository).save(testArticle);
    }

    @Test
    void createArticle_WithImage_Success() throws IOException {
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.createArticle(testArticle, testImage);

        assertNotNull(result);
        assertEquals(testArticle, result);
        assertEquals(Article.ApprovalStatus.PENDING, result.getApprovalStatus());
        assertNotNull(result.getImageUrl());
        assertTrue(result.getImageUrl().startsWith("/uploads/articles/"));
        verify(articleRepository).save(testArticle);
    }

    @Test
    void approveArticle_Success() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.approveArticle(1L);

        assertNotNull(result);
        assertEquals(Article.ApprovalStatus.APPROVED, result.getApprovalStatus());
        verify(articleRepository).save(testArticle);
    }

    @Test
    void rejectArticle_Success() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.rejectArticle(1L);

        assertNotNull(result);
        assertEquals(Article.ApprovalStatus.REJECTED, result.getApprovalStatus());
        verify(articleRepository).save(testArticle);
    }

    @Test
    void updateArticle_Success() {
        Article updatedArticle = new Article();
        updatedArticle.setTitle("Updated Title");
        updatedArticle.setDescription("Updated Description");
        updatedArticle.setContent("Updated Content");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.updateArticle(1L, updatedArticle, null);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("Updated Content", result.getContent());
        assertEquals(Article.ApprovalStatus.PENDING, result.getApprovalStatus());
        verify(articleRepository).save(testArticle);
    }

    @Test
    void deleteArticle_Success() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        doNothing().when(articleRepository).delete(testArticle);

        articleService.deleteArticle(1L);

        verify(articleRepository).delete(testArticle);
    }

    @Test
    void getApprovedArticles_Success() {
        List<Article> articles = Arrays.asList(testArticle);
        Page<Article> articlePage = new PageImpl<>(articles);
        when(articleRepository.findByApprovalStatus(eq(Article.ApprovalStatus.APPROVED), any(PageRequest.class)))
            .thenReturn(articlePage);

        Page<Article> result = articleService.getApprovedArticles(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testArticle, result.getContent().get(0));
        verify(articleRepository).findByApprovalStatus(eq(Article.ApprovalStatus.APPROVED), any(PageRequest.class));
    }
}
