package com.wondersgroup.healthcloud.api.http.dto.doctor.disease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.assessment.dto.AssessmentConstrains;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

/**
 * 筛查列表信息
 * Created by zhuchunliu on 2017/5/23.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreeningDto {
    private String registerid;//用户主键
    private String headphoto;// 头像
    private String name;//姓名
    private String gender;//性别
    private Integer age;//年龄
    private Boolean hasIdentify;// 是否实名认证
    private Boolean hasHignRisk;//是否高危
    private Boolean hasDiabetes;//是否糖尿病
    private Boolean hasBloodPressure;//是否高血压
    private Boolean hasStroke;//是否脑卒中
    private String riskFactor;//危险因素

    public ScreeningDto(Map<String, Object> map,Assessment assessment, RegisterInfo register, UserInfo userInfo) {
        this.registerid = map.get("registerid").toString();
        this.headphoto = null == map.get("headphoto")?null : map.get("headphoto").toString();
        this.name = null == map.get("name")?null : map.get("name").toString();
        this.gender = null == map.get("gender")?null : map.get("gender").toString();
        this.hasIdentify = null == map.get("identifytype") || "0".equals(map.get("identifytype").toString()) ?false:true;
        this.hasHignRisk = true;
        this.hasDiabetes = null == map.get("diabetes_type") || "0".equals(map.get("diabetes_type").toString()) ?false:true;
        this.hasBloodPressure = null == map.get("hyp_type") || "0".equals(map.get("hyp_type").toString()) ?false:true;
        this.hasStroke = null == map.get("apo_type") || "0".equals(map.get("apo_type").toString()) ?false:true;

        if(null != register && null != register.getPersoncard()){
            Date birthday = DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(register.getPersoncard()));
            this.age = new DateTime().getYear() - new DateTime(birthday).getYear();
        }else if(null != userInfo){
            this.age = userInfo.getAge();
        }else if(null != register && null != register.getBirthday()){
            this.age = new DateTime().getYear() - new DateTime(register.getBirthday()).getYear();
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

        if ((gender.equals(AssessmentConstrains.GENDER_MAN) && assessment.getWaist() >= 90 )||
                (gender.equals(AssessmentConstrains.GENDER_WOMAN) && assessment.getWaist() >= 85 )) {
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
        if(1 == assessment.getIsSmoking()){
            return "现在每天吸烟";
        }
        if(2 == assessment.getIsSmoking()){
            return "现在吸烟，但不是每天吸烟";
        }
        

        return  "";
    }


}
