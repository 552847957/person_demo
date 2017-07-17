package com.wondersgroup.healthcloud.api.http.dto.family;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;

/**
 * 家人信息
 * Created by sunhaidi on 2016年12月14日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyInfoDTO {
    private String id;
    private String relation_name;
    private String height;
    private String weight;
    private String birthDate;
    private String sex;
    private Integer age;
    private String mobile;
    private String nickname;
    private String avatar;
    private Boolean isStandalone;
    private Boolean isVerification;
    private Boolean isAccess;
    private Boolean isChild;

    private String name;
    private String medicarecard;
    private String idcard;

    public String getMedicarecard() {
        if(!StringUtils.isBlank(this.medicarecard)){
                this.medicarecard = medicarecard.substring(0,2) + "****" + medicarecard.substring(medicarecard.length() -3, medicarecard.length());
        }
        return medicarecard;
    }

    public String getIdcard() {
        if(!StringUtils.isBlank(this.idcard)){
            this.idcard = idcard.substring(0,4) + "**********" + idcard.substring(idcard.length() - 4, idcard.length());
        }
        return idcard;
    }


}
