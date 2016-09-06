package com.wondersgroup.healthcloud.jpa.repository.doctorarticle;

import com.wondersgroup.healthcloud.jpa.entity.doctorarticle.DoctorArticle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shenbin on 16/8/30.
 */
public interface DoctorArticleRepository extends JpaRepository<DoctorArticle, Integer> {

    List<DoctorArticle> findByCategoryIdsContaining(String categoryId);

    DoctorArticle findById(int id);


    @Query(" select a from DoctorArticle a where a.categoryIds like %?1% and a.isVisable = 1  ")
    List<DoctorArticle> findListByCategoryId(int cat_id, Pageable pageable);

    @Query(" select a from DoctorArticle a where a.id  in  ?1")
    List<DoctorArticle> findArticleListByIds(List<Integer> ids);

    @Transactional
    @Modifying
    @Query(" update DoctorArticle a set a.isVisable = ?2 where a.id = ?1")
    int updateDoctorArticleVisable(int id, int isVisable);
}
