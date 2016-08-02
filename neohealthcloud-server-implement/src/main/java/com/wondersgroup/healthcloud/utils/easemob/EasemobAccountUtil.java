package com.wondersgroup.healthcloud.utils.easemob;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

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
 * Created by zhangzhixiu on 16/2/24.
 */
@Component
public class EasemobAccountUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JsonFactory jsonFactory = mapper.getFactory();

    private static final String baseUrl = "http://a1.easemob.com/wondersgroup-health/healthcloud";

    private String[] authorizationHeader = null;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    private synchronized void refreshToken() {
        String body = buildFlatRequestBody(new String[]{"grant_type", "client_credentials", "client_id", "YXA6h_sc8DRPEeWKyBMkSeyCAw", "client_secret", "YXA6lrTgQDWwpAuZAhwfPWjG2Puxzyk"});
        String[] headers = new String[]{"Content-Type", "application/json"};
        Request request = new RequestBuilder().post().url(baseUrl + "/token").body(body).headers(headers).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        authorizationHeader = new String[]{"authorization", "Bearer " + result.get("access_token").asText(), "Content-Type", "application/json"};
    }

    private String[] getToken() {
        return authorizationHeader;
    }

    private String buildFlatRequestBody(String[] parameter) {
        try (StringWriter writer = new StringWriter(); JsonGenerator g = jsonFactory.createGenerator(writer)) {
            g.writeStartObject();
            for (int i = 0; i < parameter.length / 2; i++) {
                g.writeStringField(parameter[i * 2], parameter[i * 2 + 1]);
            }
            g.writeEndObject();
            g.flush();
            return writer.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Boolean register(String talkid, String password, String nickname) {
        JsonNodeResponseWrapper response = request("post", "/user", buildFlatRequestBody(new String[]{"username", talkid, "password", password, "nickname", nickname}));
        return response.code() == 200;
    }

    public Boolean validAccount(String talkId) {
        JsonNodeResponseWrapper response = request("get", String.format("/user/%s", talkId), null);
        return response.code() == 200;
    }

    private JsonNodeResponseWrapper request(String method, String path, String body) {
        JsonNodeResponseWrapper response = _request(method, path, body);
        if (response.code() == 401) {
            refreshToken();
            return _request(method, path, body);
        } else {
            return response;
        }
    }

    private JsonNodeResponseWrapper _request(String method, String path, String body) {
        JsonNodeResponseWrapper response = null;
        if ("get".equals(method)) {
            Request request = new RequestBuilder().get().url(baseUrl + path).headers(getToken()).build();
            response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        } else if ("post".equals(method)) {
            Request request = new RequestBuilder().post().url(baseUrl + path).body(body).headers(getToken()).build();
            response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        } else if ("delete".equals(method)) {

        } else {
            throw new RuntimeException("unsupported method");
        }
        return response;
    }
}
