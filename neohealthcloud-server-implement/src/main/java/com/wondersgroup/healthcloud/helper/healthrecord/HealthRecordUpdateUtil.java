package com.wondersgroup.healthcloud.helper.healthrecord;

import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * <p>
 * Created by zhangzhixiu on 8/31/16.
 */
@Component
public class HealthRecordUpdateUtil {//async callback to improve

    @Value("${internal.api.service.healthrecord.url}")
    private String host;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    public void onVerificationSuccess(String idcard, String name) {
        Request request = new RequestBuilder().post().url(host + "/api/healthRecord/updateRecord").body(String.format("{\"idc\":\"%s\",\"name\":\"%s\"}", idcard, name)).build();
        httpRequestExecutorManager.newCall(request).run();
    }

    public void onMedicareBindSuccess(String idcard, String medicareCard, String name) {
        Request request = new RequestBuilder().post().url(host + "/api/healthRecord/bindMedicarecard").body(String.format("{\"idc\":\"%s\",\"medicareCard\":\"%s\",\"name\":\"%s\"}", idcard, medicareCard, name)).build();
        httpRequestExecutorManager.newCall(request).run();
    }

    public void unBindMedicareCard(String idcard) {
        //todo
    }
}
