package com.smartgreenhouse.ai.repository;

import com.smartgreenhouse.ai.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {
    List<Article> findByCategory(String category);
    List<Article> findByCropType(String cropType);
    
    @Query("SELECT a FROM Article a WHERE a.title LIKE %?1% OR a.content LIKE %?1%")
    List<Article> search(String keyword);
    
    List<Article> findTop10ByOrderByViewCountDesc();
}
