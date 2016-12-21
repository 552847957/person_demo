package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/12/21.
 */
@RestController
@RequestMapping("/api/measure")
public class MeasureController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MeasureController.class);

    @Autowired
    private UserService userService;

    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String requestUploadPath = "%s/api/measure/upload/%s";

    private RestTemplate template = new RestTemplate();

    @VersionRange
    @PostMapping("upload/{type}")
    public JsonResponseEntity<?> uploadMeasureIndexs(@PathVariable int type, @RequestBody Map<String, Object> paras) {
        try {
            String registerId = (String) paras.get("registerId");
            RegisterInfo info = userService.findRegOrAnonymous(registerId);

            String personCard = info.getPersoncard();
            if(personCard != null){
                paras.put("personCard", personCard);
            }

            String url = String.format(requestUploadPath, host, type);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add("access-token", "version3.0");
            ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "数据上传成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("上传体征数据失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据上传失败");
    }
}
