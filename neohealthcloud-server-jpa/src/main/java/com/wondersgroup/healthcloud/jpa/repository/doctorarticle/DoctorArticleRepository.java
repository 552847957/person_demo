package com.wondersgroup.healthcloud.jpa.repository.doctorarticle;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by shenbin on 16/8/30.
 */
public interface DoctorArticleRepository extends JpaRepository<DoctorArticle, Integer> {

    @Query("from DoctorArticle where categoryIds like ?1")
    Page<DoctorArticle> findByCategoryId(int categoryId, Pageable pageable);
}
