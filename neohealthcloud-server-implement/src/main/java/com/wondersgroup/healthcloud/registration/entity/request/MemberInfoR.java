package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/17.
 * 注册或修改成员信息
 */
@XmlRootElement(name = "MemberInfo")
public class MemberInfoR {

    /**
     * 操作类型 0新增 1修改
     * 1参数暂时作废，根据医联要去，家庭卡绑卡后禁止修改，如需要修改需联系客服
     */
    private String operType;
    /**
     * 平台用户代码
     * 当对用户成员信息进行新增或者修改时，填写平台用户代码；表示挂在哪个用户下。
     */
    private String userId;

    /**
     * 成员代码 修改成员信息时必填
     */
    private String memberId;

    private String memberName;
    private String papersType;
    private String papersNum;
    private String userSex;
    private String userPhone;
    private String userAdress;
    private String userEmail;
    /**
     * 用户状态
     * 0：正常  1：注销
     */
    private String userState;
    /**
     * 管理员id   供管理员添加成员  ????
     */
    private String manageId;
    /**
     * 诊疗卡卡号
     */
    private String mediCardId;
    /**
     * 诊疗卡类型
     * 0:无卡，初诊病人
     * 1：社保卡（医保卡）
     * 2：上海医联卡
     */
    private String mediCardIdType;

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPapersType() {
        return papersType;
    }

    public void setPapersType(String papersType) {
        this.papersType = papersType;
    }

    public String getPapersNum() {
        return papersNum;
    }

    public void setPapersNum(String papersNum) {
        this.papersNum = papersNum;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAdress() {
        return userAdress;
    }

    public void setUserAdress(String userAdress) {
        this.userAdress = userAdress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getManageId() {
        return manageId;
    }

    public void setManageId(String manageId) {
        this.manageId = manageId;
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
}
