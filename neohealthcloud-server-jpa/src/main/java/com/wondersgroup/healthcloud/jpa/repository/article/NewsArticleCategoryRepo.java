package com.wondersgroup.healthcloud.jpa.repository.article;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Surface Book on 2016/8/16.
 */
public interface NewsArticleCategoryRepo extends JpaRepository<NewsArticleCategory,String> {
    @Query("select nac from NewsArticleCategory nac where nac.is_visable=1 order by nac.rank")
    List<NewsArticleCategory> queryNewsArticleCategory();
    @Query("select nac from NewsArticleCategory nac where nac.id=?1")
    NewsArticleCategory ArticleCategoryById(int id);
}
