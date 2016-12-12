package com.wondersgroup.healthcloud.api.http.dto;

import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDetailDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/**
 * Created by Administrator on 2016/12/12.
 */
@Data
public class TubePatientDetailEntity {
    private String name;//姓名
    private String gender;//性别
    private String birthDay;//出生年月
    private String personcard;//身份证号码
    private String profession;//职业
    private String employStatus;//就业状况
    private String moblilePhone;//手机号码
    private String fixedPhone;//固定号码
    private String contactPhone;//联系电话

    public TubePatientDetailEntity(TubePatientDetailDTO dto){
        this.name = dto.getName();
        this.gender = dto.getGender();
        this.moblilePhone = dto.getMoblilePhone();
        this.fixedPhone = dto.getFixedPhone();
        this.contactPhone = dto.getContactPhone();
        if(null != dto.getBirthDay()){
            this.birthDay = new DateTime(dto.getBirthDay()).toString("yyyy-MM-dd");
        }
        if(!StringUtils.isEmpty(dto.getCardType()) && "01".equals(dto.getCardType())){
            this.personcard = dto.getCardNumber();
        }

        if(!StringUtils.isEmpty(dto.getEmployStatus())){
            if("31".equals(dto.getEmployStatus())){
                this.employStatus = "学生";
            }else if("70".equals(dto.getEmployStatus())){
                this.employStatus = "无业人员";
            }else if("80".equals(dto.getEmployStatus())){
                this.employStatus = "退（离）休人员";
            }else if("90".equals(dto.getEmployStatus())){
                this.employStatus = "其他";
            }
        }
    }
}
