package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;

import java.util.List;

/**
 * Created by longshasha on 16/9/1.
 */
public interface ManageDoctorArticleService {
    List<DoctorArticle> findAppShowListByCategoryId(int cat_id, int pageSize, int page);
}
