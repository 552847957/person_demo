package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.api.http.dto.article.NewsArticleListAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.article.NewsCateArticleListAPIEntity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/31.
 */
@RestController
@RequestMapping("/api/article")
public class NewsArticleController {

    @Resource
    private ManageNewsArticleCategotyService manageNewsArticleCategotyService;

    @Resource
    private ManageNewsArticleService manageNewsArticleServiceImpl;

    private final int showCatNum = 4;
    /**
     * 资讯列表
     * @return
     */
    @WithoutToken
    @RequestMapping(value="/articleCategoty", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<List<NewsCateArticleListAPIEntity>> getArticleCategoty(){
        JsonResponseEntity<List<NewsCateArticleListAPIEntity>> response = new JsonResponseEntity<>();
        response.setData(this.getCatArticleEntityList());
        return response;
    }

    /**
     * 资讯文章列表
     * @return
     */
    @RequestMapping(value="/articleList", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<NewsArticleListAPIEntity> articleList(
            @RequestParam(required = true) String cat_id,
            @RequestParam(required = false, defaultValue = "1") String flag,
            @RequestParam(required = false) String order){

        int pageNo = Integer.valueOf(flag);
        int pageSize = 10;

        List<NewsArticleListAPIEntity> list = this.getArticleEntityList(cat_id, (pageNo-1) * pageSize, pageSize+1);
        Boolean hasMore = false;
        if (null != list  && list.size() > pageSize){
            list = list.subList(0, pageSize);
            hasMore = true;
        }else{
            flag = null;
        }
        if (hasMore){
            flag = String.valueOf(pageNo+1);
        }

        JsonListResponseEntity<NewsArticleListAPIEntity> response = new JsonListResponseEntity<>();
        response.setContent(list, hasMore, null, flag);
        return response;
    }

    /**
     * 获取医生下面的分类文章
     */
    private List<NewsCateArticleListAPIEntity> getCatArticleEntityList(){

        Map<String, Object> map = new HashMap<>();//获取分类
        map.put("is_visable", 1);
        List<NewsArticleCategory> resourList = this.manageNewsArticleCategotyService.findNewsCategoryByKeys(map);

        if (null == resourList || resourList.isEmpty()){
            return null;
        }
        if (resourList.size()>this.showCatNum){
            resourList = resourList.subList(0, this.showCatNum);
        }


        List<NewsCateArticleListAPIEntity> list = new ArrayList<>();
        for (NewsArticleCategory category : resourList) {//遍历文章分类,获取分类下面的文章
            NewsCateArticleListAPIEntity cateEntity = new NewsCateArticleListAPIEntity(category);

            List<NewsArticleListAPIEntity> articleList = this.getArticleEntityList(cateEntity.getCat_id(), 0, 11);//获取文章分类下面的文章
                Boolean hasMore = false;
                if (null != articleList && articleList.size() > 10){
                articleList = articleList.subList(0, 10);
                hasMore = true;
            }
            cateEntity.setMore(hasMore);
            if (hasMore){
                String flag = String.valueOf(2);
                cateEntity.setMore_params(null, flag);
            }
            cateEntity.setList(articleList);
            list.add(cateEntity);
        }
        return list;
    }

    /**
     * 获取分类下面的文章
     * @param cat_id 学苑文章的分类id
     * @return List
     */
    private List<NewsArticleListAPIEntity> getArticleEntityList(String cat_id, int startSize, int endSize){
        List<NewsArticle> resourceList = this.manageNewsArticleServiceImpl.findAppShowListByCategoryId(cat_id, startSize, endSize);//获取文章分类下面的文章
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
