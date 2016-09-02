package com.wondersgroup.healthcloud.api.http.dto.doctor.medicalcircle;

/**
 * Created by Yoda on 2015/9/4.
 */
public class DocSearchResultAPIEntity {
    private String name;
    private String avatar;
    private String hospital;
    private Boolean is_attention;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public Boolean getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(Boolean is_attention) {
        this.is_attention = is_attention;
    }

}
