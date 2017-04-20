package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
public interface DiabetesAssessmentRepository extends JpaRepository<DiabetesAssessment, String>, JpaSpecificationExecutor<DiabetesAssessment> {

    @Query(nativeQuery = true,value = "select registerid from app_tb_diabetes_assessment where id in ?1 " +
            " and registerid not in (select registerid from app_tb_diabetes_assessment_remind where create_date >= curdate())")
    List<String> findRemidRegisterById(String[] ids);

    @Query("select count(1) from DiabetesAssessment a where a.registerid = ?1 and a.type = ?2 and a.delFlag = '0'")
    Integer getNumByTypeAndRegisterid(String registerid, Integer type);


    @Query("select a from DiabetesAssessment a where a.registerid = ?1 and a.type = ?2 and a.delFlag = '0'")
    Page getAssessmentList(String registerid, Integer type, Pageable pageable);
}
