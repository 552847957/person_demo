package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 */
@XmlRootElement(name = "Result")
public class TwoDeptInfo {

    private String hosOrgCode;
    private String topHosDeptCode;
    private String hosDeptCode;
    private String deptName;
    private String deptDesc;

    /**
     * 专家和普通科室为1
     * 专病科室2
     */
    private String deptType;

    public TwoDeptInfo() {
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptDesc() {
        return deptDesc;
    }

    public void setDeptDesc(String deptDesc) {
        this.deptDesc = deptDesc;
    }

    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }
}
