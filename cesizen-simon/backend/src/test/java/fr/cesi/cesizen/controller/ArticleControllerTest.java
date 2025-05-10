package fr.cesi.cesizen.controller;

import fr.cesi.cesizen.model.Article;
import fr.cesi.cesizen.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    private Article testArticle;
    private MockMultipartFile testImage;

    @BeforeEach
    void setUp() {
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
        when(articleService.getAllArticles()).thenReturn(articles);

        List<Article> result = articleController.getAllArticles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArticle, result.get(0));
        verify(articleService).getAllArticles();
    }

    @Test
    void getPendingArticles_Success() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleService.getPendingArticles()).thenReturn(articles);

        List<Article> result = articleController.getPendingArticles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArticle, result.get(0));
        verify(articleService).getPendingArticles();
    }

    @Test
    void getArticleById_Success() {
        when(articleService.getArticleById(1L)).thenReturn(testArticle);

        Article result = articleController.getArticleById(1L);

        assertNotNull(result);
        assertEquals(testArticle, result);
        verify(articleService).getArticleById(1L);
    }

    @Test
    void createArticle_Success() {
        when(articleService.createArticle(any(Article.class), any(MultipartFile.class))).thenReturn(testArticle);

        Article result = articleController.createArticle(testArticle, testImage);

        assertNotNull(result);
        assertEquals(testArticle, result);
        verify(articleService).createArticle(testArticle, testImage);
    }

    @Test
    void updateArticle_Success() {
        when(articleService.updateArticle(anyLong(), any(Article.class), any(MultipartFile.class))).thenReturn(testArticle);

        Article result = articleController.updateArticle(1L, testArticle, testImage);

        assertNotNull(result);
        assertEquals(testArticle, result);
        verify(articleService).updateArticle(1L, testArticle, testImage);
    }

    @Test
    void approveArticle_Success() {
        Article approvedArticle = new Article();
        approvedArticle.setId(1L);
        approvedArticle.setApprovalStatus(Article.ApprovalStatus.APPROVED);
        when(articleService.approveArticle(1L)).thenReturn(approvedArticle);

        Article result = articleController.approveArticle(1L);

        assertNotNull(result);
        assertEquals(Article.ApprovalStatus.APPROVED, result.getApprovalStatus());
        verify(articleService).approveArticle(1L);
    }

    @Test
    void rejectArticle_Success() {
        Article rejectedArticle = new Article();
        rejectedArticle.setId(1L);
        rejectedArticle.setApprovalStatus(Article.ApprovalStatus.REJECTED);
        when(articleService.rejectArticle(1L)).thenReturn(rejectedArticle);

        Article result = articleController.rejectArticle(1L);

        assertNotNull(result);
        assertEquals(Article.ApprovalStatus.REJECTED, result.getApprovalStatus());
        verify(articleService).rejectArticle(1L);
    }

    @Test
    void deleteArticle_Success() {
        doNothing().when(articleService).deleteArticle(1L);

        ResponseEntity<?> response = articleController.deleteArticle(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(articleService).deleteArticle(1L);
    }

    @Test
    void getApprovedArticles_Success() {
        List<Article> articles = Arrays.asList(testArticle);
        Page<Article> articlePage = new PageImpl<>(articles);
        when(articleService.getApprovedArticles(anyInt(), anyInt())).thenReturn(articlePage);

        ResponseEntity<Page<Article>> response = articleController.getApprovedArticles(0, 10);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(articlePage, response.getBody());
        verify(articleService).getApprovedArticles(0, 10);
    }
}