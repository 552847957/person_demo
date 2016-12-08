package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 *  慢病风险评估
 * Created by zhuchunliu on 2016/12/6.
 */
@Data
@Entity
@Table(name = "app_tb_diabetes_assessment")
public class DiabetesAssessment {
    @Id
    private String id; // 主键
    private String registerid;//注册用户id
    private Integer hasRemind;//评估异常时，是否进行过异常提醒，0：否，1：是
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
    private Integer isHistory; //糖尿病史是否超过5年
    private Integer isEyeHistory; //是否有糖尿病眼病史
    private Integer isPressureHistory; //是否有高血压史
    private Integer isUrine; //尿中泡沫是否增多或不易消退
    private Integer isEdema; //是否有反复眼睑或下肢浮肿
    private Integer isTired; //是否经常感到乏力、易疲劳
    private Integer isCramp; //是否经常感到腰背酸痛，易抽经
    private Integer isEyeSight; //是否有渐进性视力减退
    private Integer isEyeFuzzy; //是否视物不清，如隔云烟
    private Integer isEyeShadow; //是否眼前有小黑影漂浮
    private Integer isEyeGhosting; //是否看东西出现重影
    private Integer isEyeFlash;//是否看东西有闪光感
    private Double  hbac; //最近一次糖化血红蛋白
    private Integer isSmoking; //是否抽烟
    private Integer isEyeProblem; //是否有视力问题
    private Integer isKidney; //是否有肾脏疾病
    private Integer isCardiovascular; //是否有心血管疾病
    private Integer isLimbsTemp; //下肢温度是否正常
    private Integer isLimbsEdema; //下肢是否出现水肿 （足踝以下、膝关节以下、大腿以下、无）
    private Integer isDeformity; //下肢或者足部是否存在畸形
    private Integer isFootBeat; //足部动脉是否搏动
    private Integer isShinBeat; //胫后动脉（内侧踝骨下方可触及）是否搏动
    private Integer type;//类型：1、患病风险评估;2、肾病症状评估;3、眼病症状评估;4、足部风险评估
    private Integer result;//评估结果: 0、正常;  1、type1：高危/type2:满足1-2项/type3:出现症状/type4:轻度; 2、type2:满足3项及以上/type4:属于高度
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_date")
    private Date updateDate;

}
