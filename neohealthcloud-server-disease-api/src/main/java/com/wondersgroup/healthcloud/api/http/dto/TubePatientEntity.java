package com.wondersgroup.healthcloud.api.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/12.
 */
@Data
public class TubePatientEntity {
    private String name; //姓名
    private String gender;//性别
    private Integer age;//年龄
    private String diagnoseDate;//诊断时间
    private String diabetesType;
    private String cardType;//证件类型 01:身份证
    private String cardNumber;//证件号码
    private String flag; // 0 正常 1 偏高 2 偏低
    private String measureDate;//测量日期
    private Double bloodGlucose;//血糖值
    private String interval;//区间 0 早餐前 1 早餐后 2 午餐前 3 午餐后 4 晚餐前 5 晚餐后 6睡前 7 凌晨 8 随机

    public TubePatientEntity(TubePatientDTO dto){
        this.name = dto.getName();
        this.gender = dto.getGender();
        this.cardType = dto.getCardType();
        this.cardNumber = dto.getCardNumber();
        if(null != dto.getBirthDay()){
            this.age = (new DateTime().getYear() - new DateTime(dto.getBirthDay()).getYear());
        }
        if(null != dto.getDiagnoseDate()){
            this.diagnoseDate = new DateTime(dto.getDiagnoseDate()).toString("yyyy-MM-dd");
        }
        if(!StringUtils.isEmpty(dto.getDiabetesType())){
            if("1".equals(dto.getDiabetesType())){
                this.diabetesType = "1型糖尿病";
            }
            else if("2".equals(dto.getDiabetesType())){
                this.diabetesType = "2型糖尿病";
            }
            else if("3".equals(dto.getDiabetesType())){
                this.diabetesType = "其它特殊类型糖尿病";
            }
            else if("4".equals(dto.getDiabetesType())){
                this.diabetesType = "妊娠糖尿病";
            }
        }
    }
}
