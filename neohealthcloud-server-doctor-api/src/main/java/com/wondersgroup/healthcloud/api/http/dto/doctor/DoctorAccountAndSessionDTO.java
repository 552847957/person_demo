package com.wondersgroup.healthcloud.api.http.dto.doctor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;

/**
 * Created by longshasha on 16/8/1.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorAccountAndSessionDTO {
    private String uid;
    private String token;
    private String key;
    private DoctorAccountDTO info;

    public DoctorAccountAndSessionDTO() {
    }

    public DoctorAccountAndSessionDTO(AccessToken accessToken) {
        this.uid = accessToken.getUid();
        this.token = accessToken.getToken();
        this.key = accessToken.getKey();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DoctorAccountDTO getInfo() {
        return info;
    }

    public void setInfo(DoctorAccountDTO info) {
        this.info = info;
    }
}