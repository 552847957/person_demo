package com.wondersgroup.healthcloud.api.http.controllers.assessment;


import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentHistoryAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentPreDataAPIEntity;
import com.wondersgroup.healthcloud.api.utils.TimeAgoUtils;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2015/12/31.
 */
@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @RequestMapping(value = "/predata",method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<AssessmentPreDataAPIEntity> predata(@RequestParam("uid") String uid){
        JsonResponseEntity<AssessmentPreDataAPIEntity> response = new JsonResponseEntity<>();
        RegisterInfo register = registerInfoRepo.findOne(uid);
        AssessmentPreDataAPIEntity entity = null;
        if(null != register && null != register.getIdentifytype() && "1".equals(register.getIdentifytype())) {//实名认证过
            entity = new AssessmentPreDataAPIEntity(register);
        }
        response.setData(entity);
        return response;
    }


    @RequestMapping(value = "/history",method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<AssessmentHistoryAPIEntity> history(@RequestParam("uid") String uid,
                                                                      @RequestParam(value = "flag",required = false) String flag){
        JsonListResponseEntity<AssessmentHistoryAPIEntity> response = new JsonListResponseEntity<>();
        List<AssessmentHistoryAPIEntity> list = new ArrayList<>();

        Date createdate = new Date();
        if(StringUtils.isNotEmpty(flag)){
            createdate = new Date(Long.valueOf(flag));
        }
        List<Assessment> assessHistory = assessmentService.getAssessHistory(uid, createdate);
        int i = 1;
        for (Assessment assessment : assessHistory) {
            AssessmentHistoryAPIEntity entity = new AssessmentHistoryAPIEntity();
            entity.setId(assessment.getId());
            entity.setRisk(assessmentService.getResult(assessment));
            entity.setAssesstime(TimeAgoUtils.assessAgo(assessment.getCreateDate()));
            entity.setAge(assessment.getAge());
            entity.setIsFat(assessmentService.isFat(assessment.getGender(),assessment.getWaist()));
            entity.setIsOverWeight(assessmentService.overWeight(assessment.getHeight(),assessment.getWeight()));
            entity.setIsHypertension(assessmentService.isHypertension(3,assessment.getPressure()));
            entity.setNeedMovement(assessmentService.needMovement(assessment.getSport()));
            entity.setNeedAmendLife(assessmentService.needAmendLife(assessment));
            entity.setHasFamilyHistory(assessmentService.hasFamilyHistory(assessment));
            list.add(entity);
            if(i==assessHistory.size()){
                flag = String.valueOf(assessment.getCreateDate().getTime());
            }
            i++;
        }
        Boolean more = false;
        if(list.size()==10){
            more = true;
        }
        response.setContent(list, more, null, flag);
        return response;
    }


    @RequestMapping(value = "/count",method= RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity count(){
        JsonResponseEntity response = new JsonResponseEntity<>();
        Map<String,Object> map = Maps.newHashMap();
        map.put("count",assessmentService.assessNum());
        response.setData(map);
        return response;
    }

    @RequestMapping(value = "/assess",method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<AssessmentAPIEntity> assess(@RequestBody String result){
        JsonKeyReader reader = new JsonKeyReader(result);
        String uid = reader.readString("uid", false);
        String gender = reader.readString("gender", false);
        Integer age = reader.readInteger("age", false);
        Integer height = reader.readInteger("height", false);
        Float weight = reader.readObject("weight", false,Float.class);
        Float waist = reader.readObject("waist", true, Float.class);
        String diabetesRelatives = reader.readString("diabetesRelatives", false);
        String hypertensionRelatives = reader.readString("hypertensionRelatives", false);
        String strokeRelatives = reader.readString("strokeRelatives", false);
        Integer isDrink = reader.readInteger("isDrink", false);
        Integer isSmoking = reader.readInteger("isSmoking", false);
        Integer eatHabits = reader.readInteger("eatHabits", false);
        String eatTaste = reader.readString("eatTaste", false);
        Integer sport = reader.readInteger("sport", false);
        String pressure = reader.readString("pressure", false);
        Integer takeAntihypertensiveDrugs = reader.readInteger("takeAntihypertensiveDrugs", true);
        Integer isDyslipidemia = reader.readInteger("isDyslipidemia", false);
        String medicalHistory = reader.readString("medicalHistory", false);
        String femaleMedicalHistory = reader.readString("femaleMedicalHistory", true);
        Integer isDepression = reader.readInteger("isDepression", false);
        Integer isOneself = reader.readInteger("isOneself", false);

        Assessment assessment = new Assessment();
        assessment.setUid(uid);
        assessment.setGender(gender);
        assessment.setAge(age);
        assessment.setHeight(height);
        assessment.setWeight(weight);
        assessment.setWaist(waist);
        assessment.setDiabetesRelatives(diabetesRelatives);
        assessment.setHypertensionRelatives(hypertensionRelatives);
        assessment.setStrokeRelatives(strokeRelatives);
        assessment.setIsDrink(isDrink);
        assessment.setIsSmoking(isSmoking);
        assessment.setEatHabits(eatHabits);
        assessment.setEatTaste(eatTaste);
        assessment.setSport(sport);
        assessment.setPressure(pressure);
        assessment.setTakeAntihypertensiveDrugs(takeAntihypertensiveDrugs);
        assessment.setIsDyslipidemia(isDyslipidemia);
        assessment.setMedicalHistory(medicalHistory);
        assessment.setFemaleMedicalHistory(femaleMedicalHistory);
        assessment.setIsDepression(isDepression);
        assessment.setIsOneself(isOneself);
        assessment.setDelFlag("0");
        assessment.setCreateDate(new Date());

        return getResult(assessment,true);
    }

    private JsonResponseEntity<AssessmentAPIEntity> getResult(Assessment assessment,Boolean save){
        JsonResponseEntity<AssessmentAPIEntity> response = new JsonResponseEntity<>();
        if(assessment!=null) {
            AssessmentAPIEntity assessmentAPIEntity = new AssessmentAPIEntity();
            assessmentAPIEntity.setAge(assessment.getAge());
            assessmentAPIEntity.setPressure(assessment.getPressure());
            assessmentAPIEntity.setHeight(assessment.getHeight());
            assessmentAPIEntity.setWeight(assessment.getWeight());
            assessmentAPIEntity.setWaist(assessment.getWaist());
            assessmentAPIEntity.setFamilyHistory(assessmentService.familyHistory(assessment));
            String assess = "";
            if(save) {
                assess = assessmentService.assess(assessment);
                assessmentAPIEntity.setLastAssessTime(assessmentService.getLastAssessTime(assessment.getUid()));
            }else{
                assess = assessmentService.getResult(assessment);
            }
            assessmentAPIEntity.setRisk(assess);


            assessmentAPIEntity.setIsFat(assessmentService.isFat(assessment.getGender(), assessment.getWaist()));
            assessmentAPIEntity.setIsOverWeight(assessmentService.overWeight(assessment.getHeight(), assessment.getWeight()));
            assessmentAPIEntity.setIsHypertension(assessmentService.isHypertension(3, assessment.getPressure()));
            assessmentAPIEntity.setNeedMovement(assessmentService.needMovement(assessment.getSport()));
            assessmentAPIEntity.setNeedAmendLife(assessmentService.needAmendLife(assessment));
            assessmentAPIEntity.setHasFamilyHistory(assessmentService.hasFamilyHistory(assessment));
            response.setData(assessmentAPIEntity);
        }
        return response;
    }
}
