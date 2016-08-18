package com.wondersgroup.healthcloud.services.article.impl;

import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.repository.article.NewsArticleRepo;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/30.
 */
@Service("manageNewsArticleServiceImpl")
public class ManageNewsArticleServiceImpl implements ManageNewsArticleService{

    @Autowired
    private NewsArticleRepo newsArticleRepo;
    @Override
    public NewsArticle findArticleInfoById(int id) {
        return null;
    }

    @Override
    public List<NewsArticle> findArticleListByIds(List<Integer> ids) {
        return null;
    }

    @Override
    public List<NewsArticle> findArtileListByKeys(Map<String, Object> parm) {
        return null;
    }


    @Override
    public int updateNewsAritile(NewsArticle da) {
        return newsArticleRepo.saveAndFlush(da).getId();
    }

    @Override
    public List<NewsArticle> findListByCategoryId(String categoryId, int pageNo, int pageSize) {

        return newsArticleRepo.queryNewsArticleByCatId(categoryId,pageNo,pageSize);
    }

    @Override
    public List<NewsArticle> findAppShowListByCategoryId(String categoryId, int pageNo, int pageSize) {
        return newsArticleRepo.queryNewsArticleByCatId(categoryId,pageNo,pageSize);
    }

    @Override
    public int countArticleByCategoryId(String categoryId) {
        return 0;
    }

    @Override
    public int countRow() {
        return 0;
    }

    @Override
    public int addViewPv(Integer id) {
        return 0;
    }

    @Override
    public List<NewsArticle> findAppShowListByTitle(String title, int pageNo, int pageSize) {
        return newsArticleRepo.queryNewsArticleByTitle(title,pageNo,pageSize);
    }

    @Override
    public List<NewsArticleListAPIEntity> findArticleForFirst(String areaId, int pageNo, int pageSize) {
        List<NewsArticle> list=newsArticleRepo.queryNewsArticleByAreaId(areaId,pageNo*pageSize,pageSize);

        return getArticleEntityList(list);
    }

    private List<NewsArticleListAPIEntity> getArticleEntityList(List<NewsArticle> resourceList){

        if(null == resourceList || resourceList.size() == 0){
            return null;
        }
        List<NewsArticleListAPIEntity> list = new ArrayList<>();
        for (NewsArticle article : resourceList){
            list.add(new NewsArticleListAPIEntity(article));
        }
        return list;
    }
}
