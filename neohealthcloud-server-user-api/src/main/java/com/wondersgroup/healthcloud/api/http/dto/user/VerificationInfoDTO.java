package com.wondersgroup.healthcloud.api.http.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.utils.IdcardUtils;

/**
 * Created by longshasha on 16/5/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationInfoDTO {

    public static final String[] statusArray = {"认证成功", "审核中", "认证失败"};

    private String uid;
    private Boolean success;
    @JsonProperty("can_submit")
    private Boolean canSubmit;
    private String status;
    private String msg;
    private String name;
    private String idcard;

    public VerificationInfoDTO() {

    }

    public VerificationInfoDTO(String uid, JsonNode info) {
        this.uid = uid;
        if (info == null) {
            this.canSubmit = true;
        } else {
            Integer status = info.get("status").asInt();
            this.status = statusArray[status - 1];
            this.success = status == 1;
            this.canSubmit = status == 3;
            this.name = IdcardUtils.maskName(info.get("name").asText());
            this.idcard = IdcardUtils.maskIdcard(info.get("idcard").asText());
            this.msg = info.get("msg").isNull() ? null : info.get("msg").asText();
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getCanSubmit() {
        return canSubmit;
    }

    public void setCanSubmit(Boolean canSubmit) {
        this.canSubmit = canSubmit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
}
