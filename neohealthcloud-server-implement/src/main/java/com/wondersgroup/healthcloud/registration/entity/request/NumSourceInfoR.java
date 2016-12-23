package com.wondersgroup.healthcloud.registration.entity.request;



import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.registration.entity.response.DoctInfo;
import com.wondersgroup.healthcloud.registration.entity.response.TwoDeptInfo;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "OrderInfo")
public class NumSourceInfoR {

    private String hosOrgCode;//医院代码
    private String topHosDeptCode;//一级科室代码
    private String hosDeptCode;//二级科室代码
    private String hosDoctCode;//指系统医生代码，如为空，则查询所有医生的号源信息
    private String registerType;//就诊类型  1|2|3  1专家.2专病3.普通 为空查询所有，为3时不能填医生ID，普通类型没有医生ID
    private String startTime;//2012-11-5
    private String endTime;//2012-11-11

    public NumSourceInfoR() {

    }

    public NumSourceInfoR(TwoDeptInfo twoDeptInfo,DoctInfo doctInfo){
        this.hosOrgCode = twoDeptInfo.getHosOrgCode();
        this.topHosDeptCode = twoDeptInfo.getTopHosDeptCode();
        this.hosDoctCode = doctInfo.getHosDoctCode();
        this.hosDeptCode = twoDeptInfo.getHosDeptCode();
        this.startTime = DateFormatter.dateFormat(new Date());
        this.endTime = DateFormatter.dateFormat(DateUtils.addDay(new Date(), 14));
    }

    public NumSourceInfoR(TwoDeptInfo twoDeptInfo){
        this.hosOrgCode = twoDeptInfo.getHosOrgCode();
        this.topHosDeptCode = twoDeptInfo.getTopHosDeptCode();
        this.hosDeptCode = twoDeptInfo.getHosDeptCode();

        this.startTime = DateFormatter.dateFormat(new Date());
        this.endTime = DateFormatter.dateFormat(DateUtils.addDay(new Date(), 14));
    }


    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getTopHosDeptCode() {
        return topHosDeptCode;
    }

    public void setTopHosDeptCode(String topHosDeptCode) {
        this.topHosDeptCode = topHosDeptCode;
    }

    public String getHosDeptCode() {
        return hosDeptCode;
    }

    public void setHosDeptCode(String hosDeptCode) {
        this.hosDeptCode = hosDeptCode;
    }

    public String getHosDoctCode() {
        return hosDoctCode;
    }

    public void setHosDoctCode(String hosDoctCode) {
        this.hosDoctCode = hosDoctCode;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
