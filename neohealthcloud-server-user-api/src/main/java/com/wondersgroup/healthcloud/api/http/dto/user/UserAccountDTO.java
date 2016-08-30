package com.wondersgroup.healthcloud.api.http.dto.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
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


    private String height;
    private String weight;
    private String waist;

    @JsonUnwrapped(prefix = "address_")
    private AddressDTO addressDTO;

    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");


    public UserAccountDTO(Map<String, Object> user) {
        if(user!=null) {
            this.uid = user.get("registerid") == null ? "" : user.get("registerid").toString();
            this.name = user.get("name") == null ? "" : user.get("name").toString();
            this.nickName = user.get("nickname") == null ? "" : user.get("nickname").toString();
            this.mobile = user.get("regmobilephone") == null ? "" : user.get("regmobilephone").toString();
            this.avatar = user.get("headphoto") == null ? "" : user.get("headphoto").toString();
            this.idcard = user.get("personcard") == null ? "" : user.get("personcard").toString();
            String gender = user.get("gender") == null ? "" : user.get("gender").toString();
            if (StringUtils.isNotBlank(gender))
                this.gender = Integer.valueOf(gender);

            this.age = user.get("age") == null ? "" : user.get("age").toString();

            this.height = user.get("height") == null ? "" : user.get("height").toString();
            this.weight = user.get("weight") == null ? "" : decimalFormat.format(user.get("weight")).toString();
            this.waist = user.get("waist") == null ? "" : decimalFormat.format(user.get("waist")).toString();

            String identifytype = user.get("identifytype") == null ? "" : user.get("identifytype").toString();
            this.verified = !"0".equals(identifytype);
            if (this.verified) {
                this.age = StringUtils.isBlank(this.idcard) ? "" : String.valueOf(IdcardUtils.getAgeByIdCard(this.idcard));
                this.gender = Integer.valueOf(IdcardUtils.getGenderByIdCard(this.idcard));
            }
            this.talkId = user.get("talkid") == null ? "" : user.get("talkid").toString();
            this.talkPwd = user.get("talkpwd") == null ? "" : user.get("talkpwd").toString();
            this.tagid = user.get("tagid") == null ? "" : user.get("tagid").toString();
            this.medicarecard = user.get("medicarecard") == null ? "" : user.get("medicarecard").toString();
            this.bindPersoncard = user.get("bind_personcard") == null ? "" : user.get("bind_personcard").toString();
        }
    }

    public UserAccountDTO(RegisterInfo registerInfo,UserInfo userInfo) {
        this.uid = registerInfo.getRegisterid();
        this.name = registerInfo.getName()==null?"":registerInfo.getName();
        this.nickName = registerInfo.getNickname()==null?"":registerInfo.getNickname();
        this.mobile = registerInfo.getRegmobilephone()==null?"":registerInfo.getRegmobilephone();
        this.avatar = registerInfo.getHeadphoto()==null?"":registerInfo.getHeadphoto();
        this.idcard = registerInfo.getPersoncard()==null?"":registerInfo.getPersoncard();
        if(StringUtils.isNotBlank(registerInfo.getGender()))
            this.gender = Integer.valueOf(registerInfo.getGender());
        this.age = "";

        if(userInfo!=null){
            this.age = userInfo.getAge()==null?"":String.valueOf(userInfo.getAge());
            this.height = userInfo.getHeight()==null?"":String.valueOf(userInfo.getHeight());
            this.weight = userInfo.getWeight()==null?"":decimalFormat.format(userInfo.getWeight());
            this.waist = userInfo.getWaist()== null?"":decimalFormat.format(userInfo.getWaist());
        }
        this.verified = !"0".equals(registerInfo.getIdentifytype());
        if(this.verified){
            this.age = StringUtils.isBlank(this.idcard)?"":String.valueOf(IdcardUtils.getAgeByIdCard(this.idcard));
        }


        this.talkId = registerInfo.getTalkid()==null?"":registerInfo.getTalkid();
        this.talkPwd = registerInfo.getTalkpwd()==null?"":registerInfo.getTalkpwd();
        this.tagid = registerInfo.getTagid()==null?"":registerInfo.getTagid();
        this.medicarecard = registerInfo.getMedicarecard()==null?"": registerInfo.getMedicarecard();
        this.bindPersoncard = registerInfo.getBindPersoncard()==null?"":registerInfo.getBindPersoncard();
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

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWaist() {
        return waist;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public AddressDTO getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
        this.addressDTO = addressDTO;
    }
}
