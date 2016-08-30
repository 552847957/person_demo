package com.wondersgroup.healthcloud.jpa.repository.article;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by dukuanxin on 2016/8/16.
 */
public interface NewsArticleRepo extends JpaRepository<NewsArticle,String> {
    @Query(nativeQuery = true,value = "SELECT  t1.* FROM app_tb_neoarticle t1 LEFT JOIN app_tb_neoarticle_area t2 " +
            "ON t1.id=t2.article_id WHERE t2.is_visable=1 and t2.category_id=?1 ORDER BY t1.update_time desc limit ?2,?3")
    List<NewsArticle> queryNewsArticleByCatId(String categoryId,int pageNo,int pageSize);
    @Query(nativeQuery = true,value = "select * from app_tb_neoarticle where keyword like %?1% order by update_time desc limit ?2,?3")
    List<NewsArticle> queryNewsArticleByTitle(String word,int pageNo,int pageSize);

    @Query(nativeQuery = true,value = "SELECT t1.* FROM app_tb_neoarticle t1 LEFT JOIN app_tb_neoarticle_area t2 ON t1.id=t2.article_id WHERE  main_area=?1 order by t1.update_time desc limit ?2,?3")
    List<NewsArticle> queryNewsArticleByAreaId(String areaId,int pageNo,int pageSize);

    @Query(nativeQuery = true,value = "SELECT t1.* FROM app_tb_neoarticle t1 LEFT JOIN app_tb_neoarticle_favorite t2 ON t1.id=t2.article_id WHERE  t2.user_id=?1 order by t1.update_time desc limit ?2,?3")
    List<NewsArticle> queryCollectionNewsArticle(String areaId,int pageNo,int pageSize);

    @Query(nativeQuery = true,value = "SELECT * FROM app_tb_neoarticle where id=?1")
    NewsArticle queryArticleById(int id);
    //查询首页资讯
    @Query(nativeQuery = true,value = "SELECT t1.* FROM app_tb_neoarticle t1 LEFT JOIN app_tb_neoforward_article t2 ON t1.id=t2.article_id\n" +
            "WHERE t2.start_time<=NOW() AND t2.end_time>=NOW() AND t2.is_visable=1 AND t2.main_area=?1 order by t2.rank desc limit ?2,?3")
    List<NewsArticle> queryNewsArticleForHomePage(String areaId,int pageNo,int pageSize);
}
