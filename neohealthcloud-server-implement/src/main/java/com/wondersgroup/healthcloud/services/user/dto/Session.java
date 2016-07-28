package com.wondersgroup.healthcloud.services.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p/>
 * Created by zhangzhixiu on 15/11/26.
 */
@Data
public final class Session {

    private String accessToken;
    private String userId;
    private String secret;
    private Boolean isValid;
    private Boolean isDoctor;

    public Session() {
    }

    public Session(String accessToken, JsonNode node) {
        this.accessToken = accessToken;
        this.userId = node.has("userid") ? node.get("userid").asText() : null;
        this.isValid = "true".equals(node.get("isValid").asText());
        this.secret = node.has("key") ? node.get("key").asText() : "";
    }

    public static Session guest(String accessToken, String secret) {
        Session session = new Session();
        session.setAccessToken(accessToken);
        session.setSecret(secret);
        session.setIsValid(true);
        session.setIsDoctor(false);
        return session;
    }

    public Boolean isGuest() {
        return userId == null;
    }


}