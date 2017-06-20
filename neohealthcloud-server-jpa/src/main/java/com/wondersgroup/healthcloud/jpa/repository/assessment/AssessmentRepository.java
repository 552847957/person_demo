package com.wondersgroup.healthcloud.jpa.repository.assessment;

import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2015/12/29.
 */
public interface AssessmentRepository extends JpaRepository<Assessment,String> {

    @Query("select count(1) from Assessment a where a.delFlag=0")
    Long countAssessNum();

    @Query("select a from Assessment a where a.uid=?1 and a.createDate<?2 and a.isOneself=1 and a.delFlag=0")
    Page<Assessment> getAssessmentHistory(String uid, Date flag, Pageable pageable);

    @Query(nativeQuery = true ,value = "select * from app_tb_patient_assessment  where uid=?1  and is_oneself=1 and del_flag=0" +
            " order by create_date desc limit 1")
    Assessment getRecentAssess(String uid);

    @Query("select count(1) from Assessment a where a.uid = ?1 and a.isOneself = 1 and a.delFlag=0")
    Integer getAssessNum(String uid);
    
    @Query(nativeQuery = true, value = "select * from app_tb_patient_assessment a where a.uid = ?1 and a.is_oneself = 1 and a.del_flag=0 and a.create_date >= ?2 GROUP BY DATE_FORMAT(create_date,'%Y-%m-%d') ORDER BY create_date desc")
    List<Assessment> queryAssessment(String registerId, String date);

    @Query(nativeQuery = true,value = "select * from app_tb_patient_assessment where del_flag = '0' " +
            " and create_date >= DATE_ADD(NOW(),INTERVAL -3 MONTH) and result is null")
    List<Assessment> findUnSyncAssessment();
}
