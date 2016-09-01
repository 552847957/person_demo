package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.wondersgroup.healthcloud.api.http.dto.doctor.common.H5CollectShareAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.doctor.common.ShareH5APIEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import com.wondersgroup.healthcloud.services.medicalcircle.MedicalCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by shenbin on 16/9/1.
 */
@RestController
@RequestMapping(value="/api/doctorClinic")
public class DoctorClinicController {

    @Autowired
    private MedicalCircleService mcService;

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

    /**
     * 收藏
     */
    @VersionRange
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public JsonResponseEntity<String> collect(@RequestBody String body) {

        JsonResponseEntity<String> result = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctor_id = reader.readString("doctor_id", false);
        String circle_id = reader.readString("circle_id", false);
        Boolean collect = mcService.collect(circle_id, doctor_id, 2);
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
        String circle_id = reader.readString("circle_id", false);
        Boolean success = mcService.delCollect(circle_id, doctor_id, 2);
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
