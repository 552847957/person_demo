package com.wondersgroup.healthcloud.jpa.repository.diabetes;

import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2016/12/14.
 */
public interface DiabetesAssessmentRemindRepository extends JpaRepository<DiabetesAssessmentRemind, String>, JpaSpecificationExecutor<DiabetesAssessmentRemind> {
}
