package com.wondersgroup.healthcloud.services.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Created by zhangzhixiu on 15/12/17.
 */
@Component("sessionUtil")
public class SessionUtil {

    private HttpWdUtils httpWdUtils;


    public Session get(String accessToken) {
        JsonNode result = httpWdUtils.getSession(accessToken);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new Session(accessToken, result.get("data"));
        } else {
            return null;
        }
    }

    public Session createGuest() {
        JsonNode result = httpWdUtils.guestLogin();
        Boolean success = result.get("success").asBoolean();
        if (success) {
            String token = result.get("session_token").asText();
            String key = IdGen.uuid();
            httpWdUtils.addSessionExtra(token, key);
            return Session.guest(token, key);
        } else {
            return null;
        }
    }

    @Autowired
    public void setHttpWdUtils(HttpWdUtils httpWdUtils) {
        this.httpWdUtils = httpWdUtils;
    }
}
