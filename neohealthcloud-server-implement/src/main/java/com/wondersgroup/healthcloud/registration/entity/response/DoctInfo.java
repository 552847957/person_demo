package com.wondersgroup.healthcloud.registration.entity.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/22.
 */
@XmlRootElement(name = "Result")
public class DoctInfo {

    private String hosOrgCode;
    private String hosName;
    private String hosDeptCode;
    private String deptName;
    private String hosDoctCode;
    private String doctName;

    /**
     * 医生职称
     */
    private String doctTile;

    /**
     * 医生简介
     */
    private String doctInfo;

    /**
     * 诊间位置
     */
    private String doctAdd;

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosName() {
        return hosName;
    }

    public void setHosName(String hosName) {
        this.hosName = hosName;
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

    public String getHosDoctCode() {
        return hosDoctCode;
    }

    public void setHosDoctCode(String hosDoctCode) {
        this.hosDoctCode = hosDoctCode;
    }

    public String getDoctName() {
        return doctName;
    }

    public void setDoctName(String doctName) {
        this.doctName = doctName;
    }

    public String getDoctTile() {
        return doctTile;
    }

    public void setDoctTile(String doctTile) {
        this.doctTile = doctTile;
    }

    public String getDoctInfo() {
        return doctInfo;
    }

    public void setDoctInfo(String doctInfo) {
        this.doctInfo = doctInfo;
    }

    public String getDoctAdd() {
        return doctAdd;
    }

    public void setDoctAdd(String doctAdd) {
        this.doctAdd = doctAdd;
    }
}
