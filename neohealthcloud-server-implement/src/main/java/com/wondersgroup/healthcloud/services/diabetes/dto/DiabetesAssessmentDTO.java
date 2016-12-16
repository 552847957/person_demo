package com.wondersgroup.healthcloud.services.diabetes.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by zhuchunliu on 2016/12/8.
 */
@Data
public class DiabetesAssessmentDTO {
    private String id; // 主键
    private Integer age; // 年龄
    private Integer gender; // 性别，1：男，2：女
    private Double height; // 身高
    private Double weight; // 体重
    private Double waist; // 腰围
    private Integer isIGR; //是否有糖调节受损（IGR）（又称“糖尿病前期”）史
    private Integer isSit; //是否属于静坐生活方式,
    private Integer isFamily; //一级亲属中是否有2型糖尿病家族史高危人群
    private Integer isLargeBaby; //是否有巨大儿（出生体重≥4kg）生产史
    private Integer isHighPressure; //是否有高血压
    private Integer isBloodFat; //血脂是否异常
    private Integer isArteriesHarden; //是否是动脉粥样硬化性心脑血管疾病患者
    private Integer isSterol; //是否有一过性类固醇糖尿病病史者
    private Integer isPCOS; //是否是多囊卵巢综合征（PCOS）患者
    private Integer isMedicineTreat; //是否长期接受抗精神病药物和（或）抗抑郁症药物治疗的患者
    private Date create_date;
    private String name;
}
