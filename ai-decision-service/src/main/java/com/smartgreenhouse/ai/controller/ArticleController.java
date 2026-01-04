package com.smartgreenhouse.ai.controller;

import com.smartgreenhouse.ai.entity.Article;
import com.smartgreenhouse.ai.repository.ArticleRepository;
import com.smartgreenhouse.common.core.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ai/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleRepository articleRepository;

    /** 获取文章列表 */
    @GetMapping
    public R<List<Article>> listArticles(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "cropType", required = false) String cropType) {
        if (category != null) {
            return R.ok(articleRepository.findByCategory(category));
        }
        if (cropType != null) {
            return R.ok(articleRepository.findByCropType(cropType));
        }
        return R.ok(articleRepository.findAll());
    }

    /** 搜索文章 */
    @GetMapping("/search")
    public R<List<Article>> searchArticles(@RequestParam("keyword") String keyword) {
        return R.ok(articleRepository.search(keyword));
    }

    /** 热门文章 */
    @GetMapping("/hot")
    public R<List<Article>> hotArticles() {
        return R.ok(articleRepository.findTop10ByOrderByViewCountDesc());
    }

    /** 获取文章详情 */
    @GetMapping("/{id}")
    public R<Article> getArticle(@PathVariable("id") String id) {
        return articleRepository.findById(id).map(article -> {
            article.setViewCount(article.getViewCount() + 1);
            articleRepository.save(article);
            return R.ok(article);
        }).orElse(R.fail(404, "文章不存在"));
    }

    /** 创建文章 */
    @PostMapping
    public R<Article> createArticle(@RequestBody Article article) {
        return R.ok(articleRepository.save(article));
    }

    /** 更新文章 */
    @PutMapping("/{id}")
    public R<Article> updateArticle(@PathVariable("id") String id, @RequestBody Article dto) {
        return articleRepository.findById(id).map(article -> {
            if (dto.getTitle() != null) article.setTitle(dto.getTitle());
            if (dto.getContent() != null) article.setContent(dto.getContent());
            if (dto.getCategory() != null) article.setCategory(dto.getCategory());
            if (dto.getCropType() != null) article.setCropType(dto.getCropType());
            if (dto.getTags() != null) article.setTags(dto.getTags());
            if (dto.getCoverImage() != null) article.setCoverImage(dto.getCoverImage());
            return R.ok(articleRepository.save(article));
        }).orElse(R.fail(404, "文章不存在"));
    }

    /** 删除文章 */
    @DeleteMapping("/{id}")
    public R<String> deleteArticle(@PathVariable("id") String id) {
        if (!articleRepository.existsById(id)) {
            return R.fail(404, "文章不存在");
        }
        articleRepository.deleteById(id);
        return R.ok("删除成功");
    }
}
