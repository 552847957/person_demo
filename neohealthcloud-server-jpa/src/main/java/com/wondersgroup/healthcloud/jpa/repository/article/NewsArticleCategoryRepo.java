package com.wondersgroup.healthcloud.jpa.repository.article;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by dukuanxin on 2016/8/16.
 */
public interface NewsArticleCategoryRepo extends JpaRepository<NewsArticleCategory,String> {
    @Query("select nac from NewsArticleCategory nac where nac.is_visable=1 and nac.by_area=?1  order by nac.rank")
    List<NewsArticleCategory> queryAppNewsArticleCategory(String area);

    @Query("select nac from NewsArticleCategory nac where nac.id=?1")
    NewsArticleCategory ArticleCategoryById(int id);

    @Query("select nac from NewsArticleCategory nac where nac.by_area=?1 order by nac.rank")
    List<NewsArticleCategory> findNewsCategoryByArea(String area);

    @Query(nativeQuery = true,value ="SELECT t1.* FROM app_tb_neoarticle_category t1 left JOIN app_tb_neoarticle_area t2 ON t2.category_id=t1.id" +
            " WHERE t2.article_id=?1 AND t2.main_area=?2")
    List<NewsArticleCategory> findNewsArticleBelongArea(int articleId,String area);

    @Query(nativeQuery = true,value ="SELECT t1.* FROM app_tb_neoarticle_category t1 left JOIN app_tb_neoarticle_area t2 ON t2.category_id=t1.id" +
            " WHERE t2.article_id<>?1 AND t2.main_area=?2")
    List<NewsArticleCategory> findNewsArticleNotBelongArea(int articleId,String area);
}
