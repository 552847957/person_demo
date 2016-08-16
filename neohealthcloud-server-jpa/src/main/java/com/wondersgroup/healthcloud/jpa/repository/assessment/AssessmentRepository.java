package com.wondersgroup.healthcloud.jpa.repository.assessment;

import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by zhuchunliu on 2015/12/29.
 */
public interface AssessmentRepository extends JpaRepository<Assessment,String> {

    @Query("select count(1) from Assessment a where a.delFlag=0")
    Long countAssessNum();

    @Query("select a from Assessment a where a.uid=?1 and a.createDate<?2 and a.isOneself=1 and a.delFlag=0")
    Page<Assessment> getAssessmentHistory(String uid, Date flag, Pageable pageable);


}
