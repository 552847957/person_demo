package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.api.http.dto.article.NewsArticleCategoryDTO;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleArea;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.article.ArticleAreaRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *  添加/编辑资讯分类
     * @param articleCategory
     */
    @PostMapping("/categorySave")
    public JsonResponseEntity updateArticleCategory(@RequestBody NewsArticleCategory articleCategory){
        JsonResponseEntity response=new JsonResponseEntity();
        manageNewsArticleCategotyService.updateNewsArticleCategory(articleCategory);
        response.setMsg("成功");
        return response;
    }

    /**
     * 资讯分类列表
     * @return
     */
    @GetMapping("/categoryList")
    public JsonResponseEntity categoryList(@RequestParam String area){
        JsonResponseEntity response=new JsonResponseEntity();
        List<NewsArticleCategory> newsCategory = manageNewsArticleCategotyService.findNewsCategoryByArea(area);
        List<NewsArticleCategoryDTO> newsArticleCategoryDTOs = NewsArticleCategoryDTO.infoDTO(newsCategory);
        response.setData(newsArticleCategoryDTOs);
        return response;
    }
    /**
     * 资讯分类详情
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
     * 添加/编辑资讯
     * @param article
     */
    @PostMapping("/save")
    public JsonResponseEntity updateArticle(@RequestBody NewsArticle article){
        JsonResponseEntity response=new JsonResponseEntity();
        if(article.getTitle().length()>30){
            response.setCode(-1);
            response.setMsg("字数过多，限制30个字");
            return response;
        }
        if(article.getBrief().length()>30){
            response.setCode(-1);
            response.setMsg("字数过多，限制30个字");
            return response;
        }
        manageNewsArticleServiceImpl.updateNewsAritile(article);
        response.setMsg("成功");
        return response;
    }

    /**
     * 区域引入资讯
     * @param articleArea
     */
    @PostMapping("/areaArticleUpdate")
    public JsonResponseEntity updateArticle(@RequestBody ArticleArea articleArea){
        JsonResponseEntity response=new JsonResponseEntity();
        Date date=new Date();
        articleArea.setUpdate_time(date);
        articleAreaRepository.saveAndFlush(articleArea);
        response.setMsg("成功");
        return response;
    }

    /**
     * 资讯详情
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
     * 资讯列表
     * @return
     */
    @PostMapping("/list")
    public Pager articleList(@RequestBody Pager pager){
        Map param = new HashMap();
        param.putAll(pager.getParameter());
        int pageSize = pager.getSize();

        param.put("pageSize", pager.getSize());

        param.put("pageNo",pager.getNumber());
        List list = manageNewsArticleServiceImpl.queryArticleList(param);
        pager.setData(list);
        int total=manageNewsArticleServiceImpl.getCount(param);
        int totalPage=total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
        pager.setTotalElements(total);
        pager.setTotalPages(totalPage);
        return pager;
    }

}
