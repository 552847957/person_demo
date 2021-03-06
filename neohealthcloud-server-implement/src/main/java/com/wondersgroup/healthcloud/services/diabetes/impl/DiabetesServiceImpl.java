package com.wondersgroup.healthcloud.services.diabetes.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.jpa.entity.disease.DiseaseMessage;
import com.wondersgroup.healthcloud.jpa.entity.user.DiseaseRegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.disease.DiseaseMessageRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.DiseaseRegisterInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.FollowPlanDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportFollowDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDetailDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportScreeningDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDetailDTO;
import com.wondersgroup.healthcloud.utils.DateFormatter;

/**
 * 调用web端慢病接口方法
 * Created by zhuchunliu on 2016/12/8.
 */
@Service("diabetesService")
public class DiabetesServiceImpl implements DiabetesService {

    private Logger                                  logger                       = LoggerFactory.getLogger("exlog");

    private final static HttpRequestExecutorManager httpRequestExecutorManager   = new HttpRequestExecutorManager(
                                                                                         new OkHttpClient());

    private final static String                     TUBE_PATIENT_NUMBER          = "/api/diabetes/management/num";                   //在管人数
    private final static String                     TUBE_PATIENT_LIST            = "/api/diabetes/management/list";                  //在管人群列表
    private final static String                     TUBE_PATIENT_DETAIL          = "/api/diabetes/management";                       //在管人群详情
    private final static String                     REPORT_SCREENING_LIST        = "/api/diabetes/filter";                           //筛查报告列表
    private final static String                     REPORT_INSPECT_LIST          = "/api/inspection/reports";                        //检查报告列表
    private final static String                     REPORT_INSPECT_DETAIL        = "/api/inspection/report";                         //检查报告详情
    private final static String                     REPORT_FOLLOW_LIST           = "/api/diabetes/follow";                           //随访报告列表
    private final static String                     REPORT_COUNT                 = "/api/user/reports/num";                          //随访报告数目
    private final static String                     FOLLOW_PLAN_LIST             = "/api/user/followplan";                           //最近一次随访计划
    private static final String                     LOODGLUCOSE_TOP_ONE = "%s/api/measure/3.0/getBloodGlucoseTopOne?%s";
    @Value("${internal.api.service.measure.url}")
    private String                                  host;
    private RestTemplate                            template                     = new RestTemplate();
    @Value("${diabetes.web.url}")
    private String                                  url;

    //    private String url = "http://10.1.93.110/hds";

    @Autowired
    public DiseaseMessageRepository                 diseaseMessageRepository;
    @Autowired
    public DiseaseRegisterInfoRepository            diseaseRegisterInfoRepository;

    /**
     * 根据医生获取在管人群数
     * @param hospitalCode
     * @param doctorName
     * @return
     */
    @Override
    public Integer getTubePatientNumber(String hospitalCode, String doctorName, String patientName) {

        String[] param = new String[] { "hospitalId", hospitalCode, "docName", doctorName };
        if (null != patientName && !StringUtils.isEmpty(patientName)) {
            param = new String[] { "hospitalId", hospitalCode, "docName", doctorName, "name", patientName };
        }

        Request request = new RequestBuilder().get().url(url + this.TUBE_PATIENT_NUMBER).params(param).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                logger.error("在管人群列数为空 " + hospitalCode + "  " + doctorName + " " + patientName);
                logger.error(jsonNode.toString());
                return 0;
            }
            return jsonNode.get("data").asInt();
        }
        return 0;
    }

    /**
     * 在管人群列表
     * @param hospitalCode
     * @param doctorName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<TubePatientDTO> getTubePatientList(String hospitalCode, String doctorName, String patientName,
            Integer pageNo, Integer pageSize) {
        String[] param = new String[] { "hospitalId", hospitalCode, "docName", doctorName, "pageNo",
                String.valueOf(pageNo - 1), "pageSize", pageSize.toString() };
        if (null != patientName && !StringUtils.isEmpty(patientName)) {
            param = new String[] { "hospitalId", hospitalCode, "docName", doctorName, "name", patientName, "pageNo",
                    String.valueOf(pageNo - 1), "pageSize", pageSize.toString() };
        }
        Request request = new RequestBuilder().get().url(url + this.TUBE_PATIENT_LIST).params(param).build();

        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, TubePatientDTO.class);
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("在管人群列表为空 " + hospitalCode + "  " + doctorName + " " + patientName + " " + pageNo
                            + "  " + pageSize);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 根据身份证获取在管人群详情
     */
    @Override
    public TubePatientDetailDTO getTubePatientDetail(String cardType, String cardNumber) {
        Request request = new RequestBuilder().get().url(url + this.TUBE_PATIENT_DETAIL)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("在管人群列表详情为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), TubePatientDetailDTO.class);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 筛查报告
     * @return
     * @throws Exception
     */
    @Override
    public List<ReportScreeningDTO> getReportScreening(String cardType, String cardNumber) {
        logger.info(url+this.REPORT_SCREENING_LIST+" 参数："+Arrays.toString(new String[] { "personcardType", cardType, "personcardNo", cardNumber }));
        Request request = new RequestBuilder().get().url(url + this.REPORT_SCREENING_LIST)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, ReportScreeningDTO.class);
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("筛查报告为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 检查报告
     * @return
     * @throws Exception
     */
    @Override
    public List<ReportInspectDTO> getReportInspectList(String cardType, String cardNumber) {
        logger.info(url+this.REPORT_INSPECT_LIST+" 参数："+ Arrays.toString(new String[] { "personcardType", cardType, "personcardNo", cardNumber }));
        Request request = new RequestBuilder().get().url(url + this.REPORT_INSPECT_LIST)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, ReportInspectDTO.class);
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("检查报告为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 检查报告详情
     * @return
     * @throws Exception
     */
    @Override
    public List<ReportInspectDetailDTO> getReportInspectDetail(String reportNum, Date reportDate) {
        logger.info(url+this.REPORT_INSPECT_DETAIL+" 参数："+Arrays.toString(new String[] { "reportNo", reportNum, "reportDate",
                new DateTime(reportDate).toString("yyyyMMdd") }));
        Request request = new RequestBuilder()
                .get()
                .url(url + this.REPORT_INSPECT_DETAIL)
                .params(new String[] { "reportNo", reportNum, "reportDate",
                        new DateTime(reportDate).toString("yyyyMMdd") }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class,
                    ReportInspectDetailDTO.class);
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("检查报告详情为空 " + reportNum + "  " + reportDate);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 随访报告详情
     * @return
     * @throws Exception
     */
    @Override
    public List<ReportFollowDTO> getReportFollowList(String cardType, String cardNumber) {
        logger.info(url+this.REPORT_FOLLOW_LIST+" 参数："+Arrays.toString(new String[] { "personcardType", cardType, "personcardNo", cardNumber }));
        Request request = new RequestBuilder().get().url(url + this.REPORT_FOLLOW_LIST)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, ReportFollowDTO.class);

            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("随访报告为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), javaType);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 获取用户报告数
     * @param cardType
     * @param cardNumber
     * @return
     */
    @Override
    public Map<String, Object> getReportCount(String cardType, String cardNumber) {
        logger.info(url+this.REPORT_COUNT+" 参数："+Arrays.toString(new String[] { "personcardType", cardType, "personcardNo", cardNumber }));
        Request request = new RequestBuilder().get().url(url + this.REPORT_COUNT)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("报告数为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), Map.class);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**
     * 获取最近一次随访计划
     */
    @Override
    public FollowPlanDTO getFollowPlanList(String cardType, String cardNumber) {
        logger.info(url+this.FOLLOW_PLAN_LIST+" 参数："+Arrays.toString(new String[] { "personcardType", cardType, "personcardNo", cardNumber }));
        Request request = new RequestBuilder().get().url(url + DiabetesServiceImpl.FOLLOW_PLAN_LIST)
                .params(new String[] { "personcardType", cardType, "personcardNo", cardNumber }).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run()
                .as(JsonNodeResponseWrapper.class);
        JsonNode jsonNode = response.convertBody();
        if (200 == response.code() && 0 == jsonNode.get("code").asInt()) {
            try {
                if (null == jsonNode.get("data") || StringUtils.isEmpty(jsonNode.get("data").toString())) {
                    logger.error("随访计划为空 " + cardType + "  " + cardNumber);
                    logger.error(jsonNode.toString());
                    return null;
                }
                return new ObjectMapper().readValue(jsonNode.get("data").toString(), FollowPlanDTO.class);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        DiabetesServiceImpl diabetesService = new DiabetesServiceImpl();
        diabetesService.url = "http://10.1.93.111:8380/hds";
        //        int total = diabetesService.getTubePatientNumber("42509835700","王庆杰");
        //        System.err.println(total);
        //
        List<TubePatientDTO> list = diabetesService.getTubePatientList("42509835700X", "王庆杰", null, 1, 10);
        System.err.println(list.size());
        //
        //        TubePatientDetailDTO detailDTO = diabetesService.getTubePatientDetail("01","310110193606134623");
        //        System.err.println(detailDTO.getName()+"  "+detailDTO.getHospitalCode()+"   "+detailDTO.getDoctorName());

        //        List<ReportScreeningDTO> screening = diabetesService.getReportScreening("01","420621198811200612");
        //        System.err.println(screening.size());
        //
        //        List<ReportInspectDTO> inspect = diabetesService.getReportInspectList("123456789");
        //        System.err.println(inspect.size());
        //
        //        List<ReportInspectDetailDTO> inspectDetail = diabetesService.getReportInspectDetail("4250983570010000000001",new DateTime("20160907").toDate());
        //        System.err.println(inspectDetail.size());
        //
        //        List<ReportFollowDTO> follow = diabetesService.getReportFollowList("310223195206141425");
        //        System.err.println(follow.size());

    }

    @Override
    public Boolean addDiabetesRemindMessage(String registerId) {
        DiseaseRegisterInfo info = diseaseRegisterInfoRepository.findByUserId(registerId);
        if (info == null || StringUtils.isBlank(info.getId())) {
            return true;
        }
        long day = 1;
        try {
            StringBuffer str = new StringBuffer();
            str.append("registerId=").append(registerId);
            String url = String.format(LOODGLUCOSE_TOP_ONE, host, str);
            ResponseEntity<Map> en = template.exchange(url, HttpMethod.GET, null, Map.class);
            Map m = en.getBody();
            if (Integer.parseInt(m.get("code").toString()) == 0) {
                String date = String.valueOf(m.get("data"));
               long sum = DateFormatter.dateMinusDateForDays(date, DateTime.now().toString("yyyy-MM-dd"), "yyyy-MM-dd");
               if(sum != 0 && sum / 7 != 0){
                   DiseaseMessage mes = diseaseMessageRepository.getDiseaseMessageByToday(registerId);
                   if(mes != null){
                       return false;
                   }
                   day = sum / 7;
                   DiseaseMessage message = new DiseaseMessage();
                   message.setContent("您已有" + day + "周无血糖测量记录, 请前往测量。");
                   message.setCreateTime(new Date());
                   message.setIsRead("0");
                   message.setDelFlag("0");
                   message.setJumpUrl("com.wondersgroup.healthcloud.3101://user/measure/boold_sugar");
                   message.setMsgType("3");
                   message.setTitle("血糖测量提醒");
                   message.setReceiverUid(registerId);
                   message.setNotifierUid(registerId);
                   diseaseMessageRepository.save(message);
               }
            }
        } catch (RestClientException e) {
            logger.info("请求测量数据异常", e);
        }
      
        return true;
    }
}
