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

    private ThirdDoctor thirdDoctor;

    public Session() {
    }

    public Session(String accessToken, JsonNode node) {
        this.accessToken = accessToken;
        this.userId = node.has("userid") ? node.get("userid").asText() : null;
        this.isValid = "true".equals(node.get("isValid").asText());
        this.secret = node.has("key") ? node.get("key").asText() : "";

        this.isDoctor = node.has("type")?"0".equals(node.get("type").asText()):false;
        if(this.isDoctor){
            this.thirdDoctor.hisHospitalId = node.has("hisHospitalId")?node.get("hisHospitalId").asText():null;
            this.thirdDoctor.hisDoctorId = node.has("hisDoctorId")?node.get("hisDoctorId").asText():null;
            this.thirdDoctor.hisNum = node.has("hisNum")?node.get("hisNum").asText():null;
        }

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

    public class ThirdDoctor {
        public String hisHospitalId;
        public String hisDoctorId;
        public String hisNum;
    }


}