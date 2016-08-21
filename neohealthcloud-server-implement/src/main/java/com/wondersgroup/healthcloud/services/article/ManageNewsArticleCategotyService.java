package com.wondersgroup.healthcloud.services.article;


import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;

import java.util.List;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/8/15.
 */
public interface ManageNewsArticleCategotyService {

    public int addNewsArticleCategory(NewsArticleCategory newsArticleCategory);

    public int updateNewsArticleCategory(NewsArticleCategory newsArticleCategory);

    public List<NewsArticleCategory> findNewsCategoryByKeys(Map<String, Object> parm);

    public int countRow(Map<String, Object> parm);

    public List<NewsArticleCategory> findNewsCategory();

    public NewsArticleCategory findNewsCategory(int id);
}
