package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
public interface DiabetesAssessmentRepository extends JpaRepository<DiabetesAssessment, String>, JpaSpecificationExecutor<DiabetesAssessment> {
}
