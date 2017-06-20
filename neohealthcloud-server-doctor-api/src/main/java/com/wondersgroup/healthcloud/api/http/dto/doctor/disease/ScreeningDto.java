package com.wondersgroup.healthcloud.api.http.dto.doctor.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.assessment.dto.AssessmentConstrains;
import com.wondersgroup.healthcloud.services.doctor.dto.BaseResidentDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

/**
 * 筛查列表信息
 * Created by zhuchunliu on 2017/5/23.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreeningDto extends BaseResidentDto {

    private String riskFactor;//危险因素

    public ScreeningDto(Map<String, Object> map,Assessment assessment, RegisterInfo register, UserInfo userInfo) {

        this.setRegisterId(map.get("registerid").toString());
        this.setAvatar(StringUtils.isEmpty(register.getHeadphoto())?"":register.getHeadphoto()+ ImagePath.avatarPostfix());
        this.setName(register.getName());
        this.setGender(register.getGender());
        this.setIdentifyType("0".equals(register.getIdentifytype()) ?false:true);
        this.setIsRisk(true);
        this.setDiabetesType(null == map.get("diabetes_type") || "0".equals(map.get("diabetes_type").toString()) ?false:true);
        this.setHypType(null == map.get("hyp_type") || "0".equals(map.get("hyp_type").toString()) ?false:true);
        this.setApoType(null == map.get("apo_type") || "0".equals(map.get("apo_type").toString()) ?false:true);
        this.setSignStatus(null == map.get("sign_status")?null:map.get("sign_status").toString());

        if(null != register && null != register.getPersoncard()){
            Date birthday = DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(register.getPersoncard()));
            this.setAge( new DateTime().getYear() - new DateTime(birthday).getYear());
        }else if(null != userInfo){
            this.setAge(userInfo.getAge());
        }else if(null != register && null != register.getBirthday()){
            this.setAge(new DateTime().getYear() - new DateTime(register.getBirthday()).getYear());
        }

        this.riskFactor = this.getRiskInfo(assessment);
    }

    private String getRiskInfo(Assessment assessment) {
        if(assessment.getAge()>=40){
            return "年龄="+assessment.getAge();
        }

        Double bmi = Double.valueOf(assessment.getWeight()/Math.pow((assessment.getHeight()/100), 2));
        if(bmi>=24&&bmi<28){
            return "超重";
        }
        if(bmi>=28){
            return "肥胖";
        }

        if ((this.getGender().equals(AssessmentConstrains.GENDER_MAN) && assessment.getWaist() >= 90 )||
                (this.getGender().equals(AssessmentConstrains.GENDER_WOMAN) && assessment.getWaist() >= 85 )) {
            return "中心行肥胖";
        }
        if(!"0".equals(assessment.getDiabetesRelatives())){
            return "亲属中有糖尿病患者";
        }
        if(!"0".equals(assessment.getHypertensionRelatives())){
            return "亲属中有高血压患者";
        }
        if(!"0".equals(assessment.getStrokeRelatives())){
            return "亲属中有脑卒中患者";
        }
        if(3 == assessment.getIsDrink()){
            return "每天都喝酒";
        }
        if(1 == assessment.getIsSmoking() || 2 == assessment.getIsSmoking()){
            return 1 == assessment.getIsSmoking() ? "现在每天吸烟" : "现在吸烟，但不是每天吸烟";
        }
        if(1 != assessment.getEatHabits()){
            return 2 == assessment.getEatHabits()?"饮食习惯荤食为主":"饮食习惯素食为主";
        }
        if(!"4".equals(assessment.getEatTaste())){
            if(assessment.getEatTaste().contains("1")) return "饮食口味嗜盐";
            if(assessment.getEatTaste().contains("2")) return "饮食口味嗜油";
            if(assessment.getEatTaste().contains("3")) return "饮食口味嗜糖";
        }
        if(3 == assessment.getSport() || 4 == assessment.getSport()){
            return "严重缺乏运动";
        }
        String[] pressures = assessment.getPressure().split("/");
        Integer diastolic = Integer.valueOf(pressures[1]);
        Integer systolic = Integer.valueOf(pressures[0]);
        if(diastolic >=85 || systolic >=130){
            return "血压"+assessment.getPressure()+"mmHg";
        }
        if(1 == assessment.getTakeAntihypertensiveDrugs()){
            return "正在服用降压药";
        }
        if(1 == assessment.getIsDyslipidemia()){
            return "血脂异常";
        }
        if(!"6".equals(assessment.getMedicalHistory())){
            if(assessment.getMedicalHistory().contains("1")) return "有糖调节受损（IGR，又称糖尿病前期";
            if(assessment.getMedicalHistory().contains("2")) return "动脉粥样硬化心脑血管疾病";
            if(assessment.getMedicalHistory().contains("3")) return "有一过性类固醇糖尿病";
            if(assessment.getMedicalHistory().contains("4")) return "房颤或明显的脉搏不齐";
            if(assessment.getMedicalHistory().contains("5")) return "短暂脑缺血发作病史（TIA）";
        }
        if(1 == assessment.getIsDepression()){
            return "长期接受抗精神类药物（或）抗抑郁症药物治疗";
        }
        if("2".equals(assessment.getGender()) && !"4".equals(assessment.getFemaleMedicalHistory())){
            if(assessment.getFemaleMedicalHistory().contains("1")) return "有巨大儿（出生体重>=4KG）生产史";
            if(assessment.getFemaleMedicalHistory().contains("2")) return "有妊娠期糖尿病史";
            if(assessment.getFemaleMedicalHistory().contains("3")) return "多囊卵巢综合症患者";
        }
        return  "";
    }


}
