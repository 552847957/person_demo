package com.wondersgroup.healthcloud.services.assessment;



import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;

import java.util.Date;
import java.util.List;

/**
 * Created by Yoda on 2015/12/29.
 */
public interface AssessmentService {


    String getLastAssessTime(String uid);


    /**
     * 评估
     * @param assessment
     */
    String assess(Assessment assessment);

    String getResult(Assessment assessment);

    Long assessNum();

    Boolean isHypertension(Integer standards, String pressure);

    Boolean overWeight(Integer height, Float weight);

    Boolean isFat(String gender, Float waist);


    Boolean needMovement(Integer sport);

    Boolean needAmendLife(Assessment assessment);

    Boolean hasFamilyHistory(Assessment assesment);

    String familyHistory(Assessment assesment);

    List<Assessment> getAssessHistory(String uid, Date flag);

    Assessment getAssessment(String id);

    /**
     * 用户是否有慢性疾病
     * @param uid 用户主键
     * @return 只要用户用糖尿病、高血压、脑卒中中一种即为true，否则为false
     */
    Boolean hasDiseases(String uid);
}
