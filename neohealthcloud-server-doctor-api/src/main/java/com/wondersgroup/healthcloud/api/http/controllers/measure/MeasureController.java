package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Created by Jeffrey on 16/9/1.
 */
@RestController
@RequestMapping("api/measure")
public class MeasureController {

    @Value("${internal.api.service.measure.url}")
    private String host;

    private static final Logger log = LoggerFactory.getLogger(MeasureController.class);


    private RestTemplate restTemplate = new RestTemplate();

    private static final String requestBMI = "%s/api/measure/nearest/bmiAndWhr?%s";

    @GetMapping(value = "nearest/bmiAndWhr", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResponseEntity findBMIAndWHR(String registerId, String personCard) {
        String path = "registerId=" + registerId + "&personCard=" + personCard;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(String.format(requestBMI, host, path), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                Map body = response.getBody();
                if (0 == (int) body.get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", body.get("data"));
                }
            }
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        }
        return new JsonResponseEntity<>(1000, "健康数据获取失败");
    }
}
