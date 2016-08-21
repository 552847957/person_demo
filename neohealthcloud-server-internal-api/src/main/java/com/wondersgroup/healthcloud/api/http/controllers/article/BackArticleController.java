package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.api.http.dto.article.NewsArticleCategoryDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleArea;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.article.ArticleAreaRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     *  添加/编辑资讯文章分类
     * @param articleCategory
     */
    @PostMapping("/categorySave")
    public JsonResponseEntity updateArticleCategory(@RequestBody NewsArticleCategory articleCategory){
        JsonResponseEntity response=new JsonResponseEntity();
        manageNewsArticleCategotyService.updateNewsArticleCategory(articleCategory);
        response.setMsg("添加成功");
        return response;
    }

    /**
     * 资讯文章分类列表
     * @return
     */
    @GetMapping("/categoryList")
    public JsonListResponseEntity putArticle(){
        JsonListResponseEntity response=new JsonListResponseEntity();
        List<NewsArticleCategory> newsCategory = manageNewsArticleCategotyService.findNewsCategory();
        List<NewsArticleCategoryDTO> newsArticleCategoryDTOs = NewsArticleCategoryDTO.infoDTO(newsCategory);
        response.setContent(newsArticleCategoryDTOs);
        return response;
    }

    /**
     * 资讯文章分类详情
     * @return
     */
    @GetMapping("/categoryInfo")
    public JsonResponseEntity categoryInfo(@RequestParam int id){
        JsonResponseEntity response=new JsonResponseEntity();
        NewsArticleCategory newsCategory = manageNewsArticleCategotyService.findNewsCategory(id);
        NewsArticleCategoryDTO dtos=new NewsArticleCategoryDTO(newsCategory);
        response.setData(dtos);
        return response;
    }

    /**
     * 添加/编辑资讯文章
     * @param article
     */
    @PostMapping("/save")
    public void updateArticle(@RequestBody NewsArticle article){
        manageNewsArticleServiceImpl.updateNewsAritile(article);
    }

    /**
     * 资讯文章详情
     * @return
     */
    @GetMapping("/info")
    public JsonResponseEntity articleInfo(@RequestParam(required = true) Integer id){
        JsonResponseEntity response=new JsonResponseEntity();
        NewsArticle articleInfo = manageNewsArticleServiceImpl.findArticleInfoById(id);
        response.setData(articleInfo);
        return response;
    }

    /**
     * 资讯文章列表
     * @return
     */
    @GetMapping("/list")
    public JsonListResponseEntity articleList(){
        JsonListResponseEntity response=new JsonListResponseEntity();


        return response;
    }

    @GetMapping("/putArticle")
    public void putArticle(@RequestBody ArticleArea articleArea){
        articleAreaRepository.saveAndFlush(articleArea);
    }
}
