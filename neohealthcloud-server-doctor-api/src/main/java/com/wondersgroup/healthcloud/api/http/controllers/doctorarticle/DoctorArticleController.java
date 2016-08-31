package com.wondersgroup.healthcloud.api.http.controllers.doctorarticle;

import com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle.DoctorArticleCategoryDto;
import com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle.DoctorArticleDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticleCategory;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctorarticle.DoctorArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

    /**
     * 级联查询学苑分类及其文章
     * @return
     */
    @VersionRange
    @RequestMapping(value = "doctorArticleAndCategory",method = RequestMethod.GET)
    public JsonResponseEntity findDoctorArticleAndCategory() {
        List<DoctorArticleCategoryDto> doctorArticleCategoryDtos = new ArrayList<>();
        List<DoctorArticleCategory> doctorArticleCategories = doctorArticleCategoryRepository.findAll();
        for (DoctorArticleCategory doctorArticleCategory : doctorArticleCategories) {
            DoctorArticleCategoryDto doctorArticleCategoryDto = new DoctorArticleCategoryDto();
            doctorArticleCategoryDto.setCategoryId(doctorArticleCategory.getId());
            doctorArticleCategoryDto.setCategoryName(doctorArticleCategory.getCaName());
            List<DoctorArticleDto> doctorArticleDtos = new ArrayList<>();
            List<DoctorArticle> doctorArticles =
                    doctorArticleRepository.findByCategoryIdsContaining(doctorArticleCategory.getId().toString());
            for (DoctorArticle doctorArticle : doctorArticles) {
                DoctorArticleDto doctorArticleDto = new DoctorArticleDto().toNewDoctorArticleDto(doctorArticle);
                doctorArticleDto.setUrl(appUrlH5Utils.buildXueYuanArticleView(doctorArticle.getId()));
                doctorArticleDtos.add(doctorArticleDto);
            }
            doctorArticleCategoryDto.setDoctorArticleDtos(doctorArticleDtos);
            doctorArticleCategoryDtos.add(doctorArticleCategoryDto);
        }
        if (doctorArticleCategoryDtos != null && !doctorArticleCategoryDtos.isEmpty()) {
            return new JsonResponseEntity(0, "查询成功", doctorArticleCategoryDtos);
        }
        return new JsonResponseEntity(-1, "查询失败");
    }

}
