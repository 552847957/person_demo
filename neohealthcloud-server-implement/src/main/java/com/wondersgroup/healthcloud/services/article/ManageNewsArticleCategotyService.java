package com.wondersgroup.healthcloud.services.article;


import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;

import java.util.List;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/8/15.
 */
public interface ManageNewsArticleCategotyService {

    public int updateNewsArticleCategory(NewsArticleCategory newsArticleCategory);

    public List<NewsArticleCategory> findAppNewsCategoryByArea(String area);

    public List<NewsArticleCategory> findNewsCategoryByArea(String area);

    public NewsArticleCategory findNewsCategory(int id);

    List<NewsArticleCategory> findNewsArticleNotBelongArea(int articleId,String area);

    List<NewsArticleCategory> findNewsArticleBelongArea(int articleId,String area);

}
