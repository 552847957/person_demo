package com.wondersgroup.healthcloud.services.article.impl;

import com.wondersgroup.healthcloud.jpa.entity.article.Article;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleCategory;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleSearchCriteria;
import com.wondersgroup.healthcloud.jpa.repository.article.ArticleRepository;
import com.wondersgroup.healthcloud.services.article.ManageArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.util.List;

@Service("manageArticleService")
public class ManageArticleServiceImpl implements ManageArticleService {

    @Autowired
    private ArticleRepository articleRepo;
    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jt;

    @Override
    public Article findArticleInfoById(int id) {
        return null;
    }

    @Override
    public List<Article> findArticleListByIds(List<Integer> ids) {
        return null;
    }

    @Override
    public List<Article> findArticleListByCategoryId(String categoryId, int pageNo, int pageSize) {
        return articleRepo.findArticle(','+categoryId+',',pageNo,pageSize);
    }

    @Override
    public List<Article> findArticleListByDiseaseId(int diseaseId, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public List<Article> findArticleListByCriteria(ArticleSearchCriteria criteria) {
        return null;
    }

    @Override
    public List<ArticleCategory> findCategoryByDiseaseId(int diseaseId) {
        return null;
    }

    @Override
    public List<ArticleCategory> findValidCategoryByDiseaseId(int diseaseId) {
        return null;
    }

    @Override
    public int countArticleByCriteria(ArticleSearchCriteria criteria) {
        return 0;
    }

    @Override
    public int countArticleByCategoryId(int articleCategoryId) {
        return 0;
    }

    @Override
    public int countArticleByDiseaseId(int diseaseId) {
        return 0;
    }

    @Override
    public int addViewPv(int articleId) {
        return 0;
    }

    @Override
    public String getVersion() {
        return null;
    }
}
