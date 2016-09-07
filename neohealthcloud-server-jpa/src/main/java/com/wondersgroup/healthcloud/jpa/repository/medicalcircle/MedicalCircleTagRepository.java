package com.wondersgroup.healthcloud.jpa.repository.medicalcircle;

import com.wondersgroup.healthcloud.jpa.entity.medicalcircle.MedicalCircleTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by longshasha on 16/9/6.
 */
public interface MedicalCircleTagRepository extends JpaRepository<MedicalCircleTag,String> {


    @Query(" select count(a) from MedicalCircleTag a where a.delFlag = '0' ")
    int countTag();

    @Query(" select a from MedicalCircleTag a where a.delFlag = '0' ")
    List<MedicalCircleTag> findTagListByPager(Pageable pageable);

    @Query(" select a from MedicalCircleTag a where a.delFlag = '0' ")
    List<MedicalCircleTag> findTagList();

    @Transactional
    @Modifying
    @Query(value = "update MedicalCircleTag set delFlag = '1' where id in ?1")
    void deleteByIds(List<String> ids);
}
