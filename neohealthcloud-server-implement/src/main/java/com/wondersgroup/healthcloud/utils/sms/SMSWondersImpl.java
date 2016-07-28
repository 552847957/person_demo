package com.wondersgroup.healthcloud.utils.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;

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
 * Created by zhangzhixiu on 16/3/6.
 */
public class SMSWondersImpl implements SMS {

    private static final String token = "ahincKKBq3H9YGiyMUPRW6POHyAUmp";
    private static final String url = "http://172.18.11.164:8080/wondersSMS/sendSMS";
    private static final String sid = "jkja";

    private HttpRequestExecutorManager manager;

    public void setHttpManager(HttpRequestExecutorManager manager) {
        this.manager = manager;
    }

    @Override
    public void send(String mobile, String content) {
        Request request = new RequestBuilder().get().url(url).params(new String[]{"content", content, "phonelist", mobile, "taskId", "00000000000000", "token", token, "sid", sid}).build();
        JsonNodeResponseWrapper result = (JsonNodeResponseWrapper) manager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode node = result.convertBody();
        if (!"0".equals(node.get("code").asText())) {
            throw new SMSFailureException();
        }
    }

    public static void main(String... args) {
        HttpRequestExecutorManager manager = new HttpRequestExecutorManager(new OkHttpClient());
        SMSWondersImpl sms = new SMSWondersImpl();
        sms.setHttpManager(manager);
        sms.send("15900809844", "test");
    }
}
