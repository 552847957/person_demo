package com.wondersgroup.healthcloud.services.user.dto.member;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.common.image.utils.ImagePath;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberAccess;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberAPIEntity {

    private String uid;
    private String avatar;
    private String name;
    private String memo;
    private String mobile;
    private Boolean verified;
    private String relation;
    @JsonProperty("relation_name")
    private String relationName;
    @JsonProperty("record_readable")
    private Boolean recordReadable;//对方对本人的设定
    @JsonProperty("record_readable_setting")
    private Boolean recordReadableSetting;//本人对对方的设定
    private Integer mood;
    @JsonProperty("schedule_is_complete")
    private Boolean scheduleIsComplete;
    @JsonProperty("health_warning")
    private Boolean healthWarning;
    @JsonProperty("is_anonymous")
    private Boolean isAnonymous;
    @JsonProperty("redirect_flag")
    private Integer redirectFlag;
    private String label;
    private String labelColor;
    private String gender;
    private Boolean isChild;//当是匿名账户的时候 是否是儿童

    public FamilyMemberAPIEntity() {
    }

    public FamilyMemberAPIEntity(FamilyMember familyMember, RegisterInfo register) {
        this.uid = familyMember.getMemberId();
        this.avatar = register.getHeadphoto() == null ? null : (register.getHeadphoto() + ImagePath.avatarPostfix());
        this.mobile = register.getRegmobilephone();
        this.name = register.getNickname();
        this.memo = familyMember.getMemo();
        this.verified = !"0".equals(register.getIdentifytype());
        this.relation = familyMember.getRelation();
        this.relationName = FamilyMemberRelation.getName(this.relation, familyMember.getMemo());
        this.recordReadableSetting = FamilyMemberAccess.recordReadable(familyMember.getAccess());
        this.isAnonymous = false;
        this.gender = register.getGender();
    }

    public FamilyMemberAPIEntity(FamilyMember familyMember, AnonymousAccount anonymousAccount) {
        this.uid = familyMember.getMemberId();
        this.name = anonymousAccount.getName();
        this.memo = familyMember.getMemo();
        this.verified = anonymousAccount.getIdcard() != null;
        this.relation = familyMember.getRelation();
        this.relationName = FamilyMemberRelation.getName(this.relation, familyMember.getMemo());
        this.recordReadableSetting = FamilyMemberAccess.recordReadable(familyMember.getAccess());
        this.isAnonymous = true;
        this.gender = null == anonymousAccount.getIdcard()?"":(anonymousAccount.getIdcard().charAt(16)%2==1?"1":"2");
        this.isChild = anonymousAccount.getIsChild() == null ? false : anonymousAccount.getIsChild();
    }

}
