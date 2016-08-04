package com.wondersgroup.healthcloud.api.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;

/**
 * Created by longshasha on 16/8/4.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountAndSessionDTO {

    private String uid;
    private String token;
    private String key;
    private String userType;

    private UserAccountDTO info;

    public UserAccountAndSessionDTO() {
    }

    public UserAccountAndSessionDTO(AccessToken accessToken) {
        this.uid = accessToken.getUid();
        this.token = accessToken.getToken();
        this.key = accessToken.getKey();
        this.userType = accessToken.getUserType();
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UserAccountDTO getInfo() {
        return info;
    }

    public void setInfo(UserAccountDTO info) {
        this.info = info;
    }
}
