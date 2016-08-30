package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by Jeffrey on 16/8/29.
 */
@RestController
@RequestMapping("api/exam")
public class ExamController {

    private static final String requestStationNearby = "%s/api/exam/station/nearby?";
    private static final String requestStationDetail = "%s/api/exam/station/detail?id=%s";

    @Value("${internal.api.service.measure.url}")
    private String host;

    private RestTemplate template = new RestTemplate();

    @GetMapping(value = "station/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResponseEntity stationNearby(@RequestParam String areaCode,
                                            @RequestParam double longitude, @RequestParam double latitude) {
        String url = String.format(requestStationNearby, host) +
                "areaCode=" + areaCode +
                "&longitude=" + longitude +
                "&latitude=" + latitude;
        ResponseEntity<Map> response = template.getForEntity(url, Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return formatResponse(response.getBody());
        }
        return new JsonResponseEntity(500, "附近免费测量点获取失败");
    }

    @GetMapping("station/detail")
    public JsonResponseEntity stationDetail(@RequestParam String id) {
        ResponseEntity<Map> response = template.getForEntity(String.format(requestStationDetail, host, id), Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return formatResponse(response.getBody());
        }
        return new JsonResponseEntity(500, "信息获取失败");
    }

    private JsonResponseEntity formatResponse(Map responseBody) {
        if (0 != (int)responseBody.get("code")) {
            return new JsonResponseEntity(500, "信息获取失败");
        }
        JsonResponseEntity<Object> result = new JsonResponseEntity<>(0, null);
        result.setData(responseBody.get("data"));
        return result;
    }

}
