package com.wondersgroup.healthcloud.api.http.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountDTO {

    private String uid;
    private String nickName;
    private String mobile;
    private String avatar;
    private String name;
    private String idCard;
    private String age;
    private Integer gender;//1:男 2:女



    private Boolean verified;//是否实名认证

    private String talkId;
    private String talkPwd;
    private String tagid;

    private String medicarecard;//医保卡
    @JsonProperty("bind_personcard")
    private String bindPersoncard;//医养结合绑定的身份证


    public UserAccountDTO(Map<String, Object> user) {

    }
}
