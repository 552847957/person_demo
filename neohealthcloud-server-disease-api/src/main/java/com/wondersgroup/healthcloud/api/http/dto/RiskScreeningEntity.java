package com.wondersgroup.healthcloud.api.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessment;
import com.wondersgroup.healthcloud.services.diabetes.dto.DiabetesAssessmentDTO;
import lombok.Data;
import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiskScreeningEntity {
    private String id;
    private String name;
    private Integer gender;
    private Integer age;
    private String date;
    private List<String> indicators;
    public RiskScreeningEntity(DiabetesAssessmentDTO assessment){
        this.id = assessment.getId();
        this.name = assessment.getName();
        this.gender = assessment.getGender();
        this.age = assessment.getAge();
        this.date = new DateTime(assessment.getCreate_date()).toString("yyyy-MM-dd");
        this.indicators = Lists.newArrayList();
        if(this.age >= 40){
            this.indicators.add("年龄40岁及以上");
        }
        if(this.BMI(assessment)){
            this.indicators.add("BMI异常");
        }
        if(1 == assessment.getIsIGR()){
            this.indicators.add("有糖尿病前期史");
        }
        if(1 == assessment.getIsSit()){
            this.indicators.add("静坐生活方式");
        }
        if(1 == assessment.getIsFamily()){
            this.indicators.add("一级亲属中有2型糖尿病家族史高危人群");
        }
        if(2 == assessment.getGender() && 1 == assessment.getIsLargeBaby()){
            this.indicators.add("有妊娠期糖尿病史");
        }
        if(1 == assessment.getIsHighPressure()){
            this.indicators.add("有高血压正在接受降压治疗");
        }
        if(1 == assessment.getIsBloodFat()){
            this.indicators.add("血脂异常，正在接受调脂治疗");
        }
        if(1 == assessment.getIsArteriesHarden()){
            this.indicators.add("动脉粥样硬化性心脑血管疾病患者");
        }
        if(1 == assessment.getIsSterol()){
            this.indicators.add("一过性类固醇糖尿病病史者");
        }
        if(2 == assessment.getGender() && 1 == assessment.getIsPCOS()){
            this.indicators.add("多囊卵巢综合征(PCOS)患者");
        }
        if(1 == assessment.getIsMedicineTreat()){
            this.indicators.add("长期接受抗精神病药物和（或）抗抑郁症药物治疗的患者");
        }
    }

    private Boolean BMI(DiabetesAssessmentDTO assessment){
        if(null != assessment.getHeight() && null != assessment.getWeight()){//待定
            DecimalFormat d =new DecimalFormat("##.00");
            Double value = Double.valueOf(d.format(assessment.getWeight()/Math.pow((assessment.getHeight()/100), 2)));
            if(value >= 24 ){
                return true;
            }
        }
        if(null != assessment.getGender() && null != assessment.getWaist()){
            if(1 == assessment.getGender() && assessment.getWaist() >= 90){
                return true;
            }
            if(2 == assessment.getGender() && assessment.getWaist() >= 85){
                return true;
            }
        }
        return false;
    }

}
