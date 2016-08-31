package com.wondersgroup.healthcloud.api.http.dto.doctor.doctorarticle;

import java.util.List;

/**
 * Created by shenbin on 16/8/31.
 */
public class DoctorArticleCategoryDto {

    private int categoryId;

    private String categoryName;

    private List<DoctorArticleDto> doctorArticleDtos;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<DoctorArticleDto> getDoctorArticleDtos() {
        return doctorArticleDtos;
    }

    public void setDoctorArticleDtos(List<DoctorArticleDto> doctorArticleDtos) {
        this.doctorArticleDtos = doctorArticleDtos;
    }
}
