package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
public interface DiabetesAssessmentRepository extends JpaRepository<DiabetesAssessment, String>, JpaSpecificationExecutor<DiabetesAssessment> {

    @Query(value = "select  registerid from DiabetesAssessment where id in ?1")
    List<String> findRegisterById(String[] ids);

    @Modifying
    @Transactional
    @Query(value = "update DiabetesAssessment set hasRemind = 1 , updateDate = ?2 where type = 1 and registerid in ?1")
    void updateRemindByRegister(List ids, Date date);

}
