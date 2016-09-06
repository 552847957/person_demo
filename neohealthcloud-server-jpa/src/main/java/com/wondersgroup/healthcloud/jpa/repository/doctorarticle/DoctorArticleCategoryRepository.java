package com.wondersgroup.healthcloud.jpa.repository.doctorarticle;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by shenbin on 16/8/30.
 */
public interface DoctorArticleCategoryRepository extends JpaRepository<DoctorArticleCategory, Integer> {

    DoctorArticleCategory findById(int id);

    @Transactional
    @Modifying
    @Query(" update DoctorArticleCategory a set a.isVisable = ?2 where a.id = ?1")
    int updateCategoryVisable(int id, int isVisable);
}
