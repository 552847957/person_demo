package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleFavorite;

import com.wondersgroup.healthcloud.services.article.ManageArticleFavoriteService;
import com.wondersgroup.healthcloud.services.article.ManageArticleService;

import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.*;

/**
 * 1. 添加收藏
 * 2. 获取收藏列表
 *
 */
@RestController
@RequestMapping("/api/articleFavorite")
public class ArticleFavoriteController {

	private static final Logger log = Logger.getLogger(ArticleFavoriteController.class);
	private static final Integer PAGE_SIZE = 20;//每页个数20

	@Resource
	private ManageArticleFavoriteService manageArticleFavoriteService;


    @Resource
    private ManageNewsArticleService manageNewsArticleServiceImpl;

	@RequestMapping(value = "/list",method = RequestMethod.GET)
    @VersionRange
	public JsonListResponseEntity<NewsArticleListAPIEntity> articleFavoriteList(@RequestParam(required=false) String uid,
                                                                       @RequestParam(required=false, defaultValue = "0") String flag){

		JsonListResponseEntity<NewsArticleListAPIEntity> body = new JsonListResponseEntity<>();
		int pageSize=10;
		int pageNo=Integer.valueOf(flag);
		List<NewsArticleListAPIEntity> collectionArticle = manageNewsArticleServiceImpl.findCollectionArticle(uid, pageNo, pageSize + 1);
		Boolean hasMore = false;
		if (null != collectionArticle  && collectionArticle.size() > pageSize){
			collectionArticle = collectionArticle.subList(0, pageSize);
			hasMore = true;
		}else{
			flag = null;
		}
		if (hasMore){
			flag = String.valueOf(pageNo+1);
		}
		body.setContent(collectionArticle,hasMore, null, flag);
		return body;
	}
    @VersionRange
	@RequestMapping(value = "/addDel",method = RequestMethod.POST)
	public JsonResponseEntity<String> addArticleFavorite(@RequestBody String request){

		JsonResponseEntity<String> body = new JsonResponseEntity<>();
		JsonKeyReader reader = new JsonKeyReader(request);
		Integer article_id = reader.readInteger("id", false);
		String uid = reader.readString("uid", false);

        ArticleFavorite articleFavorite = manageArticleFavoriteService.queryByUidAndArticleId(uid, article_id);
        if(null != articleFavorite){
            manageArticleFavoriteService.deleteArticleFavorite(articleFavorite);
            body.setMsg("删除收藏成功");
        }else{
            ArticleFavorite af=new ArticleFavorite();
            af.setUser_id(uid);
            af.setArticle_id(article_id);
			af.setUpdate_time(new Date());
            manageArticleFavoriteService.addFavorite(af);
            body.setMsg("添加收藏成功");
        }

		return body;
	}

    @RequestMapping(value = "/checkIsFavor",method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Boolean> checkIsFavor(@RequestParam(required = true) int id,@RequestParam(required=true) String uid){
        JsonResponseEntity<Boolean> body = new JsonResponseEntity<>();

		ArticleFavorite articleFavorite = manageArticleFavoriteService.queryByUidAndArticleId(uid, id);

		boolean has=false;

		if(null != articleFavorite){
			has=true;
		}
		body.setData(has);
        return body;
    }

}
