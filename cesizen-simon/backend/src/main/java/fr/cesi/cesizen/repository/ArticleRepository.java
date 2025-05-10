package fr.cesi.cesizen.repository;

import fr.cesi.cesizen.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByApprovalStatus(Article.ApprovalStatus status);
    Page<Article> findByApprovalStatus(Article.ApprovalStatus status, Pageable pageable);
} 