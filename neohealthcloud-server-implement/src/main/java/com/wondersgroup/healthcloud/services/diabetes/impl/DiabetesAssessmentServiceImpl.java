package com.wondersgroup.healthcloud.services.diabetes.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRepository;
import com.wondersgroup.healthcloud.services.assessment.dto.AssessmentConstrains;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/12/6.
 */
@Service("diabetesAssessmentService")
public class DiabetesAssessmentServiceImpl implements DiabetesAssessmentService{
    @Autowired
    private DiabetesAssessmentRepository assessmentRepo;

    /**
     * 患病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer sicken(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setType(1);
        assessment.setResult(this.sickenAssement(assessment));
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 肾病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer kidney(DiabetesAssessment assessment) {
        int total = assessment.getIsHistory() + assessment.getIsEyeHistory() + assessment.getIsPressureHistory()
                + assessment.getIsUrine() + assessment.getIsEdema() + assessment.getIsTired() + assessment.getIsCramp();
        switch (total){
            case 0 :
                assessment.setResult(0);
                break;
            case 1:
                assessment.setResult(1);
                break;
            case 2:
                assessment.setResult(1);
                break;
            default:
                assessment.setResult(2);
        }
        assessment.setId(IdGen.uuid());
        assessment.setType(2);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 眼病风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer eye(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setType(3);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        if(1 == assessment.getIsEyeSight() || 1 == assessment.getIsEyeFuzzy() || 1 == assessment.getIsEyeShadow()
                || 1 == assessment.getIsEyeGhosting() || 1 == assessment.getIsEyeFlash()){
            assessment.setResult(1);
        }else{
            assessment.setResult(0);
        }
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }

    /**
     * 足部风险评估
     * @param assessment
     * @return
     */
    @Override
    public Integer foot(DiabetesAssessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setType(4);
        assessment.setCreateDate(new Date());
        assessment.setUpdateDate(new Date());
        assessment.setDelFlag("0");
        int total = assessment.getIsSmoking() + assessment.getIsEyeProblem() + assessment.getIsKidney()
                + assessment.getIsCardiovascular() + assessment.getIsLimbsEdema() + assessment.getIsLimbsTemp()
                + assessment.getIsDeformity() + assessment.getIsFootBeat() + assessment.getIsShinBeat();
        if(assessment.getHBA1C() <= 7 && 0 ==total){
            assessment.setResult(0);
        }
        if(assessment.getHBA1C() > 7 && 0 ==total){
            assessment.setResult(1);
        }
        if(assessment.getHBA1C() > 7 && 1 ==total){
            assessment.setResult(2);
        }
        assessmentRepo.save(assessment);
        return assessment.getResult();
    }


    private Integer sickenAssement(DiabetesAssessment assessment){
        if(null != assessment.getAge() && assessment.getAge() >= 40 || 1== assessment.getIsIGR()
                || 1 == assessment.getIsSit() || 1 == assessment.getIsFamily() || 1 == assessment.getIsLargeBaby()
                || 1 == assessment.getIsHighPressure() || 1 == assessment.getIsBloodFat() || 1== assessment.getIsArteriesHarden()
                || 1 == assessment.getIsSterol() || 1 == assessment.getIsPCOS() || 1 == assessment.getIsMedicineTreat()){
            return 1;
        }
        if(null != assessment.getHeight() && null != assessment.getWeight()){//待定
            DecimalFormat d =new DecimalFormat("##.00");
            Double value = Double.valueOf(d.format(assessment.getWeight()/Math.pow((assessment.getHeight()/100), 2)));
            if(value > 24 ){
                return 1;
            }
        }
        return 0;
    }
}
