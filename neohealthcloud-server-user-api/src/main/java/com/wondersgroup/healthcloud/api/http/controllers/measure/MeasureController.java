package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.wondersgroup.healthcloud.api.http.dto.measure.MeasureTypeDTO;
import com.wondersgroup.healthcloud.api.http.dto.measure.SimpleMeasure;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by Jeffrey on 16/8/19.
 */
@RestController
@RequestMapping("api/measure")
public class MeasureController {

    private static final Logger log = LoggerFactory.getLogger(MeasureController.class);

    private static final String requestFamilyPath = "%s/api/measure/family/nearest?%s";

    private RestTemplate template = new RestTemplate();

    @Autowired
    private Environment env;

    @GetMapping(value = "home", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResponseEntity<Map> measureHome() {

//        System.out.println("session.getUserId() = " + session.getUserId());

        MeasureTypeDTO bmi = new MeasureTypeDTO();
        bmi.setTitle("BMI");
        bmi.setDesc("BMI超标要谨慎");
        bmi.setIconUrl("");
        bmi.setType(HealthType.BMI);

        MeasureTypeDTO oxygen = new MeasureTypeDTO();
        oxygen.setTitle("测血氧");
        oxygen.setDesc("血氧指数需重视");
        oxygen.setIconUrl("");
        oxygen.setType(HealthType.BloodOxygen);

        MeasureTypeDTO pressure = new MeasureTypeDTO();
        pressure.setTitle("测血压");
        pressure.setDesc("平稳心态控血压");
        pressure.setIconUrl("");
        pressure.setType(HealthType.BloodPressure);

        MeasureTypeDTO glucose = new MeasureTypeDTO();
        glucose.setTitle("测血糖");
        glucose.setDesc("平稳心态控血压");
        glucose.setIconUrl("");
        glucose.setType(HealthType.BloodGlucose);

        MeasureTypeDTO waistHipRatio = new MeasureTypeDTO();
        waistHipRatio.setTitle("腰臀比");
        waistHipRatio.setDesc("平稳心态控血压");
        waistHipRatio.setIconUrl("");
        waistHipRatio.setType(HealthType.WaistHipRatio);

        List<MeasureTypeDTO> measures = new ArrayList<>();
        measures.add(bmi);
        measures.add(oxygen);
        measures.add(pressure);
        measures.add(glucose);
        measures.add(waistHipRatio);

        SimpleMeasure measure = new SimpleMeasure();
        measure.setName("BMI指数");
        measure.setTestTime("2016-08-19");
        measure.setValue("21.7");
        measure.setFlag("0");

        List<SimpleMeasure> histories = Collections.singletonList(measure);

        Map<String, Object> homeMap = new HashMap<>();
        homeMap.put("types", measures);
        homeMap.put("more", histories.size() > 3);
        homeMap.put("histories", histories);
        JsonResponseEntity<Map> result = new JsonResponseEntity<>(0, null);
        result.setData(homeMap);
        return result;
    }

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

}
