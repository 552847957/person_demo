package com.wondersgroup.healthcloud.jpa.repository.article;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by dukuanxin on 2016/8/16.
 */
public interface NewsArticleRepo extends JpaRepository<NewsArticle,String> {
    @Query(nativeQuery = true,value = "select * from app_tb_news_article where is_visable = '1' and CONCAT(',',category_ids,',')  like %?1% order by update_time desc limit ?2,?3")
    List<NewsArticle> queryNewsArticleByCatId(String categoryId,int pageNo,int pageSize);

}
