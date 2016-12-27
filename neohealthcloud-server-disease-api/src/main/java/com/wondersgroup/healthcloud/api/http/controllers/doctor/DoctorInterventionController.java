package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import java.text.Collator;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorInterventionDTO;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger("exlog");

    @Value("${internal.api.service.measure.url}")
    private String host;

    @Autowired
    private HttpRequestExecutorManager httpRequestExecutorManager;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    private static final String requestInterventionSimpleListPath = "%s/api/measure/intervention/simpleList";
    private static final String requestInterventionDetailListPath = "%s/api/measure/intervention/detailList";
    private static final String requestInterventionUpdatePath = "%s/api/measure/intervention/update";

    private RestTemplate template = new RestTemplate();


    @Autowired
    private DoctorInterventionService doctorInterventionService;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private DiabetesAssessmentRemindRepository remindRepo;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(name = "uid", required = true) String patientId,
                                   @RequestParam(name = "type", required = true) String type) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention doctorIntervention = new DoctorIntervention();
        doctorIntervention.setPatientId(patientId);
        doctorIntervention.setType(type);
        List<DoctorIntervention> rtnList = doctorInterventionService.list(doctorIntervention);
        if (rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonResponseEntity saveAndUpdate(@RequestBody DoctorIntervention doctorIntervention) {
        JsonResponseEntity result = new JsonResponseEntity();

        if (doctorIntervention == null || StringUtils.isEmpty(doctorIntervention.getPatientId())) {
            return new JsonResponseEntity<>(1000, "[patient_id]不能为空！");
        }

        if (doctorIntervention == null || StringUtils.isEmpty(doctorIntervention.getDoctorId())) {
            return new JsonResponseEntity<>(1000, "[doctor_id]不能为空！");
        }
        Map<String, Object> paras = new HashMap<>();
        paras.put("registerId", doctorIntervention.getPatientId());
        String url = String.format(requestInterventionUpdatePath, host);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("access-token", "version3.0");
        ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            if (0 == (int) response.getBody().get("code")) {
                DoctorIntervention rtnDoctorIntervention = doctorInterventionService.saveAndUpdate(doctorIntervention);
                if (rtnDoctorIntervention != null) {
                    remind(doctorIntervention.getPatientId(), doctorIntervention.getDoctorId());
                    return new JsonResponseEntity<>(0, "干预成功！");
                }
            }
        }
        return new JsonResponseEntity<>(1000, "干预失败");
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
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    List<DoctorInterventionDTO> diDTOs = mapper.readValue(mapper.writeValueAsString(response.getBody().get("data")), new TypeReference<List<DoctorInterventionDTO>>() {
                    });
                    List<String> regesterIds = new ArrayList<>();
                    if (diDTOs != null && diDTOs.size() > 0) {
                        List<DoctorInterventionDTO> rtnList = new ArrayList<>();
                        Map<String, DoctorInterventionDTO> diMap = new HashMap();
                        String registerId = null;
                        for (int i = 0; i < diDTOs.size(); i++) {
                            registerId = diDTOs.get(i).getRegisterId();
                            regesterIds.add(registerId);
                            diMap.put(registerId, diDTOs.get(i));
                        }
                        List<RegisterInfo> registerInfos = registerInfoRepository.findByRegisterIds(regesterIds);

                        for (RegisterInfo registerInfo : registerInfos) {
                            registerId = registerInfo.getRegisterid();
                            diMap.get(registerId).setName(registerInfo.getName() == null ? registerInfo.getNickname() : registerInfo.getName());
                            diMap.get(registerId).setSex(registerInfo.getGender());
                            diMap.get(registerId).setAge(registerInfo.getBirthday() == null ? "未知" : IdcardUtils.getAgeByBirthday(DateFormatter.parseDate(registerInfo.getBirthday().toString())) + "岁");
                            rtnList.add(diMap.get(registerId));
                        }
                        Collections.sort(rtnList, new SortChineseName());
                        return new JsonResponseEntity<>(0, null, rtnList);
                    }
                } catch (Exception ex) {
                    log.error(Exceptions.getStackTraceAsString(ex));
                }
            }
        }
        return new JsonResponseEntity<>(1000, "数据获取失败");
    }

    @RequestMapping(value = "/intervention/detailList", method = RequestMethod.GET)
    public JsonResponseEntity detailList(@RequestParam String registerId) {
        String url = String.format(requestInterventionDetailListPath, host);
        url += "?registerId=" + registerId;
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

    public Boolean remind(String registerId, String doctorId) {
        DiabetesAssessmentRemind remind = new DiabetesAssessmentRemind();
        remind.setId(IdGen.uuid());
        remind.setRegisterid(registerId);
        remind.setDoctorId(doctorId);
        remind.setCreateDate(new Date());
        remind.setUpdateDate(new Date());
        remind.setDelFlag("0");
        remindRepo.save(remind);

        String param = "{\"notifierUID\":\"" + doctorId + "\",\"receiverUID\":\"" + registerId + "\",\"msgType\":\"0\",\"msgTitle\":\"慢病干预\",\"msgContent\":\"近期您血糖数据异常，建议您到所属社区卫生服务中心专业咨询。点击查看医生相关建议。\"}";
        Request build = new RequestBuilder().post().url(jobClientUrl + "/api/disease/message").body(param).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(build).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        return true;
    }

    public static class SortChineseName implements Comparator<DoctorInterventionDTO> {
        Collator cmp = Collator.getInstance(java.util.Locale.CHINA);

        @Override
        public int compare(DoctorInterventionDTO o1, DoctorInterventionDTO o2) {
            if (cmp.compare(o1.getName(), o2.getName()) > 0) {
                return 1;
            } else if (cmp.compare(o1.getName(), o2.getName()) < 0) {
                return -1;
            }
            return 0;
        }
    }
}