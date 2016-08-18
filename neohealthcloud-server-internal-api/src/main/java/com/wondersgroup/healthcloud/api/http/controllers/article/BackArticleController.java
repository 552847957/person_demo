package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.jpa.entity.article.ArticleArea;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.article.ArticleAreaRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by dukuanxin on 2016/8/12.
 */
@RestController
@RequestMapping("/back/article")
public class BackArticleController {

    @Resource
    private ManageNewsArticleCategotyService manageNewsArticleCategotyService;
    @Resource
    private ArticleAreaRepository articleAreaRepository;
    @Resource
    private ManageNewsArticleService manageNewsArticleServiceImpl;
    @RequestMapping("/saveArticle")
    public void updateArticle(@RequestBody NewsArticle article){
        manageNewsArticleServiceImpl.updateNewsAritile(article);
    }
    @RequestMapping("/saveArticleCategory")
    public void updateArticleCategory(@RequestBody NewsArticleCategory articleCategory){
        manageNewsArticleCategotyService.updateNewsArticleCategory(articleCategory);
    }
    @RequestMapping("/putArticle")
    public void putArticle(@RequestBody ArticleArea articleArea){
        articleAreaRepository.saveAndFlush(articleArea);
    }

    @RequestMapping("/queryAllArticle")
    public void putArticle(){
        List<NewsArticleCategory> newsCategory = manageNewsArticleCategotyService.findNewsCategory();
    }
}
