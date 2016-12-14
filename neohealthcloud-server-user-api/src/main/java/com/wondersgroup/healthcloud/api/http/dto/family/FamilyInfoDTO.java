package com.wondersgroup.healthcloud.api.http.dto.family;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyInfoDTO {
    private String id;
    private String relation_name;
    private String height;
    private String weight;
    private String birthDate;
    private String sex;
    private String mobile;
    private String nickname;
    private String avatar;
    private Boolean isStandalone;
    private Boolean isVerification;
    
    
}
