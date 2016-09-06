package com.wondersgroup.healthcloud.helper.push.api;

import com.squareup.okhttp.*;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.common.http.utils.JsonConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
 * Created by zhangzhixiu on 8/17/16.
 */
@Component
public class PushClientWrapper {//todo(zzx) can convert the blocked request to async request to imporve the performance.

    @Value("${internal.api.service.message.url}")
    private String baseUrl = "http://localhost:8001/neohealthcloud-internal/message";

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    public Boolean pushToAll(AppMessage message, String area) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/all").newBuilder().addQueryParameter("area", area).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
        return wrapper.convertBody().get("code").asInt() == 0;
    }

    public Boolean pushToAlias(AppMessage message, String alias) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/single").newBuilder().addQueryParameter("alias", alias).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
        return wrapper.convertBody().get("code").asInt() == 0;
    }

    public Boolean pushToTags(AppMessage message, String area, List<String> tags) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/tag").newBuilder().addQueryParameter("area", area).addQueryParameter("tags", StringUtils.join(tags, ",")).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
        return wrapper.convertBody().get("code").asInt() == 0;
    }


    public static void main(String... args) {
        PushClientWrapper wrapper = new PushClientWrapper();
        wrapper.httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());
        wrapper.pushToAlias(AppMessage.Builder.init().title("我的问答").content("您有一条新的问诊提问，点击查看").isDoctor()
                .type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question("1")).persistence().build(), "757b0bb2d734401d931bde49f851eda9");
    }
}
