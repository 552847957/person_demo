package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by shenbin on 16/8/30.
 */
@RestController
@RequestMapping(value="api")
public class DoctorArticleController {

    @Autowired
    private DoctorArticleRepository doctorArticleRepository;

    @Autowired
    private DoctorArticleCategoryRepository doctorArticleCategoryRepository;

    /**
     * 查询学苑分类
     * @return
     */
    @VersionRange
    @RequestMapping(value = "doctorArticleCategory/find", method = RequestMethod.GET)
    public JsonResponseEntity findDoctorArticleCategory(){
        List<DoctorArticleCategory> doctorArticleCategories = doctorArticleCategoryRepository.findAll();
        if (doctorArticleCategories != null && !doctorArticleCategories.isEmpty()) {
            return new JsonResponseEntity(0, "查询成功", doctorArticleCategories);
        } else {
            return new JsonResponseEntity(-1, "查询失败");
        }
    }

    /**
     * 查询学苑分类文章
     * @param categoryId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "doctorArticle/find", method = RequestMethod.GET)
    public JsonResponseEntity findDoctorArticle(@RequestParam String categoryId){
        List<DoctorArticle> doctorArticles = doctorArticleRepository.findByCategoryIdsContaining(categoryId);
        if (doctorArticles != null && !doctorArticles.isEmpty()) {
            return new JsonResponseEntity(0, "查询成功", doctorArticles);
        } else {
            return new JsonResponseEntity(-1, "查询失败");
        }
    }

}
