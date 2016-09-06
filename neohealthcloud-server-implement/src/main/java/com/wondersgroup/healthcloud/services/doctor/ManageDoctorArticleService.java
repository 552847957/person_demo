package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/9/1.
 */
public interface ManageDoctorArticleService {
    List<DoctorArticle> findAppShowListByCategoryId(int cat_id, int pageSize, int page);

    List<DoctorArticle> findArticleListByIds(List<Integer> ids);

    List<Map<String,Object>> findDoctorArticleListByPager(int pageNum, int size, Map<String, Object> parameter);

    int countDoctorArticleByParameter(Map<String, Object> parameter);
}
