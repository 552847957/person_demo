package com.wondersgroup.healthcloud.api.http.controllers.assessment;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentHistoryAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.assessment.AssessmentPreDataAPIEntity;
import com.wondersgroup.healthcloud.api.utils.TimeAgoUtils;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.jpa.repository.assessment.AssessmentRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.UserInfoRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentController.class);

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @Autowired
    private UserInfoRepository userInfoRepo;

    @Autowired
    private Environment env;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @GetMapping(value = "/predata")
    @VersionRange
    public JsonResponseEntity<AssessmentPreDataAPIEntity> predata(@RequestParam("uid") String uid){
        JsonResponseEntity<AssessmentPreDataAPIEntity> response = new JsonResponseEntity<>();
        RegisterInfo register = registerInfoRepo.findOne(uid);
        UserInfo userInfo = userInfoRepo.findOne(uid);
        AssessmentPreDataAPIEntity entity = new AssessmentPreDataAPIEntity(register,userInfo);

        String url=env.getProperty("internal.api.service.measure.url")+"/api/measure/2/nearest?registerId="+uid;
        Request build= new RequestBuilder().get().url(url).build();
        String body = httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class).body();

        try {
            JsonNode jsonNode = new ObjectMapper().readTree(body);
            if(jsonNode.get("code").intValue() == 0){
                JsonNode child = jsonNode.get("data");
                String date = child.get("testTime").asText();
                if(!StringUtils.isEmpty(date)){
                    Date testTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(date).toDate();
                    if(DateTime.now().plusDays(-3).isBefore(new DateTime(testTime).getMillis())){
                        entity.setPressure(child.get("systolic").asText()+"/"+child.get("diastolic").asText());
                    }
                }
            }

        }catch (Exception ex){
            LOGGER.error(ex.getMessage(),ex);
        }

        response.setData(entity);
        return response;
    }


    @GetMapping(value = "/history")
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

    @GetMapping(value = "/recent")
    @VersionRange
    public JsonResponseEntity<AssessmentHistoryAPIEntity> recent(@RequestParam("uid") String uid){

        Assessment assessment = assessmentService.getRecentAssess(uid);
        if(null == assessment){
            return new JsonResponseEntity();
        }
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
        return new JsonResponseEntity(0,null,entity);
    }


    @GetMapping(value = "/count")
    @VersionRange
    @WithoutToken
    public JsonResponseEntity count(){
        JsonResponseEntity response = new JsonResponseEntity<>();
        Map<String,Object> map = Maps.newHashMap();
        map.put("count",assessmentService.assessNum());
        response.setData(map);
        return response;
    }

    @PostMapping(value = "/assess")
    @VersionRange
    @WithoutToken
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
        assessment.setResult(0);

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
            assessmentAPIEntity.setSport(assessment.getSport());
            response.setData(assessmentAPIEntity);

            if(!assessmentAPIEntity.getRisk().equals("0") || assessmentAPIEntity.getIsFat() ||
                    assessmentAPIEntity.getIsOverWeight() || assessmentAPIEntity.getIsHypertension() ||
                    assessmentAPIEntity.getNeedMovement() || assessmentAPIEntity.getNeedAmendLife()||
                    assessmentAPIEntity.getHasFamilyHistory()){
                assessment.setResult(1);
                assessmentRepository.save(assessment);
            }
        }
        return response;
    }

    @VersionRange
    @GetMapping
    public JsonResponseEntity<AssessmentAPIEntity> get(@RequestParam("id") String id){
        Assessment assessment = assessmentService.getAssessment(id);
        return getResult(assessment,false);
    }
}
