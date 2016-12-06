package com.wondersgroup.healthcloud.registration.entity.request;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by longshasha on 16/5/23.
 */
@XmlRootElement(name = "HosInfo")
public class HosInfoR {

    private String hosOrgCode;

    public HosInfoR() {
    }

    public HosInfoR(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }

    public String getHosOrgCode() {
        return hosOrgCode;
    }

    public void setHosOrgCode(String hosOrgCode) {
        this.hosOrgCode = hosOrgCode;
    }
}
