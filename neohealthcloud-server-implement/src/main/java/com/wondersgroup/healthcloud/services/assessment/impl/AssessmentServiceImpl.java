package com.wondersgroup.healthcloud.services.assessment.impl;



import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.repository.assessment.AssessmentRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import com.wondersgroup.healthcloud.services.assessment.MeasureService;
import com.wondersgroup.healthcloud.services.assessment.dto.AssessmentConstrains;
import com.wondersgroup.healthcloud.utils.PageFactory;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Yoda on 2015/12/29.
 */
@Service("assessmentServiceImpl")
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private MeasureService measureService;

    @Override
    public String getLastAssessTime(String uid){
        if(!guest(uid)) {
            List<Assessment> assessHistory = getAssessHistory(uid, new Date());
            if (!assessHistory.isEmpty() && assessHistory.size() > 1) {
                Date createDate = assessHistory.get(1).getCreateDate();
                if (createDate != null) {
                    return new DateTime(createDate).toString("yyyy-MM-dd");
                }
            }
        }
        return "";
    }

    private static Boolean guest(String uid){
        return "guest".equals(uid);
    }



    @Override
    public String assess(Assessment assessment) {
        assessment.setId(IdGen.uuid());
        assessment.setCreateDate(new Date());
        assessment.setDelFlag("0");
        assessmentRepository.save(assessment);
        return getResult(assessment);
    }

    @Override
    public String getResult(Assessment assessment){
        String result="";
        if(isDiabetesHighRisk(assessment)){
            result += "1-3,";
        }else{
            if(diabetesRiskCheck(assessment)) {
                result += "1-2,";
            }
        }

        if(isHypertensionHighRisk(assessment)) {
            result += "2-3,";
        }else{
            if(hypertensionRiskCheck(assessment)){
                result += "2-2,";
            }
        }
        if(isStrokeHighRisk(assessment)) {
            result += "3-3,";
        }else{
            if(strokeRiskCheck(assessment)){
                result += "3-2,";
            }
        }
        if(result.endsWith(",")) {
            result = result.substring(0, result.lastIndexOf(","));
        }
        if(StringUtils.isBlank(result)){
            result="0";
        }
        return result;
    }


    @Override
    public Long assessNum(){
        return assessmentRepository.countAssessNum();
    }


    @Override
    public Boolean isHypertension(Integer standards, String pressure){
        if (StringUtils.isNotEmpty(pressure)||"0".equals(pressure)) {
            String[] pressures = pressure.split("/");
            if(pressures.length==2) {
                Integer diastolic = Integer.valueOf(pressures[1]);
                Integer systolic = Integer.valueOf(pressures[0]);
                if (standards == 1) {
                    String diastolicFlag = measureService.checkdPressureDiastolic(diastolic);
                    String systolicFlag = measureService.checkdPressureSystolic(systolic);
                    return diastolicFlag.equals(AssessmentConstrains.MEASURE_FLAG_HIGH) ||
                            systolicFlag.equals(AssessmentConstrains.MEASURE_FLAG_HIGH);
                } else if (standards == 2) {
                    return (diastolic >= 85 && diastolic <= 90) || (systolic >= 130 && systolic <= 139);
                } else {
                    return diastolic >=85 || systolic >=130;
                }
            }
        }
        return false;
    }


    @Override
    public Boolean overWeight(Integer height, Float weight){
        String bmiFlag = measureService.checkBMI(Double.valueOf(height), Double.valueOf(weight));
        return  bmiFlag.equals(AssessmentConstrains.MEASURE_FLAG_HIGH) ||
                bmiFlag.equals(AssessmentConstrains.MEASURE_FLAG_ULTRAHIGH);
    }



    @Override
    public Boolean isFat(String gender, Float waist){
        if(waist!=null) {
            if (gender.equals(AssessmentConstrains.GENDER_MAN)) {
                return waist >= 90;
            } else if (gender.equals(AssessmentConstrains.GENDER_WOMAN)) {
                return waist >= 85;
            }
        }
        return false;
    }

    @Override
    public Boolean needMovement(Integer sport){
        return sport==AssessmentConstrains.SPORT_NONE||sport==AssessmentConstrains.SPORT_ATTIMES;
    }

    @Override
    public Boolean needAmendLife(Assessment assessment){
        return !assessment.getEatTaste().contains(AssessmentConstrains.EAT_TASTE_NORMAL)||assessment.getEatHabits()!=AssessmentConstrains.EAT_HABITS_BALANCED;
    }

    @Override
    public Boolean hasFamilyHistory(Assessment assesment){
        return isWholeRelative(assesment.getDiabetesRelatives())||
                isWholeRelative(assesment.getHypertensionRelatives())||
                isWholeRelative(assesment.getStrokeRelatives());
    }

    @Override
    public String familyHistory(Assessment assesment){
        String fh="";
        if(isWholeRelative(assesment.getDiabetesRelatives())){
            fh+="1,";
        }
        if(isWholeRelative(assesment.getHypertensionRelatives())){
            fh+="2,";
        }
        if(isWholeRelative(assesment.getStrokeRelatives())){
            fh+="3";
        }
        if(fh.endsWith(",")){
            fh = fh.substring(0,fh.lastIndexOf(","));
        }
        return fh;
    }

    @Override
    public List<Assessment> getAssessHistory(String uid, Date flag){
        Pageable pageable = PageFactory.create(1, 10, "createDate:desc");
        return assessmentRepository.getAssessmentHistory(uid,flag,pageable).getContent();
    }

    @Override
    public Assessment getAssessment(String assessId){
        return  assessmentRepository.findOne(assessId);
    }

    private static Boolean isFirstDegreeRelative(String relatives){
        return  relatives.contains(AssessmentConstrains.RELATIVES_BRANDSR)||
                relatives.contains(AssessmentConstrains.RELATIVES_DAD)||
                relatives.contains(AssessmentConstrains.RELATIVES_DAUGHTER)||
                relatives.contains(AssessmentConstrains.RELATIVES_MOM)||
                //relatives.contains(AssessmentConstrains.RELATIVES_ONESELF)||
                relatives.contains(AssessmentConstrains.RELATIVES_SON);
    }

    private static Boolean isWholeRelative(String relatives){
        return isFirstDegreeRelative(relatives)||relatives.contains(AssessmentConstrains.RELATIVES_GPANDGM);
    }

    /**
     * 高血压易患人群标准
     1、收缩压介于130～139mmHg之间或舒张压介于85mmHg～90mmHg之间。
     2、超重或肥胖（BMI≥24kg/ m2）。
     3、高血压家族史（一级亲属）。
     4、糖尿病患者。
     5、长期的过量饮酒史（每日白酒量≥100ml且每周饮酒≥4次）。
     6、长期高盐膳食。
     7、高血压患者

     * @param assessment
     * @return
     */
    private Boolean hypertensionRiskCheck(Assessment assessment){
        if(assessment!=null){
            return overWeight(assessment.getHeight(),assessment.getWeight())||
                    isFirstDegreeRelative(assessment.getHypertensionRelatives())||
                    assessment.getEatTaste().contains(AssessmentConstrains.EAT_TASTE_SALT)||
                    AssessmentConstrains.DRINK_EVERYDAY==assessment.getIsDrink()||
                    isHypertension(2,assessment.getPressure())||
                    isDiabetesHighRisk(assessment)||
                    isHypertensionHighRisk(assessment);
        }
        return false;
    }

    /**
     * 脑卒中高危人群标准
     * 脑卒中风险初筛评估对象为：
     用户满足[题目十八]，选择[短暂脑缺血发作病史（TIA）]，判断为[脑卒中]风险人群。
     用户选择其他符合上述表格中脑卒中的答案，需要满足下列任意三个条件，且40岁以上，方可确认为[脑卒中]易患人群。
     1、高血压病史（≥140/90 mmHg），或正在服用降压药
     2、房颤或明显的脉搏不齐；
     3、吸烟；
     4、血脂异常或未知；
     5、糖尿病；
     6、很少进行体育活动（体育锻炼的标准是每周锻炼≥3次、每次≥30分钟、持续时间超过1年。从事农业体力劳动可视为有体育活动）；
     7、肥胖（BMI≥26 kg/m2）；
     8、有卒中家族史。

     * @param assessment
     * @return
     */
    private Boolean strokeRiskCheck(Assessment assessment){
        if(assessment!=null){
            if(assessment.getMedicalHistory().contains(AssessmentConstrains.MEDICAL_HISTORY_TIA)){
                return true;
            }
            if(assessment.getAge()>=40){
                Boolean[] conditions = new Boolean[8];
                conditions[0] = isHypertension(1,assessment.getPressure())||assessment.getTakeAntihypertensiveDrugs()==AssessmentConstrains.CHOISE_TRUE;
                conditions[1] = assessment.getMedicalHistory().contains(AssessmentConstrains.MEDICAL_HISTORY_AF);
                conditions[2] = assessment.getIsSmoking()==AssessmentConstrains.SMOKE_EVERYDAY||assessment.getIsSmoking()==AssessmentConstrains.SMOKE_NOTEVERYDAY;
                conditions[3] = assessment.getIsDyslipidemia()==AssessmentConstrains.CHOISE_UNSPECIFIED||assessment.getIsDyslipidemia()==AssessmentConstrains.CHOISE_TRUE;
                conditions[4] = isDiabetesHighRisk(assessment);
                conditions[5] = needMovement(assessment.getSport());
                conditions[6] = overWeightForStroke(assessment.getHeight(),assessment.getWeight());
                conditions[7] = isWholeRelative(assessment.getStrokeRelatives());
                return require3condition(conditions);
            }
        }
        return false;
    }



    private static boolean require3condition(Boolean[] conditions){
        int i=0;
        for (Boolean condition : conditions) {
            if(condition){
                i++;
            }
        }
        return i>=3;
    }




    private Boolean overWeightForStroke(Integer height,Float weight){
        Double bmi =measureService.calculateBMI(Double.valueOf(height), Double.valueOf(weight));
        return bmi>=26;
    }

    private boolean isStrokeHighRisk(Assessment assessment){
        return assessment.getStrokeRelatives().contains(AssessmentConstrains.RELATIVES_ONESELF);
    }

    private boolean isDiabetesHighRisk(Assessment assessment){
        return assessment.getDiabetesRelatives().contains(AssessmentConstrains.RELATIVES_ONESELF);
    }

    private boolean isHypertensionHighRisk(Assessment assessment){
        return assessment.getHypertensionRelatives().contains(AssessmentConstrains.RELATIVES_ONESELF);
    }

    /**
     * 2型糖尿病高危人群初筛标准
     1、年龄≥40岁。
     2、有糖调节受损（IGR）（又称“糖尿病前期”）史。
     3、超重（BMI≥24kg/m2）或肥胖（BMI≥28 kg/m 2），和（或）中心型肥胖（男性腰围≥90cm，女性腰围≥85cm）。
     4、静坐（从不运动）生活方式。
     5、一级亲属中有2型糖尿病家族史高危种族。
     6、有巨大儿（出生体重≥4kg）生产史，妊娠期糖尿病（GDM）史妇女。
     7、高血压（收缩压≥140和（或）舒张压≥90mmHg），或正在接受降压治疗。
     8、血脂异常（HDL-C≤0.91mmol/L（≤35mg/dl）及TG≥2.22mmol/L（≥200mg/dl），或正在接受调脂治疗。
     9、动脉粥样硬化性心脑血管疾病患者。
     10、有一过性类固醇糖尿病病史者。
     11、多囊卵巢综合征（PCOS）患者。
     12、长期接受抗精神病药物和（或）抗抑郁症药物治疗的患者。
     * @param assessment
     * @return
     */
    private Boolean diabetesRiskCheck(Assessment assessment){
        if(assessment!=null) {
            return (assessment.getAge()) >= 40 ||
                    assessment.getMedicalHistory().contains(AssessmentConstrains.MEDICAL_HISTORY_IGR) ||
                    assessment.getSport()==AssessmentConstrains.SPORT_NONE||
                    isFirstDegreeRelative(assessment.getDiabetesRelatives()) ||
                    assessment.getFemaleMedicalHistory().contains(AssessmentConstrains.FAMALE_MEDICAL_HISTORY_GDM) ||
                    assessment.getFemaleMedicalHistory().contains(AssessmentConstrains.FAMALE_MEDICAL_HISTORY_DELIVERY) ||
                    assessment.getMedicalHistory().contains(AssessmentConstrains.MEDICAL_HISTORY_ATHEROSCLEROSIS) ||
                    assessment.getMedicalHistory().contains(AssessmentConstrains.MEDICAL_HISTORY_STEROID) ||
                    assessment.getFemaleMedicalHistory().contains(AssessmentConstrains.FAMALE_MEDICAL_HISTORY_PCOS) ||
                    assessment.getIsDepression() == AssessmentConstrains.CHOISE_TRUE ||
                    overWeight(assessment.getHeight(),assessment.getWeight())||
                    isFat(assessment.getGender(), assessment.getWaist()) ||
                    assessment.getTakeAntihypertensiveDrugs() == AssessmentConstrains.CHOISE_TRUE ||
                    assessment.getIsDyslipidemia() == AssessmentConstrains.CHOISE_TRUE ||
                    isHypertension(1,assessment.getPressure());

        }
        return false;
    }

    /**
     * 用户是否有慢性疾病
     * @param uid 用户主键
     * @return 只要用户用糖尿病、高血压、脑卒中中一种即为true，否则为false
     */
    @Override
    public Boolean hasDiseases(String uid){
        Page<Assessment> page = assessmentRepository.getAssessmentHistory(uid, new Date(), PageFactory.create(1, 1, "createDate:desc"));
        if(null== page || null == page.getContent() || 0 == page.getContent().size()){
            return false;
        }
        Assessment assessment = page.getContent().get(0);

        if(isDiabetesHighRisk(assessment)){
            return true;
        }else{
            if(diabetesRiskCheck(assessment)) {
                return true;
            }
        }

        if(isHypertensionHighRisk(assessment)) {
            return true;
        }else{
            if(hypertensionRiskCheck(assessment)){
                return true;
            }
        }
        if(isStrokeHighRisk(assessment)) {
            return true;
        }else{
            if(strokeRiskCheck(assessment)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户最近的一次风险评估
     * @param uid
     * @return
     */
    @Override
    public Assessment getRecentAssess(String uid) {
        return assessmentRepository.getRecentAssess(uid);
    }

    @Override
    public Map<String,Object> getRecentAssessIsNormal(String uid) {
        Assessment assessment =  assessmentRepository.getRecentAssess(uid);
        if(null == assessment){
            return  null;
        }
        String risk = this.getResult(assessment);
        Map<String,Object> map = Maps.newHashMap();
        if(!StringUtils.isEmpty(risk) && (risk.contains("-3"))){
            map.put("state",3);
        }else if(!StringUtils.isEmpty(risk) && (risk.contains("-2"))){
            map.put("state",2);
        }else{
            map.put("state",1);
        }
        map.put("date",new DateTime(assessment.getCreateDate()).toString("yyyy-MM-dd"));
        return map;
    }

    /**
     * 根据用户id获取评估的数量
     * @param uid
     * @return
     */
    @Override
    public Integer getAssessNum(String uid){
        return assessmentRepository.getAssessNum(uid);
    }

}
