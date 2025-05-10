package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void testSaveArticle() {
        // Create a new article
        Article article = new Article();
        article.setTitle("Test Article");
        article.setDescription("Test Description");
        article.setContent("Test Content");
        article.setApprovalStatus(Article.ApprovalStatus.PENDING);

        // Save the article
        Article savedArticle = articleRepository.save(article);

        // Verify the article was saved
        assertThat(savedArticle).isNotNull();
        assertThat(savedArticle.getId()).isNotNull();
        assertThat(savedArticle.getTitle()).isEqualTo("Test Article");
        assertThat(savedArticle.getDescription()).isEqualTo("Test Description");
        assertThat(savedArticle.getContent()).isEqualTo("Test Content");
        assertThat(savedArticle.getApprovalStatus()).isEqualTo(Article.ApprovalStatus.PENDING);
        assertThat(savedArticle.getCreatedAt()).isNotNull();
        assertThat(savedArticle.getUpdatedAt()).isNotNull();
    }

    @Test
    public void testFindArticleById() {
        // Create and save a new article
        Article article = new Article();
        article.setTitle("Test Article");
        article.setDescription("Test Description");
        article.setContent("Test Content");
        article.setApprovalStatus(Article.ApprovalStatus.PENDING);
        Article savedArticle = articleRepository.save(article);

        // Find the article by ID
        Optional<Article> foundArticle = articleRepository.findById(savedArticle.getId());

        // Verify the article was found
        assertThat(foundArticle).isPresent();
        assertThat(foundArticle.get().getId()).isEqualTo(savedArticle.getId());
        assertThat(foundArticle.get().getTitle()).isEqualTo("Test Article");
    }

    @Test
    public void testUpdateArticle() {
        // Create and save a new article
        Article article = new Article();
        article.setTitle("Test Article");
        article.setDescription("Test Description");
        article.setContent("Test Content");
        article.setApprovalStatus(Article.ApprovalStatus.PENDING);
        Article savedArticle = articleRepository.save(article);

        // Update the article
        savedArticle.setTitle("Updated Title");
        savedArticle.setApprovalStatus(Article.ApprovalStatus.APPROVED);
        Article updatedArticle = articleRepository.save(savedArticle);

        // Verify the article was updated
        assertThat(updatedArticle.getId()).isEqualTo(savedArticle.getId());
        assertThat(updatedArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedArticle.getApprovalStatus()).isEqualTo(Article.ApprovalStatus.APPROVED);
    }

    @Test
    public void testDeleteArticle() {
        // Create and save a new article
        Article article = new Article();
        article.setTitle("Test Article");
        article.setDescription("Test Description");
        article.setContent("Test Content");
        article.setApprovalStatus(Article.ApprovalStatus.PENDING);
        Article savedArticle = articleRepository.save(article);

        // Delete the article
        articleRepository.delete(savedArticle);

        // Verify the article was deleted
        Optional<Article> deletedArticle = articleRepository.findById(savedArticle.getId());
        assertThat(deletedArticle).isEmpty();
    }

    @Test
    public void testFindByApprovalStatus() {
        // Create and save articles with different approval statuses
        Article pendingArticle = new Article();
        pendingArticle.setTitle("Pending Article");
        pendingArticle.setDescription("Pending Description");
        pendingArticle.setContent("Pending Content");
        pendingArticle.setApprovalStatus(Article.ApprovalStatus.PENDING);
        articleRepository.save(pendingArticle);

        Article approvedArticle = new Article();
        approvedArticle.setTitle("Approved Article");
        approvedArticle.setDescription("Approved Description");
        approvedArticle.setContent("Approved Content");
        approvedArticle.setApprovalStatus(Article.ApprovalStatus.APPROVED);
        articleRepository.save(approvedArticle);

        Article rejectedArticle = new Article();
        rejectedArticle.setTitle("Rejected Article");
        rejectedArticle.setDescription("Rejected Description");
        rejectedArticle.setContent("Rejected Content");
        rejectedArticle.setApprovalStatus(Article.ApprovalStatus.REJECTED);
        articleRepository.save(rejectedArticle);

        // Find articles by approval status
        List<Article> pendingArticles = articleRepository.findByApprovalStatus(Article.ApprovalStatus.PENDING);
        List<Article> approvedArticles = articleRepository.findByApprovalStatus(Article.ApprovalStatus.APPROVED);
        List<Article> rejectedArticles = articleRepository.findByApprovalStatus(Article.ApprovalStatus.REJECTED);

        // Verify the correct articles were found
        assertThat(pendingArticles).hasSize(1);
        assertThat(pendingArticles.get(0).getTitle()).isEqualTo("Pending Article");

        assertThat(approvedArticles).hasSize(1);
        assertThat(approvedArticles.get(0).getTitle()).isEqualTo("Approved Article");

        assertThat(rejectedArticles).hasSize(1);
        assertThat(rejectedArticles.get(0).getTitle()).isEqualTo("Rejected Article");
    }

    @Test
    public void testFindByApprovalStatusWithPageable() {
        // Create and save multiple approved articles
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setTitle("Approved Article " + i);
            article.setDescription("Approved Description " + i);
            article.setContent("Approved Content " + i);
            article.setApprovalStatus(Article.ApprovalStatus.APPROVED);
            articleRepository.save(article);
        }

        // Find approved articles with pagination
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Article> approvedArticlesPage = articleRepository.findByApprovalStatus(Article.ApprovalStatus.APPROVED, pageRequest);

        // Verify pagination works correctly
        assertThat(approvedArticlesPage.getContent()).hasSize(5);
        assertThat(approvedArticlesPage.getTotalElements()).isEqualTo(10);
        assertThat(approvedArticlesPage.getTotalPages()).isEqualTo(2);
        assertThat(approvedArticlesPage.getNumber()).isEqualTo(0);

        // Check second page
        PageRequest secondPageRequest = PageRequest.of(1, 5);
        Page<Article> secondPage = articleRepository.findByApprovalStatus(Article.ApprovalStatus.APPROVED, secondPageRequest);
        assertThat(secondPage.getContent()).hasSize(5);
        assertThat(secondPage.getNumber()).isEqualTo(1);
    }
}