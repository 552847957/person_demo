package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 * 注册或修改用户的请求类
 */
@XmlRootElement(name = "UserInfo")
public class UserInfoR {

    /**
     * 操作类型 not null
     * 0新增 1修改
     */
    private String operType;
    /**
     * 平台用户代码
     * 当对用户信息进行修改时，填写平台用户代码；
     * 如果没有填写，则根据证件类型和证件号码更新用户信息
     */
    private String platformUserId;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 证件类型
     */
    private String userCardType;
    /**
     * 证件号码
     */
    private String userCardId;
    /**
     * 手机号码
     */
    private String userPhone;
    /**
     * 用户性别
     */
    private String userSex;
    /**
     * 用户出生日期
     * 格式：yyyy-MM-dd
     */
    private String userBD;
    /**
     * 用户密码string(8)
     */
    private String userPwd;
    private String userContAdd;
    private String userEmail;
    private String bindType;
    private String identifyCode;
    private String isDefaultWeixin;
    /**
     * 诊疗卡卡号
     * 有卡预约必需
     */
    private String mediCardId;
    /**
     * 诊疗卡类型
     * 0:无卡，初诊病人
     * 1：社保卡（医保卡）
     * 2：上海医联卡
     */
    private String mediCardIdType;

    /**
     * 用户登录名
     */
    private String userLoginName;

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCardType() {
        return userCardType;
    }

    public void setUserCardType(String userCardType) {
        this.userCardType = userCardType;
    }

    public String getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(String userCardId) {
        this.userCardId = userCardId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserBD() {
        return userBD;
    }

    public void setUserBD(String userBD) {
        this.userBD = userBD;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserContAdd() {
        return userContAdd;
    }

    public void setUserContAdd(String userContAdd) {
        this.userContAdd = userContAdd;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }

    public String getIsDefaultWeixin() {
        return isDefaultWeixin;
    }

    public void setIsDefaultWeixin(String isDefaultWeixin) {
        this.isDefaultWeixin = isDefaultWeixin;
    }

    public String getMediCardId() {
        return mediCardId;
    }

    public void setMediCardId(String mediCardId) {
        this.mediCardId = mediCardId;
    }

    public String getMediCardIdType() {
        return mediCardIdType;
    }

    public void setMediCardIdType(String mediCardIdType) {
        this.mediCardIdType = mediCardIdType;
    }

    public String getUserLoginName() {
        return userLoginName;
    }

    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }
}
