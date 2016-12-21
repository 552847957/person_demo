package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import java.util.List;
import java.util.Map;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

@RestController
@RequestMapping("/api/doctorIntervention")
public class DoctorInterventionController {

    @Value("${internal.api.service.measure.url}")
    private String host;

    private static final String requestInterventionSimpleListPath = "%s/api/measure/intervention/simpleList";
    private static final String requestInterventionDetailListPath = "%s/api/measure/intervention/detailList";

    private RestTemplate template = new RestTemplate();


    @Autowired
    private DoctorInterventionService doctorInterventionService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(name = "uid", required = true) String patientId,
                                   @RequestParam(name = "type", required = true) String type) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention doctorIntervention = new DoctorIntervention();
        doctorIntervention.setPatientId(patientId);
        doctorIntervention.setType(type);
        List<DoctorIntervention> rtnList = doctorInterventionService.list(doctorIntervention);
        if(rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonResponseEntity saveAndUpdate(@RequestBody DoctorIntervention doctorIntervention) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention rtnDoctorIntervention = doctorInterventionService.saveAndUpdate(doctorIntervention);
        if(rtnDoctorIntervention != null) {
            result.setMsg("数据保存成功！");
        } else {
            result.setCode(1000);
            result.setMsg("数据保存失败！");
        }
        return result;
    }

    @RequestMapping(value = "/intervention/simpleList", method = RequestMethod.GET)
    public JsonResponseEntity simpleList() {
        String url = String.format(requestInterventionSimpleListPath, host);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("access-token", "version3.0");
        ResponseEntity<Map> response = template.getForEntity(url, Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            if (0 == (int) response.getBody().get("code")) {
                return new JsonResponseEntity<>(0, null, response.getBody().get("data"));
            }
        }
        return new JsonResponseEntity<>(1000, "数据获取失败");
    }

    @RequestMapping(value = "/intervention/detailList", method = RequestMethod.GET)
    public JsonResponseEntity detailList(@RequestParam String registerId) {
        String url = String.format(requestInterventionDetailListPath, host);
        url += "?id=" + registerId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("access-token", "version3.0");
        ResponseEntity<Map> response = template.getForEntity(url, Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            if (0 == (int) response.getBody().get("code")) {
                return new JsonResponseEntity<>(0, null, response.getBody().get("data"));
            }
        }
        return new JsonResponseEntity<>(1000, "数据获取失败");
    }
}