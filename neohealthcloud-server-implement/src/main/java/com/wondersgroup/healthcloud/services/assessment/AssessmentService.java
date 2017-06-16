package com.wondersgroup.healthcloud.services.assessment;



import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取用户最近一次的风险评估结果
     * @param uid
     * @return
     */
    Assessment getRecentAssess(String uid);

    /**
     * 获取用户最近一次的风险评估结果
     * @param uid
     * @return  {state:true,date:yyyy-MM-dd HH:mm:ss}  【state:1:健康人群, state:2:风险人群，state:3:疾病人群】
     */
    Map<String,Object> getRecentAssessIsNormal(String uid);

    /**
     * 根据用户id获取评估的数量
     * @param uid
     * @return
     */
    Integer getAssessNum(String uid);





}
