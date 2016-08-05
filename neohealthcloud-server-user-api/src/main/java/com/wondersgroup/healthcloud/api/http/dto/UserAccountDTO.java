package com.wondersgroup.healthcloud.api.http.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountDTO {

    private String uid;
    private String name;

    @JsonProperty("nick_name")
    private String nickName;
    private String mobile;
    private String avatar;
    private String idcard;
    private String age;
    private Integer gender;//1:男 2:女

    private Boolean verified;//是否实名认证

    private String talkId;
    private String talkPwd;
    private String tagid;

    private String medicarecard;//医保卡

    @JsonProperty("bind_personcard")
    private String bindPersoncard;//医养结合绑定的身份证


    public UserAccountDTO(Map<String, Object> user) {


    }

    public UserAccountDTO(RegisterInfo registerInfo) {
        this.uid = registerInfo.getRegisterid();
        this.name = registerInfo.getName();
        this.nickName = registerInfo.getNickname();
        this.mobile = registerInfo.getRegmobilephone();
        this.avatar = registerInfo.getHeadphoto();
        this.idcard = registerInfo.getPersoncard();
        if(StringUtils.isNotBlank(registerInfo.getGender()))
            this.gender = Integer.valueOf(registerInfo.getGender());
        this.age = "";
        this.verified = !"0".equals(registerInfo.getIdentifytype());
        if(this.verified){
            this.age = StringUtils.isBlank(this.idcard)?"":String.valueOf(IdcardUtils.getAgeByIdCard(this.idcard));
        }
        this.talkId = registerInfo.getTalkid();
        this.talkPwd = registerInfo.getTalkpwd();
        this.tagid = registerInfo.getTagid();
        this.medicarecard = registerInfo.getMedicarecard();
        this.bindPersoncard = registerInfo.getBindPersoncard();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getTalkId() {
        return talkId;
    }

    public void setTalkId(String talkId) {
        this.talkId = talkId;
    }

    public String getTalkPwd() {
        return talkPwd;
    }

    public void setTalkPwd(String talkPwd) {
        this.talkPwd = talkPwd;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getMedicarecard() {
        return medicarecard;
    }

    public void setMedicarecard(String medicarecard) {
        this.medicarecard = medicarecard;
    }

    public String getBindPersoncard() {
        return bindPersoncard;
    }

    public void setBindPersoncard(String bindPersoncard) {
        this.bindPersoncard = bindPersoncard;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
}
