package com.wondersgroup.healthcloud.api.http.controllers;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/12/14.
 */
@RestController
@RequestMapping("/api")
public class AbnormalController {

    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String requestAbnormalBlood = "%s/api/measure/3.0/getHalfYearForBlood?%s";
    private RestTemplate template = new RestTemplate();
    @VersionRange
    @GetMapping("/abnormal/blood/nearest")
    public JsonResponseEntity nearestMeasure(@RequestParam String personcard) {

            String parameters = "personcard=".concat(personcard);
            String url = String.format(requestAbnormalBlood, host, parameters);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                Map<String, Object> responseBody = response.getBody();
                if (0 == (int) responseBody.get("code"))
                    return new JsonResponseEntity<>(0, null, responseBody.get("data"));
            }

        return new JsonResponseEntity<>(1000, "内部错误");
    }

    private <T> ResponseEntity<T> buildGetEntity(String url, Class<T> responseType, Object... urlVariables){
        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), responseType, urlVariables);
    }
}
