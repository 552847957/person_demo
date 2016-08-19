package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by Jeffrey on 16/8/19.
 */
@RestController
@RequestMapping("api/measure/")
public class MeasureController {

    private static final Logger log = LoggerFactory.getLogger(MeasureController.class);

    private static final String requestFamilyPath = "%s/api/measure/family/nearest?%s";

    private static final String requestUploadPath = "%s/api/measure/upload/%s";

    private RestTemplate template = new RestTemplate();

    @Autowired
    private Environment env;

    @VersionRange
    @GetMapping("family/nearest")
    public JsonResponseEntity<?> nearestMeasure(@RequestParam String familyMateId) {

        try {
            String parameters = "registerId=".concat(familyMateId).concat("&personCard=0");
            String url = String.format(requestFamilyPath, env.getProperty("measure.server.host"), parameters);
            ResponseEntity<Map> response = template.getForEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                Map<String, Object> responseBody = response.getBody();
                if (0 == (int) responseBody.get("code"))
                    return new JsonResponseEntity<>(0, null, responseBody.get("data"));
            }
        } catch (RestClientException e) {
            log.info("请求测量数据异常", e);
        }
        return new JsonResponseEntity<>(1000, "内部错误");
    }

    @VersionRange
    @PostMapping("upload/{type}")
    public JsonResponseEntity<?> uploadMeasureIndexs(@PathVariable int type, @RequestBody Map<String, Object> paras) {

        try {
            String url = String.format(requestUploadPath, env.getProperty("measure.server.host"), type);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add("access-token", "version3.0");
            ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return new JsonResponseEntity<>(0, "数据上传成功", response.getBody().get("data"));
            }
        } catch (RestClientException e) {
            log.info("上传体征数据失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据上传失败");
    }

}
