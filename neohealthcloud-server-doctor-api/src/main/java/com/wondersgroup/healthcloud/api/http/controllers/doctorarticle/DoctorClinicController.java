package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.wondersgroup.healthcloud.api.http.dto.doctor.common.H5CollectShareAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.common.ShareH5APIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle.DoctorArticleListAPIEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import com.wondersgroup.healthcloud.services.doctor.ManageDoctorArticleService;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by shenbin on 16/9/1.
 *
 * 学苑--收藏
 */
@RestController
@RequestMapping(value="/api/doctorClinic")
public class DoctorClinicController {

    @Autowired
    private MedicalCircleService mcService;

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    @Autowired
    private ManageDoctorArticleService manageDoctorArticleService;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;


    /**
     * 学苑文章列表
     * @param uid
     * @return
     */
    @RequestMapping(value="/articleList", method = RequestMethod.GET)
    public JsonListResponseEntity<DoctorArticleListAPIEntity> articleList(@RequestParam(required = true) Integer cat_id,
                                                                          @RequestParam(required = false) String uid,
                                                                          @RequestParam(required = false, defaultValue = "1") String flag,
                                                                          @RequestParam(required = false) String order){
        if (null == flag || "".equals(flag)){
            flag = "1";
        }
        int page = Integer.valueOf(flag);
        int pageSize = 10;

        List<DoctorArticleListAPIEntity> cateArticleListAPIEntities = this.getArticleEntityList(cat_id, page, pageSize+1);
        JsonListResponseEntity<DoctorArticleListAPIEntity> rt = new JsonListResponseEntity<>();
        Boolean hasMore = false;
        if (null != cateArticleListAPIEntities && !cateArticleListAPIEntities.isEmpty() && cateArticleListAPIEntities.size() > pageSize){
            cateArticleListAPIEntities = cateArticleListAPIEntities.subList(0, pageSize);
            hasMore = true;
        }
        if (hasMore){
            flag = String.valueOf(page+1);
        }
        rt.setContent(cateArticleListAPIEntities, hasMore, null, flag);
        return rt;
    }

    /**
     * 获取分类下面的文章
     * @param cat_id 学苑文章的分类id
     * @return List
     */
    private List<DoctorArticleListAPIEntity> getArticleEntityList(int cat_id, int page, int pageSize){
        //获取文章分类下面的文章
        List<DoctorArticle> catArticleList = manageDoctorArticleService.findAppShowListByCategoryId(cat_id, pageSize, page );

        if(null == catArticleList || catArticleList.size() == 0){
            return null;
        }
        List<DoctorArticleListAPIEntity> articleList = new ArrayList<>();
        for (DoctorArticle articleModel : catArticleList){
            DoctorArticleListAPIEntity articleEntity = new DoctorArticleListAPIEntity();
            articleEntity.setId(String.valueOf(articleModel.getId()));
            articleEntity.setTitle(articleModel.getTitle());
            articleEntity.setDesc(articleModel.getBrief());
            int pv = articleModel.getPv() + articleModel.getFakePv();
            articleEntity.setPv(String.valueOf(pv));
            articleEntity.setThumb(articleModel.getThumb());
            articleEntity.setUrl(appUrlH5Utils.buildXueYuanArticleView(articleModel.getId()));
            articleList.add(articleEntity);
        }
        return articleList;
    }


    /**
     * 收藏
     */
    @VersionRange
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public JsonResponseEntity<String> collect(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String article_id = reader.readString("article_id", false);
        Boolean collect = mcService.collect(article_id, doctor_id, 2);
        if (collect) {
            result.setMsg("收藏成功");
        } else {
            result.setCode(1320);
            result.setMsg("已收藏过");
        }
        return result;
    }

    /**
     * 取消收藏
     */
    @VersionRange
    @RequestMapping(value = "/collect/del", method = RequestMethod.POST)
    public JsonResponseEntity<String> collectDel(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String article_id = reader.readString("article_id", false);
        Boolean success = mcService.delCollect(article_id, doctor_id, 2);
        if (success) {
            result.setCode(0);
            result.setMsg("取消收藏成功");
        } else {
            result.setCode(1321);
            result.setMsg("取消收藏失败");
        }
        return result;
    }

    /**
     * 检查是否收藏过
     */
    @VersionRange
    @RequestMapping(value = "/collect/check",method = RequestMethod.GET)
    public JsonResponseEntity<H5CollectShareAPIEntity> collectCheck(@RequestParam int article_id,
                                                                    @RequestParam(required = false) String from,
                                                                    @RequestParam(required=false, defaultValue="") String uid){

        JsonResponseEntity<H5CollectShareAPIEntity> result = new JsonResponseEntity<>();
        Boolean isCollect = mcService.checkCollect(String.valueOf(article_id), uid, 2);
        ShareH5APIEntity h5APIEntity = new ShareH5APIEntity();

        //文章
        DoctorArticle article = this.doctorArticleRepository.findById(article_id);
        if (null == article || article.getIsVisable() != 1){
            //文章被删除
        }else {
            h5APIEntity.setDesc(article.getBrief());
            String thumb = "";
            if (null != article.getThumb() && !"".equals(article.getThumb())){
                thumb = article.getThumb() + "?imageView2/1/w/200/h/200";
            }
            h5APIEntity.setThumb(thumb);
            h5APIEntity.setTitle(article.getTitle());
            h5APIEntity.setUrl(appUrlH5Utils.buildXueYuanArticleView(article.getId()));
        }
        if (null == h5APIEntity.getTitle()){
            h5APIEntity = null;
        }
        H5CollectShareAPIEntity h5CollectShareAPIEntity = new H5CollectShareAPIEntity();
        h5CollectShareAPIEntity.setIs_collect(isCollect);
        h5CollectShareAPIEntity.setCan_collect(true);
        h5CollectShareAPIEntity.setShare(h5APIEntity);
        result.setData(h5CollectShareAPIEntity);
        return result;
    }
}
