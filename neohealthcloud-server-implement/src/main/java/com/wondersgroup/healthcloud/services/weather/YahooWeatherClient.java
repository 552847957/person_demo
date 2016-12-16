package com.wondersgroup.healthcloud.services.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * 雅虎天气API获取类, 获取后处理数据并缓存
 * <p>
 * Created by zhangzhixiu on 29/11/2016.
 */
@Component
public class YahooWeatherClient {

    private static final Logger logger = LoggerFactory.getLogger(YahooWeatherClient.class);

    private static final String url = "https://query.yahooapis.com/v1/public/yql";
    private static final ObjectMapper mapper = new ObjectMapper();

    private HttpRequestExecutorManager httpRequestExecutorManager;


    public JsonNode channel(String woeid) {
        try {
            JsonNode result = query(String.format("select * from weather.forecast where woeid=%s and u='c'", woeid));
            return result.get("query").get("results").get("channel");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private JsonNode query(String q) {
        Request request = new RequestBuilder().get().url(url).param("q", q).param("format", "json").build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        logger.info(result.toString());
        return result;
    }

    @Autowired
    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }
}