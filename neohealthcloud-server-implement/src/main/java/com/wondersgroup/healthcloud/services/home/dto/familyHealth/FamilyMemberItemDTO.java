package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 家庭健康异常项
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberItemDTO {
    private String relationship;
    private String prompt;
    private Long testTime;
    private String uid;      //id,用于分组


}
