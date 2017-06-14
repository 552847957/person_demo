package com.wondersgroup.healthcloud.jpa.entity.assessment;


import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zhuchunliu on 2015/12/29.
 */
@Data
@Entity
@Table(name = "app_tb_patient_assessment")
public class Assessment extends BaseEntity{
    @Id
    private String id;
    private String uid;
    private String gender;
    private Integer age;
    private Integer height;
    private Float weight;
    private Float waist;
    @Column(name="diabetes_relatives")
    private String diabetesRelatives;//糖尿病亲属(逗号分隔)
    @Column(name="hypertension_relatives")
    private String hypertensionRelatives; //高血压亲属(逗号分隔)
    @Column(name="stroke_relatives")
    private String strokeRelatives; //脑卒中亲属(逗号分隔)
    @Column(name="is_drink")
    private Integer isDrink; //是否喝酒
    @Column(name="is_smoking")
    private Integer isSmoking; //是否吸烟
    @Column(name="eat_habits")
    private Integer eatHabits; //饮食习惯
    @Column(name="eat_taste")
    private String eatTaste; //饮食口味
    private Integer sport; //运动情况
    private String pressure; //血压值
    @Column(name="take_antihypertensive_drugs")
    private Integer takeAntihypertensiveDrugs; //是否服用降压药
    @Column(name="is_dyslipidemia")
    private Integer isDyslipidemia; //是否血脂异常
    @Column(name="medical_history")
    private String medicalHistory; //病史
    @Column(name="female_medical_history")
    private String femaleMedicalHistory; //女性病史
    @Column(name="is_depression")
    private Integer isDepression; //您是否长期接受抗精神类药物和（或）抗抑郁症药物治疗
    @Column(name="is_oneself")
    private Integer isOneself; //是否本人测试
    private String result;//风险评估高危结果1：糖尿病，2：高血压，3：脑卒中，多个值逗号间隔
}
