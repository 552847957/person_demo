package com.wondersgroup.healthcloud.helper.push.api;

import com.squareup.okhttp.*;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.utils.JsonConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    private static final Logger logger = LoggerFactory.getLogger(PushClientWrapper.class);

    private static class PushCallback implements Callback {
        @Override
        public void onFailure(Request request, IOException e) {
            logger.error(e.getMessage(), e);
        }

        @Override
        public void onResponse(Response response) throws IOException {
        }
    }

    @Value("${internal.api.service.message.url}")
    private String baseUrl = "http://localhost:8001/neohealthcloud-internal/message";

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    public Boolean pushToAll(AppMessage message, String area) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/all").newBuilder().addQueryParameter("area", area).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
//        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
//        return wrapper.convertBody().get("code").asInt() == 0;
        httpRequestExecutorManager.newCall(builder.build()).callback(new PushCallback()).run();
        return true;
    }

    public Boolean pushToAlias(AppMessage message, String alias) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/single").newBuilder().addQueryParameter("alias", alias).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
//        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
//        return wrapper.convertBody().get("code").asInt() == 0;
        httpRequestExecutorManager.newCall(builder.build()).callback(new PushCallback()).run();
        return true;
    }

    public Boolean pushToTags(AppMessage message, String area, List<String> tags) {
        Request.Builder builder = new Request.Builder();
        builder.url(HttpUrl.parse(baseUrl + "/push/tag").newBuilder().addQueryParameter("area", area).addQueryParameter("tags", StringUtils.join(tags, ",")).build()).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonConverter.toJson(message)));
//        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(builder.build()).run().as(JsonNodeResponseWrapper.class);
//        return wrapper.convertBody().get("code").asInt() == 0;
        httpRequestExecutorManager.newCall(builder.build()).callback(new PushCallback()).run();
        return true;
    }


    public static void main(String... args) throws Exception {
        PushClientWrapper wrapper = new PushClientWrapper();
        wrapper.httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());
        wrapper.pushToAlias(AppMessage.Builder.init().title("我的问答").content("您有一条新的问诊提问，点击查看").isDoctor()
                .type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question("1")).persistence().build(), "757b0bb2d734401d931bde49f851eda9");
        Thread.sleep(100000);
    }
}
