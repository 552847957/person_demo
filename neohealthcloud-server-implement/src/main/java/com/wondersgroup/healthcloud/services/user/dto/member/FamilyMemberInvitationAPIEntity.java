package com.wondersgroup.healthcloud.services.user.dto.member;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMemberInvitation;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberInvitationAPIEntity {

    private static final String[] statusArray = {"申请中", "已通过", "已拒绝"};
    private String id;
    private String avatar;
    private String nickname;
    private String memo;
    private String mobile;
    private Boolean todo;
    private String status;
    private String relation;
    @JsonProperty("relation_name")
    private String relationName;

    public FamilyMemberInvitationAPIEntity() {
    }

    public FamilyMemberInvitationAPIEntity(FamilyMemberInvitation invitation, String uid, RegisterInfo register,int isAnonymous) {
        this.id = invitation.getId();
        this.avatar = register.getHeadphoto() + ImagePath.avatarPostfix();
        this.mobile = register.getRegmobilephone();
        this.nickname = register.getNickname();
        this.memo = invitation.getMemo();
        Boolean isSelf = uid.equals(invitation.getUid());
        this.todo = (!isSelf) && "0".equals(invitation.getStatus());
        this.status = statusArray[Integer.valueOf(invitation.getStatus())];
        this.relation = isSelf ? invitation.getRelation() : FamilyMemberRelation.getOppositeRelation(invitation.getRelation(), register.getGender());
        this.relationName = isSelf ? FamilyMemberRelation.getName(relation, invitation.getRelationName()) : FamilyMemberRelation.getName(relation);
        if(1==isAnonymous) this.status="身份审核成功";
    }

}
