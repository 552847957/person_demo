package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "DeptInfo")
public class DeptInfoR {


    private String hosOrgCode;
    private String topHosDeptCode;

    /**
     * 专家和普通科室传1
     * 专病科室传2
     * 为空则查所有
     */
    private String deptType;

    /**
     * 用于查询医生列表
     */
    private String hosDeptCode;


    public DeptInfoR() {
    }

    /**
     * 用于一级科室查询二级科室
     * @param hosOrgCode
     * @param oneDeptCode
     */
    public DeptInfoR(String hosOrgCode, String oneDeptCode) {
        this.hosOrgCode = hosOrgCode;
        this.topHosDeptCode = oneDeptCode;
        this.hosDeptCode="";
    }


    /**
     * 用于根据二级科室查询医生列表
     * @param hosOrgCode
     * @param topHosDeptCode
     * @param hosDeptCode
     */
    public DeptInfoR(String hosOrgCode, String topHosDeptCode, String hosDeptCode) {
        this.hosOrgCode = hosOrgCode;
        this.topHosDeptCode = topHosDeptCode;
        this.hosDeptCode = hosDeptCode;
    }


    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosDeptCode() {
        return hosDeptCode;
    }

    public void setHosDeptCode(String hosDeptCode) {
        this.hosDeptCode = hosDeptCode;
    }

    public String getTopHosDeptCode() {
        return topHosDeptCode;
    }

    public void setTopHosDeptCode(String topHosDeptCode) {
        this.topHosDeptCode = topHosDeptCode;
    }

    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }
}
