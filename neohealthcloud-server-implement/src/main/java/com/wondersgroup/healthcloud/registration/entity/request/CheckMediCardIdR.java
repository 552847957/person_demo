package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/11/15.
 */
@XmlRootElement(name = "HospitalInfo")
public class CheckMediCardIdR {

    private String hosOrgCode;//可为空 其他都必填

    /**
     * 诊疗卡号
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
     * 用户姓名
     */
    private String userName;

    /**
     * 证件号码
     */
    private String userCardId;

    /**
     * 证件类型
     * 1：居民身份证
     * 2：居民户口簿
     * 3：护照
     * 4：军官证（士兵证）
     * 5：驾驶执照
     * 6：港澳居民来往内地通行证
     * 7：台湾居民来往内地通行证

     */
    private String userCardType = "1";

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCardId() {
        return userCardId;
    }

    public void setUserCardId(String userCardId) {
        this.userCardId = userCardId;
    }

    public String getUserCardType() {
        return userCardType;
    }

    public void setUserCardType(String userCardType) {
        this.userCardType = userCardType;
    }
}
