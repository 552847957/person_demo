package com.wondersgroup.healthcloud.api.http.controllers.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.weather.WeatherCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * Created by zhangzhixiu on 06/12/2016.
 */
@RestController
@RequestMapping(path = "/api/weather")
public class WeatherController {

    @Autowired
    private WeatherCache cache;

    @GetMapping(path = "/brief")
    public JsonResponseEntity<JsonNode> brief(@RequestHeader("main-area") String mainArea,
                                              @RequestHeader(value = "spec-area", required = false) String specArea) {
        String result = cache.get(WeatherCache.Type.BRIEF, StringUtils.isEmpty(specArea) ? mainArea : specArea);

        if (StringUtils.isBlank(result)) {
            result = cache.get(WeatherCache.Type.BRIEF, mainArea);
        }

        JsonResponseEntity<JsonNode> response = new JsonResponseEntity<>();
        response.setData(JsonConverter.toJsonNode(result));
        return response;
    }

    @GetMapping(path = "/all")
    public JsonResponseEntity<JsonNode> all(@RequestHeader("main-area") String mainArea,
                                            @RequestHeader(value = "spec-area", required = false) String specArea) {
        String result = cache.get(WeatherCache.Type.ALL, StringUtils.isEmpty(specArea) ? mainArea : specArea);

        if (StringUtils.isBlank(result)) {
            result = cache.get(WeatherCache.Type.ALL, mainArea);
        }

        JsonResponseEntity<JsonNode> response = new JsonResponseEntity<>();
        response.setData(JsonConverter.toJsonNode(result));
        return response;
    }
}
