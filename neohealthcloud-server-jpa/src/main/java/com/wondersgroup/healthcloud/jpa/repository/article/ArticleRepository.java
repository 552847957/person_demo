package com.wondersgroup.healthcloud.jpa.repository.article;

import com.wondersgroup.healthcloud.jpa.entity.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by dukuanxin on 2016/8/12.
 */
public interface ArticleRepository extends JpaRepository<Article,String> {

    @Query(nativeQuery = true,value = "select * from article_tb where is_visable = '1' and CONCAT(',',category_ids,',')  like %?1% order by update_time desc limit ?2,?3 ")
    List<Article> findArticle(String categoryId, int pageNo, int pageSize);
}
