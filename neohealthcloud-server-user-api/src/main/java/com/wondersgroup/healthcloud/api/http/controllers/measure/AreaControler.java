package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by Jeffrey on 16/8/29.
 */
@RestController
@RequestMapping("/api")
public class AreaControler {

    @Value("${internal.api.service.measure.url}")
    private String host;

    private static final String requestAreaDictionaryPath = "%s/public/exam/area/dictionary";

    private RestTemplate template = new RestTemplate();

    @GetMapping(value = "/exam/area/dictionary", produces = MediaType.APPLICATION_JSON_VALUE)
    @WithoutToken
    @VersionRange
    public JsonResponseEntity areaDictionary() {
        ResponseEntity<Map> response = template.getForEntity(String.format(requestAreaDictionaryPath, host), Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            Map body = response.getBody();
            if (0 == (int) body.get("code")) {
                JsonResponseEntity result = new JsonResponseEntity(0, null);
                result.setData(body.get("data"));
                return result;
            }
        }
        return new JsonResponseEntity(500, "区域字典获取失败");
    }
}
