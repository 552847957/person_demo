package com.wondersgroup.healthcloud.services.user.dto.member;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;

import org.apache.commons.lang3.StringUtils;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberMiniAPIEntity {
    private String uid;
    private String name;
    private String relation;
    @JsonProperty("relation_name")
    private String relationName;
    @JsonProperty("health_warning")
    private Boolean healthWarning;
    @JsonProperty("redirect_flag")
    private Integer redirectFlag;

    public FamilyMemberMiniAPIEntity() {
    }

    public FamilyMemberMiniAPIEntity(FamilyMember familyMember, RegisterInfo register) {
        this.uid = familyMember.getMemberId();
        this.relation = familyMember.getRelation();
        this.name = FamilyMemberRelation.getName(StringUtils.isEmpty(this.relation)?"0":this.relation, familyMember.getMemo());
        this.relationName = FamilyMemberRelation.getName(this.relation, familyMember.getMemo());
        if (StringUtils.isEmpty(name)) {
            this.name = register.getNickname();
        }
        if(StringUtils.isEmpty(relationName)){
            this.relationName = this.name;
        }
    }

    public FamilyMemberMiniAPIEntity(FamilyMember familyMember, AnonymousAccount anonymousAccount) {
        this.uid = familyMember.getMemberId();
        this.name = FamilyMemberRelation.getName(familyMember.getRelation(), familyMember.getMemo());
        this.relation = familyMember.getRelation();
        this.relationName = FamilyMemberRelation.getName(this.relation, familyMember.getMemo());
        if (StringUtils.isBlank(this.name)) {
            this.name = anonymousAccount.getName();
        }
    }

}
