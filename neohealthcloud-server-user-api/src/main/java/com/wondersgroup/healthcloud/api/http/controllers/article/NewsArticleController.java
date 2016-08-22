package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.api.http.dto.article.NewsCateArticleListAPIEntity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticle;
import com.wondersgroup.healthcloud.jpa.entity.article.NewsArticleCategory;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleCategotyService;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

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
            @RequestHeader("main-area") String area,
            @RequestParam(required = true) String cat_id,
            @RequestParam(required = false, defaultValue = "1") String flag){

        int pageNo = Integer.valueOf(flag);
        int pageSize = 10;
        List<NewsArticle> resourceList = this.manageNewsArticleServiceImpl.findAppShowListByCategoryId(cat_id, (pageNo-1) * pageSize, pageSize+1);//获取文章分类下面的文章
        List<NewsArticleListAPIEntity> list = this.getArticleEntityList(resourceList);//获取文章分类下面的文章
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
     *热词搜索
     * @return
     */
    @RequestMapping(value="/getHotWords", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<List<NewsCateArticleListAPIEntity>> getHotSearch(){
        Map<String, Object> map = new HashMap<>();//获取分类
        map.put("is_visable", 1);
        List<NewsArticleCategory> resourList = this.manageNewsArticleCategotyService.findNewsCategoryByKeys(map);
        List<NewsCateArticleListAPIEntity> data=new ArrayList<>();
        for (NewsArticleCategory category : resourList) {//遍历文章分类,获取分类下面的文章
            NewsCateArticleListAPIEntity cateEntity = new NewsCateArticleListAPIEntity(category);
            data.add(cateEntity);
        }
        JsonResponseEntity<List<NewsCateArticleListAPIEntity>> response = new JsonResponseEntity<>();
        response.setData(data);
        return response;
    }
    /**
     *文章搜索
     */

    @RequestMapping(value="/searchArticle", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<NewsArticleListAPIEntity> searchArticle(@RequestParam(required = false) String cat_id
            ,@RequestParam(required = false) String word,
            @RequestParam(required = false, defaultValue = "0") String flag){
        int pageNo = Integer.valueOf(flag);
        int pageSize = 10;

        JsonListResponseEntity<NewsArticleListAPIEntity> response=new JsonListResponseEntity<>();
        List<NewsCateArticleListAPIEntity> list = new ArrayList<>();
        Boolean hasMore = false;
        List<NewsArticleListAPIEntity> articleList=null;
        if(null != cat_id&&!"".equals(cat_id)){

            List<NewsArticle> resourceList = this.manageNewsArticleServiceImpl.findAppShowListByCategoryId(cat_id, pageNo, pageSize+1);//获取文章分类下面的文章
            articleList = this.getArticleEntityList(resourceList);

            if (null != articleList && articleList.size() > 10){
                articleList = articleList.subList(0, 10);
                hasMore = true;
            }


        }else{
            List<NewsArticle> resourceList = this.manageNewsArticleServiceImpl.findAppShowListByTitle(word,pageNo, pageSize+1);
            articleList =  this.getArticleEntityList(resourceList);
            if (null != articleList && articleList.size() > 10){
                articleList = articleList.subList(0, 10);
                hasMore = true;
            }
        }

        if(hasMore){
            response.setContent(articleList, true, null, String.valueOf(pageNo + 1));
        }else{
            response.setContent(articleList, false, null, null);
        }
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
            List<NewsArticle> resourceList = this.manageNewsArticleServiceImpl.findAppShowListByCategoryId(cateEntity.getCat_id(), 0, 11);//获取文章分类下面的文章
            List<NewsArticleListAPIEntity> articleList = this.getArticleEntityList(resourceList);//获取文章分类下面的文章
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
     * @param resourceList 学苑文章的分类id
     * @return List
     */
    private List<NewsArticleListAPIEntity> getArticleEntityList(List<NewsArticle> resourceList){

        if(null == resourceList || resourceList.size() == 0){
            return null;
        }
        List<NewsArticleListAPIEntity> list = new ArrayList<>();
        for (NewsArticle article : resourceList){
            list.add(new NewsArticleListAPIEntity(article,appUrlH5Utils));
        }
        return list;
    }
}
