package com.wondersgroup.healthcloud.services.article.impl;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.article.ArticleRepository;
import com.wondersgroup.healthcloud.jpa.repository.article.NewsArticleCategoryRepo;
import com.wondersgroup.healthcloud.jpa.repository.article.NewsArticleRepo;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/30.
 */
@Service("manageNewsArticleCategotyServiceImpl")
public class ManageNewsArticleCategotyServiceImpl implements ManageNewsArticleCategotyService{

    @Autowired
    private NewsArticleCategoryRepo newsArticleCategoryRepo;

    @Autowired
    private NewsArticleRepo newsArticleRepo;

    @Override
    public int addNewsArticleCategory(NewsArticleCategory newsArticleCategory) {
        return 0;
    }

    @Override
    public int updateNewsArticleCategory(NewsArticleCategory newsArticleCategory) {
        return 0;
    }

    @Override
    public List<NewsArticleCategory> findNewsCategoryByKeys(Map<String, Object> parm) {

        return newsArticleCategoryRepo.queryNewsArticleCategory();
    }

    @Override
    public int countRow(Map<String, Object> parm) {
        return 0;
    }
}
