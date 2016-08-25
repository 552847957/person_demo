package com.wondersgroup.healthcloud.services.article.impl;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.article.NewsArticleCategoryRepo;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
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
    private DataSource dataSource;
    private JdbcTemplate jt;

    @Override
    public int updateNewsArticleCategory(NewsArticleCategory newsArticleCategory) {
        Date date=new Date();
        newsArticleCategory.setUpdate_time(date);
        return newsArticleCategoryRepo.saveAndFlush(newsArticleCategory).getId();
    }

    @Override
    public List<NewsArticleCategory> findNewsCategoryByKeys(Map<String, Object> parm) {

        return newsArticleCategoryRepo.queryNewsArticleCategory();
    }

    @Override
    public List<NewsArticleCategory> findNewsCategoryByArea(String area) {
        return newsArticleCategoryRepo.findNewsCategoryByArea(area);
    }

    @Override
    public NewsArticleCategory findNewsCategory(int id) {

        return newsArticleCategoryRepo.ArticleCategoryById(id);
    }


    private JdbcTemplate getJt() {
        if (jt == null) {
            jt = new JdbcTemplate(dataSource);
        }
        return jt;
    }
}
