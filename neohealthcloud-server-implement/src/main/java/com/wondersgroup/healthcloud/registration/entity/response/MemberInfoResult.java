package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/18.
 */
@XmlRootElement(name = "Result")
public class MemberInfoResult {

    /**
     * 用户代码
     */
    private String userId;
    /**
     * 成员身份证号码
     */
    private String papersNum;
    /**
     * 成员姓名
     */
    private String memberName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPapersNum() {
        return papersNum;
    }

    public void setPapersNum(String papersNum) {
        this.papersNum = papersNum;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
