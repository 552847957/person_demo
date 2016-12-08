package com.wondersgroup.healthcloud.services.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>
 * Created by zhangzhixiu on 29/11/2016.
 */
@Component
public class HeWeatherClient {

    private static final Logger logger = LoggerFactory.getLogger(HeWeatherClient.class);

    private static final String url = "https://free-api.heweather.com/v5";

    private HttpRequestExecutorManager httpRequestExecutorManager;

    public JsonNode weather(String hecode) {
        Request request = new RequestBuilder().get().url(url + "/weather").param("city", hecode == null ? "shanghai" : hecode).param("key", "5b9092f4d8594ff4ad55bdaac1127e75").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        logger.info(result.toString());
        return result;
    }

    public void now() {
        Request request = new RequestBuilder().get().url(url + "/now").param("city", "shanghai").param("key", "5b9092f4d8594ff4ad55bdaac1127e75").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
    }

    public void forecast() {
        Request request = new RequestBuilder().get().url(url + "/forecast").param("city", "shanghai").param("key", "5b9092f4d8594ff4ad55bdaac1127e75").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        System.out.println(result.toString());
    }

    public void hourly() {
        Request request = new RequestBuilder().get().url(url + "/hourly").param("city", "shanghai").param("key", "5b9092f4d8594ff4ad55bdaac1127e75").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        System.out.println(result.toString());
    }

    public String suggestion() {
        Request request = new RequestBuilder().get().url(url + "/suggestion").param("city", "shanghai").param("key", "5b9092f4d8594ff4ad55bdaac1127e75").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        return result.get("HeWeather5").get(0).get("suggestion").toString();
    }

    @Autowired
    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }

    public static void main(String... args) {
        HeWeatherClient client = new HeWeatherClient();
        client.httpRequestExecutorManager = new HttpRequestExecutorManager(new OkHttpClient());
        System.out.println(client.weather(null));
    }
}
