package com.wondersgroup.healthcloud.services.article;

import com.wondersgroup.healthcloud.jpa.entity.article.Article;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleCategory;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleSearchCriteria;

import java.util.List;

public interface ManageArticleService {

    public Article findArticleInfoById(int id);

    public List<Article> findArticleListByIds(List<Integer> ids);

    /**
     * 根据分类查询改分类下面的所有文章
     * @param categoryId
     * @return List
     */
    public List<Article> findArticleListByCategoryId(String categoryId, int pageNo, int pageSize);

    /**
     * 根据某一慢病下面的所有文章
     * @param
     * @return List
     */
    public List<Article> findArticleListByDiseaseId(int diseaseId, int pageNo, int pageSize);

    /**
     * 根据条件文章
     * @param
     * @return List
     */
    public List<Article> findArticleListByCriteria(ArticleSearchCriteria criteria);

    /**
     * 获取慢病下所有的分类，排序后的
     * @param diseaseId
     * @return list排序后的
     */
    public List<ArticleCategory> findCategoryByDiseaseId(int diseaseId);

    /**
     * 获取慢病下所有有效的分类，排序后的
     * @param diseaseId
     * @return list排序后的
     */
    public List<ArticleCategory> findValidCategoryByDiseaseId(int diseaseId);

    public int countArticleByCriteria(ArticleSearchCriteria criteria);

    /**
     * 根据分类查询改分类下面的所有文章数量
     * @param articleCategoryId
     * @return int
     */
    public int countArticleByCategoryId(int articleCategoryId);

    public int countArticleByDiseaseId(int diseaseId);

    /**
     * 追加访问量
     * @param articleId
     * @return
     */
    public int addViewPv(int articleId);

    public String getVersion();

}
