package com.wondersgroup.healthcloud.services.diabetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 随访报告
 * Created by zhuchunliu on 2016/12/9.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportFollowDTO {
    @JsonProperty("cfrq")
    private Date folowDate;//随访日期
    @JsonProperty("yljgdm")
    private String hospitalCode;//医疗机构代码
    @JsonProperty("sffs")
    private String followStyle;//随访方式  1:门诊、2:家庭、3:电话、4:短信、5:网络、9:其他
    @JsonProperty("sffsqt")
    private String followStyleOther;//随访方式其他
    @JsonProperty("bcsfglzt")
    private String followStatus;//随访状态 1: 继续随访、2: 暂时性失访、3: 失访、4:转组
    @JsonProperty("yyxcsfrq")
    private Date followNextDate;//预约下次随访时间
    @JsonProperty("sfz")
    private String doctorName;//随访医生
    @JsonProperty("tnblczz")
    private String symptom;//糖尿病临床症状 1:多饮、多尿、2:多食/常有饥饿感、3:乏力、4:体重下降
                            //5:视力下降、6：肢体麻木、7：下肢浮肿、8：肢端溃疡
                            //9:皮肤及外阴瘙痒、10：其它、11：无相关临床症状【可多选，各值间用逗号隔开】
    @JsonProperty("fpg")
    private BigDecimal emptyBloodSugar;//空腹血糖
    @JsonProperty("chlxsxtogtt")
    private BigDecimal ogtt;//餐后两小时血糖OGTT
    @JsonProperty("hba1C")
    private BigDecimal hemoglobin;//糖化血红蛋白
    @JsonProperty("sfjy")
    private String advice;//随访建议 1：控制饮食、2：戒烟戒酒、3：减轻体重、4：规律活动
                            //5：放松情绪、6：定期检查、7：遵医嘱服药
                            //8、其他（填写文字描述） 【可填多个，用逗号间隔】
    @JsonProperty("sfjyqt")
    private String adviceOther;//其他随访建议
}
