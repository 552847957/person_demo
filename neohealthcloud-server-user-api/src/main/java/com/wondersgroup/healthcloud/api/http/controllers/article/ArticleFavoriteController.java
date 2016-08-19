package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.article.ArticleFavorite;

import com.wondersgroup.healthcloud.services.article.ManageArticleFavoriteService;
import com.wondersgroup.healthcloud.services.article.ManageArticleService;

import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
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
	private ManageArticleService manageArticleService;

    @Resource
    private ManageNewsArticleService manageNewsArticleServiceImpl;

	@RequestMapping("/list")
    @VersionRange
	public JsonListResponseEntity<ArticleFavorite> articleFavoriteList(@RequestParam(required=false) String uid,
                                                                       @RequestParam(required=false) Integer flag){

		JsonListResponseEntity<ArticleFavorite> body = new JsonListResponseEntity<>();
		try{
			Integer position = 0;
			if(flag!=null){
				position = flag;
			}
            List<ArticleFavorite> favoriteList = manageArticleFavoriteService.queryAllArticleFavListByUserId(uid);
            if(favoriteList!=null){
                body = new Page<ArticleFavorite>().handleResponseEntity(body, position, (position+PAGE_SIZE),
                        PAGE_SIZE, favoriteList);
            }else{
                body.setContent((List) Collections.emptyList());
            }
		}catch(Exception e){
			e.printStackTrace();
			log.error("get favorite list occured error " + e.getMessage());
			body.setCode(1001);
			body.setMsg("调用失败");
			body.setContent((List) Collections.emptyList());
		}
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

    @RequestMapping("/checkIsFavor")
    @VersionRange
    public JsonResponseEntity<Map> checkIsFavor(@RequestParam(required = true) int id,
                                                @RequestParam(required=false, defaultValue="") String for_type,
                                                @RequestParam(required=false, defaultValue="") String from,
												@RequestParam(required=false, defaultValue="") String uid){
        JsonResponseEntity<Map> body = new JsonResponseEntity<>();


        return body;
    }

	private static class Page<ArticleFavorite>{
		public List<ArticleFavorite> pageObject(List<ArticleFavorite> origin, int fromIndex, int toIndex){
			if(origin==null){
				return null;
			}
			if(toIndex>=origin.size())
				toIndex = origin.size();

			return new ArrayList<>(origin.subList(fromIndex, toIndex));
		}

		public JsonListResponseEntity<ArticleFavorite> handleResponseEntity(JsonListResponseEntity<ArticleFavorite> body,
				int fromIndex, int toIndex, int pageSize, List<ArticleFavorite> favorList){
			if(body==null)
				return null;
			List<ArticleFavorite> temp = pageObject(favorList,fromIndex,toIndex);
			if(favorList.size()>pageSize&&fromIndex<favorList.size()&&temp.size()==PAGE_SIZE){
				body.setContent(temp, true, "updateTime", String.valueOf(fromIndex+temp.size()));
			}else{
				body.setContent(temp, false, "updateTime", String.valueOf(favorList.size()));
			}
			return body;
		}
	}
}
