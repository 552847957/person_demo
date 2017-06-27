package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import java.text.Collator;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.dto.Interven.DoctorAdvice;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorInterventionDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DiabetesAssessmentRemind;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DiabetesAssessmentRemindRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.TubeRelationRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInterventionRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.BloodGlucoseAndPressureDto;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.interven.dto.OutlierDTO;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
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

    @Value("${disease.h5.url}")
    private String diseaseH5Url;

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

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private TubeRelationRepository tubeRelationRepository;

    @Autowired
    private DoctorInterventionRepository doctorInterventionRepository;

    @Autowired
    private DoctorIntervenService doctorIntervenService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(name = "uid", required = true) String patientId,
                                   @RequestParam(name = "type", required = true) String type) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention doctorIntervention = new DoctorIntervention();
        doctorIntervention.setPatientId(patientId);
        doctorIntervention.setType(type);
        List<DoctorIntervention> rtnList = doctorInterventionService.list(doctorIntervention);
        if (rtnList != null && rtnList.size() > 0) {
            StringBuffer doctorIds = new StringBuffer();
            for (DoctorIntervention tmpObj : rtnList) {
                if (StringUtils.isNotEmpty(tmpObj.getDoctorId())) {
                    if (doctorIds.length() == 0) {
                        doctorIds.append(tmpObj.getDoctorId());
                    } else {
                        doctorIds.append(",").append(tmpObj.getDoctorId());
                    }
                }
            }
            List<Doctor> doctors = doctorService.findDoctorByIds(doctorIds.toString());
            Map<String, Doctor> doctorMap = new HashMap<>();
            if (doctors != null && doctors.size() > 0) {
                for (Doctor doctor : doctors) {
                    doctorMap.put(doctor.getUid(), doctor);
                }
            }
            for (int i = 0; i < rtnList.size(); i++) {
                Doctor doctor = doctorMap.get(rtnList.get(i).getDoctorId());
                if (doctor == null) {
                    rtnList.get(i).setName(null);
                    rtnList.get(i).setDutyName(null);
                    rtnList.get(i).setAvatar(null);
                } else {
                    rtnList.get(i).setName(StringUtils.isEmpty(doctor.getName()) ? doctor.getNickname() : doctor.getName());
                    rtnList.get(i).setDutyName(doctor.getDutyName());
                    rtnList.get(i).setAvatar(doctor.getAvatar());
                }
            }// end for
        }// end if

        if (rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @GetMapping(value = "/bloodGlucoseAndPressure")
    public JsonResponseEntity bloodGlucoseAndPressure(@RequestParam(name = "uid", required = true) String patientId,
                                                      @RequestParam(name = "type", required = true) String type) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        DoctorIntervention doctorIntervention = new DoctorIntervention();
        doctorIntervention.setPatientId(patientId);
        doctorIntervention.setType(type);
        List<DoctorIntervention> rtnList = doctorInterventionService.list(doctorIntervention);

        BloodGlucoseAndPressureDto bgap = new BloodGlucoseAndPressureDto(rtnList);

        jsonResponseEntity.setData(bgap);
        return jsonResponseEntity;
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
                    try {
                        remind(doctorIntervention.getPatientId(), doctorIntervention.getDoctorId(), rtnDoctorIntervention.getId());
                    } catch (Exception ex) {
                        log.error(Exceptions.getStackTraceAsString(ex));
                    }
                    return new JsonResponseEntity<>(0, "干预成功！");
                }
            }
        }
        return new JsonResponseEntity<>(1000, "干预失败");
    }

    @RequestMapping(value = "/intervention/simpleList", method = RequestMethod.GET)
    public JsonResponseEntity simpleList(@RequestParam String doctor_id) {
        Doctor doctor = doctorService.findDoctorByUid(doctor_id);
        if (doctor == null) {
            return new JsonResponseEntity<>(0, "当前医生信息不存在");
        }
        // 获取当前医生在管用户
        List<String> userIds = tubeRelationRepository.getRelationByDoctorInfo(doctor.getHospitalId(), doctor.getName());
        if (userIds == null || userIds.size() < 1) {
            return new JsonResponseEntity<>(0, "当前医生无在管用户");
        }

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
                            if (!userIds.contains(registerInfo.getRegisterid())) {
                                continue;
                            }
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
        return new JsonResponseEntity<>(0, "数据获取失败");
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
        return new JsonResponseEntity<>(0, "数据获取失败");
    }

    @RequestMapping(value = "/intervention/detail", method = RequestMethod.GET)
    public JsonResponseEntity detail(@RequestParam String id) {
        JsonResponseEntity result = new JsonResponseEntity();

        DoctorIntervention doctorIntervention = doctorInterventionRepository.findOne(id);
        if (doctorIntervention != null) {
            Doctor doctor = doctorService.findDoctorByUid(doctorIntervention.getDoctorId());
            if (doctor == null) {
                doctorIntervention.setName(null);
                doctorIntervention.setDutyName(null);
                doctorIntervention.setAvatar(null);
            } else {
                doctorIntervention.setName(StringUtils.isEmpty(doctor.getName()) ? doctor.getNickname() : doctor.getName());
                doctorIntervention.setDutyName(doctor.getDutyName());
                doctorIntervention.setAvatar(doctor.getAvatar());
            }
            result.setData(doctorIntervention);
            return result;
        } else {
            return new JsonResponseEntity(0, "未查询到相关数据");
        }
    }

    public Boolean remind(String registerId, String doctorId, String interventionId) {
        DiabetesAssessmentRemind remind = new DiabetesAssessmentRemind();
        remind.setId(IdGen.uuid());
        remind.setRegisterid(registerId);
        remind.setDoctorId(doctorId);
        remind.setCreateDate(new Date());
        remind.setUpdateDate(new Date());
        remind.setDelFlag("0");
        remindRepo.save(remind);

        String jumpUrl = diseaseH5Url + "/DoctorAdviceDetail/" +interventionId;

        String param = "{\"notifierUID\":\"" + doctorId + "\",\"receiverUID\":\"" + registerId + "\",\"msgType\":\"0\",\"msgTitle\":\"干预提醒\",\"msgContent\":\"近期您血糖数据异常，建议您到所属社区卫生服务中心专业咨询。点击查看医生相关建议。\",\"jumpUrl\":\"" + jumpUrl + "\"}";
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


    /**
     * 用户端医生建议列表
     * @param patientId
     * @return
     */
    @GetMapping(value = "/doctorAdvice/list")
    public JsonListResponseEntity<DoctorAdvice> doctorAdviceList(@RequestParam(name = "uid", required = true) String patientId,
                                                             @RequestParam(defaultValue = "0", required = false) String flag) {
        JsonListResponseEntity<DoctorAdvice> response = new JsonListResponseEntity<>();
        List<DoctorAdvice> doctorAdviceList = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<IntervenEntity> intervenEntityList = doctorIntervenService.findDoctorAdviceListByRegisterid(patientId,pageNo,pageSize);
        if(intervenEntityList!=null && intervenEntityList.size()>0){
            for(IntervenEntity intervenEntity : intervenEntityList){
                if(doctorAdviceList.size()<pageSize) {

                    DoctorAdvice doctorAdvice = new DoctorAdvice(intervenEntity);
                    //查询血糖异常数据
                    List<OutlierDTO> bloodGlucoseOutlierDTOs = Lists.newArrayList();
                    //查询血压异常数据
                    List<OutlierDTO> pressureOutlierDTOs = Lists.newArrayList();

                    //查询是否有关联的异常数据 如果没有则取医生干预中的一条记录
                    NeoFamIntervention neoFamIntervention = doctorInterventionService.findLatestByInterventionId(intervenEntity.getId());
                    if (neoFamIntervention == null) {
                        OutlierDTO outlierDTO = new OutlierDTO(intervenEntity);
                        bloodGlucoseOutlierDTOs.add(outlierDTO);
                        doctorAdvice.setIntervenType(IntervenEnum.msgType1.getTypeName());
                    } else {
                        //血糖
                        List<NeoFamIntervention> bloodGlucoseOutlierList = doctorInterventionService.findPatientBGOutlierListByIntervenId(intervenEntity.getId());
                        if (bloodGlucoseOutlierList != null && bloodGlucoseOutlierList.size() > 0) {
                            for (NeoFamIntervention intervention : bloodGlucoseOutlierList) {
                                OutlierDTO outlierDTO = new OutlierDTO(intervention);
                                bloodGlucoseOutlierDTOs.add(outlierDTO);
                            }
                        }
                        //血压
                        List<NeoFamIntervention> pressureOutlierList = doctorInterventionService.findPatientPressureOutlierListByIntervenId(intervenEntity);
                        if (pressureOutlierList != null && pressureOutlierList.size() > 0) {
                            for (NeoFamIntervention preInterven : pressureOutlierList) {
                                OutlierDTO outlierDTO = new OutlierDTO(preInterven);
                                pressureOutlierDTOs.add(outlierDTO);
                            }
                        }
                    }

                    doctorAdvice.setBloodGlucoseList(bloodGlucoseOutlierDTOs);
                    doctorAdvice.setPressureList(pressureOutlierDTOs);
                    doctorAdviceList.add(doctorAdvice);
                }
            }

            if(intervenEntityList.size()>pageSize){
                more = true;
                flag = String.valueOf(pageNo + 1);
            }

        }
        response.setContent(doctorAdviceList, more, null, flag);
        return response;
    }

    @GetMapping(value = "/doctorAdvice/detail")
    public JsonResponseEntity<DoctorAdvice> doctorAdviceDetail(@RequestParam(name = "id", required = true) String id) {
        JsonResponseEntity<DoctorAdvice> response = new JsonResponseEntity<>();
        DoctorAdvice doctorAdvice = null;
        IntervenEntity intervenEntity = doctorIntervenService.findDoctorAdviceDetailById(id);
        if(intervenEntity!=null){
               doctorAdvice = new DoctorAdvice(intervenEntity);
                //查询血糖异常数据
                List<OutlierDTO> bloodGlucoseOutlierDTOs = Lists.newArrayList();
                //查询血压异常数据
                List<OutlierDTO> pressureOutlierDTOs = Lists.newArrayList();

                //查询是否有关联的异常数据 如果没有则取医生干预中的一条记录
                NeoFamIntervention neoFamIntervention = doctorInterventionService.findLatestByInterventionId(intervenEntity.getId());
                if (neoFamIntervention == null) {
                    OutlierDTO outlierDTO = new OutlierDTO(intervenEntity);
                    bloodGlucoseOutlierDTOs.add(outlierDTO);
                    doctorAdvice.setIntervenType(IntervenEnum.msgType1.getTypeName());
                } else {
                    //血糖
                    List<NeoFamIntervention> bloodGlucoseOutlierList = doctorInterventionService.findPatientBGOutlierListByIntervenId(intervenEntity.getId());
                    if (bloodGlucoseOutlierList != null && bloodGlucoseOutlierList.size() > 0) {
                        for (NeoFamIntervention intervention : bloodGlucoseOutlierList) {
                            OutlierDTO outlierDTO = new OutlierDTO(intervention);
                            bloodGlucoseOutlierDTOs.add(outlierDTO);
                        }
                    }
                    //血压
                    List<NeoFamIntervention> pressureOutlierList = doctorInterventionService.findPatientPressureOutlierListByIntervenId(intervenEntity);
                    if (pressureOutlierList != null && pressureOutlierList.size() > 0) {
                        for (NeoFamIntervention preInterven : pressureOutlierList) {
                            OutlierDTO outlierDTO = new OutlierDTO(preInterven);
                            pressureOutlierDTOs.add(outlierDTO);
                        }
                    }
                }

                doctorAdvice.setBloodGlucoseList(bloodGlucoseOutlierDTOs);
                doctorAdvice.setPressureList(pressureOutlierDTOs);
            }

        response.setData(doctorAdvice);
        return response;
    }
}