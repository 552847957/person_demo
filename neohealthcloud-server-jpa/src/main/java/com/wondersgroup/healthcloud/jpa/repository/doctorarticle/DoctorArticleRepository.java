package com.wondersgroup.healthcloud.jpa.repository.doctorarticle;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by shenbin on 16/8/30.
 */
public interface DoctorArticleRepository extends JpaRepository<DoctorArticle, Integer> {

    List<DoctorArticle> findByCategoryIdsContaining(String categoryId);

    DoctorArticle findById(int id);
}
