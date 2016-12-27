package com.wondersgroup.healthcloud.api.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportFollowDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/12/13.
 */
@Data
public class ReportFollowEntity {

    private String folowDate;//随访日期
    private String hospitalName;//医疗机构代码
    private String followStyle;//随访方式  1:门诊、2:家庭、3:电话、4:短信、5:网络、9:其他
    private String followStatus;//随访状态 1: 继续随访、2: 暂时性失访、3: 失访、4:转组
    private String followNextDate;//预约下次随访时间
    private String doctorName;//随访医生
    private List<String> symptom;//糖尿病临床症状
    private BigDecimal emptyBloodSugar;//空腹血糖
    private BigDecimal ogtt;//餐后两小时血糖OGTT
    private BigDecimal hemoglobin;//糖化血红蛋白
    private List advice;//随访建议
    private String adviceOther;//其他随访建议

    public ReportFollowEntity(ReportFollowDTO dto){
        this.folowDate = null == dto.getFolowDate() ? null : new DateTime(dto.getFolowDate()).toString("yyyy-MM-dd");
        this.followNextDate = null == dto.getFollowNextDate() ? null : new DateTime(dto.getFollowNextDate()).toString("yyyy-MM-dd");
        this.doctorName = dto.getDoctorName();
        this.adviceOther = dto.getAdviceOther();
        this.emptyBloodSugar = dto.getEmptyBloodSugar();
        this.ogtt = dto.getOgtt();
        this.hemoglobin = dto.getHemoglobin();

        if(!StringUtils.isEmpty(dto.getFollowStyle())){
            if("1".equals(dto.getFollowStyle())){
                this.followStyle = "门诊";
            }else if("2".equals(dto.getFollowStyle())){
                this.followStyle = "家庭";
            }else if("3".equals(dto.getFollowStyle())){
                this.followStyle = "电话";
            }else if("4".equals(dto.getFollowStyle())){
                this.followStyle = "短信";
            }else if("5".equals(dto.getFollowStyle())){
                this.followStyle = "网络";
            }else if("9".equals(dto.getFollowStyle())){
                this.followStyle = dto.getFollowStyleOther();
            }
        }
        if(!StringUtils.isEmpty(dto.getFollowStatus())){
            if("1".equals(dto.getFollowStatus())){
                this.followStatus = "继续随访";
            }else if("2".equals(dto.getFollowStatus())){
                this.followStatus = "暂时性失访";
            }else if("3".equals(dto.getFollowStatus())){
                this.followStatus = "失访";
            }else if("4".equals(dto.getFollowStatus())){
                this.followStatus = "转组";
            }
        }

        if(!StringUtils.isEmpty(dto.getSymptom())){
            this.symptom = Lists.newArrayList();
            for(String object : dto.getSymptom().split(",")){
                switch (Integer.parseInt(object)){
                    case 1:
                        this.symptom.add("多饮、多尿"); break;
                    case 2:
                        this.symptom.add("多食/常有饥饿感"); break;
                    case 3:
                        this.symptom.add("乏力"); break;
                    case 4:
                        this.symptom.add("体重下降"); break;
                    case 5:
                        this.symptom.add("视力下降"); break;
                    case 6:
                        this.symptom.add("肢体麻木"); break;
                    case 7:
                        this.symptom.add("下肢浮肿"); break;
                    case 8:
                        this.symptom.add("肢端溃疡"); break;
                    case 9:
                        this.symptom.add("皮肤及外阴瘙痒"); break;
                    case 10:
                        this.symptom.add("其它"); break;
                    case 11:
                        this.symptom.add("无相关临床症状"); break;
                }
            }
        }

        if(!StringUtils.isEmpty(dto.getAdvice())){
            this.advice = Lists.newArrayList();
            for(String object : dto.getAdvice().split(",")){
                switch (Integer.parseInt(object)){
                    case 1:
                        this.advice.add("控制饮食"); break;
                    case 2:
                        this.advice.add("戒烟戒酒"); break;
                    case 3:
                        this.advice.add("减轻体重"); break;
                    case 4:
                        this.advice.add("规律活动"); break;
                    case 5:
                        this.advice.add("放松情绪"); break;
                    case 6:
                        this.advice.add("定期检查"); break;
                    case 7:
                        this.advice.add("遵医嘱服药"); break;
                }
            }
        }



    }
}
