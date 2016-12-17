package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 家庭健康栏目
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyHealthDTO {
    private UserHealthDTO userHealth;
    private FamilyMemberDTO familyMember;
    public FamilyHealthDTO(){}
    public FamilyHealthDTO(UserHealthDTO userHealth,FamilyMemberDTO familyMember){
        this.userHealth = userHealth;
        this.familyMember = familyMember;
    }
}
