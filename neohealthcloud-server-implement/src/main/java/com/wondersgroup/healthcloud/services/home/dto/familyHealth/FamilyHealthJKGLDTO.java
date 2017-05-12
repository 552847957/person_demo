package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.services.remind.dto.RemindForHomeDTO;

/**
 * 家庭健康栏目
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyHealthJKGLDTO {
    private UserHealthDTO userHealth;
    private FamilyMemberJKGLDTO familyMember;
    private RemindForHomeDTO takeDrugsRemind;
    public FamilyHealthJKGLDTO(){}
    public FamilyHealthJKGLDTO(UserHealthDTO userHealth,FamilyMemberJKGLDTO familyMember){
        this.userHealth = userHealth;
        this.familyMember = familyMember;
    }
}
