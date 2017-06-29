package com.wondersgroup.healthcloud.api.http.controllers.measure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wondersgroup.healthcloud.api.http.dto.doctor.AssessmentAbnormal;
import com.wondersgroup.healthcloud.api.http.dto.doctor.disease.ScreeningDto;
import com.wondersgroup.healthcloud.api.http.dto.doctor.heathrecord.HeathIconDto;
import com.wondersgroup.healthcloud.api.http.dto.doctor.heathrecord.HeathUserInfoDto;
import com.wondersgroup.healthcloud.common.http.annotations.JsonEncode;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.jpa.entity.assessment.Assessment;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.assessment.AssessmentRepository;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.DoctorTubeSignUserRepository;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import com.wondersgroup.healthcloud.services.assessment.dto.AssessmentConstrains;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jeffrey on 16/9/1.
 */
@Controller
@RequestMapping("api/measure")
public class MeasureController {

    @Value("${internal.api.service.measure.url}")
    private String host;

    private static final Logger log = LoggerFactory.getLogger(MeasureController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorTubeSignUserRepository doctorTubeSignUserRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private DictCache dictCache;
    @Autowired
    private ImageTextService imageTextService;
    @Autowired
    private DoctorIntervenService doctorIntervenService;
    @Autowired
    private DoctorTubeSignUserService doctorTubeSignUserService;

    private RestTemplate restTemplate = new RestTemplate();
    private static final String recentMeasureHistoryByDate = "%s/api/measure/3.0/recentHistoryByDate/%s?%s";
    private static final String recentMeasureStatisticalData = "%s/api/measure/3.0/recentStatisticalData?%s";
    private static final String recentMeasureHistory = "%s/api/measure/3.0/recentHistory/%s?%s";
    private static final String queryHistoryRecordByDate = "%s/api/measure/3.0/queryHistoryRecordByDate?%s";
    private static final String historyHba1cPath = "%s/api/measure/history/hba1c?%s";
    private static final String bmiH5ChartPath = "%s/api/measure/bmi/chart?%s";
    private static final String requestHistoryByArrayDay = "%s/api/measure/3.0/getHistoryByArrayDay?%s";
    private static final String BLOODGLUCOSE_TOP_ONE = "%s/api/measure/3.0/getBloodGlucoseTopOne?%s";

    private static final String requestBMI = "%s/api/measure/nearest/bmiAndWhr?%s";
    private static final String REQUEST_BLOODGLUCOSE_ABNORMAL = "%s/api/measure/3.0/queryBloodGlucoseAbnormalHistorys?%s";
    private static final String REQUEST_PRESSURE_ABNORMAL = "%s/api/measure/3.0/queryPressureAbnormalHistorys?%s";

    @GetMapping(value = "nearest/bmiAndWhr", produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonEncode(encode = true)
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

    /**
     * 血压异常查询
     *
     * @param registerId
     * @param personCard
     * @param currentPage
     * @param numPerPage
     * @return JsonResponseEntity
     */
    @GetMapping(value = "queryPressureAbnormalHistorys", produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonEncode(encode = true)
    public JsonResponseEntity queryPressureAbnormalHistorys(
            @RequestParam String registerId,
            @RequestParam(required = false, defaultValue = "") String personCard,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "5") int numPerPage
    ) {
        String path = "registerId=" + registerId + "&personCard=" + personCard
                + "currentPage=" + currentPage + "&numPerPage=" + numPerPage;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(String.format(REQUEST_PRESSURE_ABNORMAL, host, path), Map.class);
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

    /**
     * 血糖异常查询
     *
     * @param registerId
     * @param personCard
     * @param currentPage
     * @param numPerPage
     * @return JsonResponseEntity
     */
    @GetMapping(value = "queryBloodGlucoseAbnormalHistorys", produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonEncode(encode = true)
    public JsonResponseEntity queryBloodGlucoseAbnormalHistorys(
            @RequestParam String registerId,
            @RequestParam(required = false, defaultValue = "") String personCard,
            @RequestParam(required = false, defaultValue = "1") int currentPage,
            @RequestParam(required = false, defaultValue = "5") int numPerPage
    ) {
        String path = "registerId=" + registerId + "&personCard=" + personCard
                + "currentPage=" + currentPage + "&numPerPage=" + numPerPage;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(String.format(REQUEST_BLOODGLUCOSE_ABNORMAL, host, path), Map.class);
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


    /**
     * 周血糖
     *
     * @param registerId
     * @param personCard
     * @return JsonResponseEntity
     */
    @RequestMapping(value = "/lastWeekHistory", method = RequestMethod.GET)
    @JsonEncode(encode = true)
    @VersionRange
    public JsonResponseEntity measureHistory(@RequestParam(name = "uid") String registerId,
                                             @RequestParam(required = false) String personCard) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            List<JsonNode> lastWeekData = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            int count = 0;
            for (int i = 0; i < 7; i++) {
                lastWeekData.add(((ObjectNode) mapper.readTree("{}")).put("date", DateFormatter.dateFormat(DateTime.now().plusDays(-i).toDate())));
            }
            RegisterInfo info = userService.getOneNotNull(registerId);

            try {
                String statisticalparam = "registerId=".concat(registerId)
                        .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard());
                String url = String.format(recentMeasureStatisticalData, host, statisticalparam);
                ResponseEntity<Map> response = buildGetEntity(url, Map.class);
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    if (0 == (int) response.getBody().get("code")) {
                        String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JsonNode resultJson = mapper.readTree(jsonStr);
                            Map<String, Object> tmpMap = new HashMap<>();
                            tmpMap.put("high", resultJson.get("high").asInt());
                            tmpMap.put("normal", resultJson.get("normal").asInt());
                            tmpMap.put("low", resultJson.get("low").asInt());
                            rtnMap.put("status", tmpMap);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("MeasureController.measureHistory error --> " + ex.getLocalizedMessage());
            }

            String param = "registerId=".concat(registerId)
                    .concat("&sex=").concat(StringUtils.isEmpty(info.getGender()) ? "1" : info.getGender())
                    .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard());
            String url = String.format(recentMeasureHistory, host, "3", param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.get("content").iterator();// 最近10天测量记录
                        DateTime today = DateTime.now();
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();// 单日测量记录
                            String date = jsonNode.get("date").asText();
                            if (date != null
                                    && !new DateTime(date).isBefore(today.plusDays(-6).withTimeAtStartOfDay().getMillis())
                                    && new DateTime(date).isBefore(today.plusDays(1).withTimeAtStartOfDay())) {

                                Map<Integer, List<JsonNode>> testPeriodMap = groupByTestPerid(jsonNode);
                                String[] dayDatas = genedayDatas(testPeriodMap);

                                StringBuffer strBuf = new StringBuffer();
                                for (int j = 0; j < dayDatas.length; j++) {
                                    if (j == 0) {
                                        strBuf.append(dayDatas[j]);
                                    } else {
                                        strBuf.append(",").append(dayDatas[j]);
                                    }
                                }
                                ((ObjectNode) jsonNode).put("data", strBuf.toString());
                                lastWeekData.set(Days.daysBetween(new DateTime(date), today).getDays(), jsonNode);
                            }
                            count++;
                        }
                    }
                }
            }

            rtnMap.put("list", lastWeekData);
            rtnMap.put("count", count);
            result.setData(rtnMap);
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }


    /**
     * 将一天的血糖数据，按照 testPeriod 分组
     *
     * @param jsonNode
     * @return
     */
    private Map<Integer, List<JsonNode>> groupByTestPerid(JsonNode jsonNode) {
        Map<Integer, List<JsonNode>> periodMap = new HashMap<Integer, List<JsonNode>>();
        JsonNode jsonNodeData = jsonNode.get("data");
        Iterator<JsonNode> itJsonNodeData = jsonNodeData.iterator();
        List<JsonNode> list = IteratorUtils.toList(itJsonNodeData);

        for (JsonNode node : list) {
            Integer testPeriod = node.get("testPeriod").asInt();
            if (periodMap.keySet().contains(testPeriod)) {
                periodMap.get(testPeriod).add(node);
            } else {
                List<JsonNode> keyList = new ArrayList<JsonNode>();
                keyList.add(node);
                periodMap.put(testPeriod, keyList);
            }

        }
        return periodMap;
    }

    /**
     * 生成一天的测量数据（根据业务排序的）
     *
     * @param testPeriodMap
     * @return
     */
    private String[] genedayDatas(Map<Integer, List<JsonNode>> testPeriodMap) {
        String[] dayDatas = {"", "", "", "", "", "", "", ""};
        if (null != testPeriodMap && testPeriodMap.keySet().size() > 0) {
            for (Integer testPeriodKey : testPeriodMap.keySet()) {
                List<JsonNode> testPeriodList = testPeriodMap.get(testPeriodKey);
                sortList(testPeriodList);
                Iterator<JsonNode> itJsonNodeData = testPeriodList.iterator();
                while (itJsonNodeData.hasNext()) {
                    JsonNode tmpJson = itJsonNodeData.next();
                    int testPeriod = tmpJson.get("testPeriod").asInt();
                    if (0 <= testPeriod && testPeriod <= 7) {
                        //  7 0 1 2 3 4 5 6 这样排序,醉了
                        if (testPeriod == 7) {
                            if (StringUtils.isBlank(dayDatas[0])) {
                                dayDatas[0] = tmpJson.get("fpgValue").asText();
                            } else {
                                String day = dayDatas[0];
                                if (day.split("&").length < 4) {// 同一时间段最新4条数据
                                    dayDatas[0] = day + (StringUtils.isBlank(day) ? "" : "&") + tmpJson.get("fpgValue").asText();
                                }
                            }
                        } else {
                            String day = dayDatas[testPeriod + 1];
                            if (day.split("&").length < 4) {// 同一时间段最新4条数据
                                dayDatas[testPeriod + 1] = day + (StringUtils.isBlank(day) ? "" : "&") + tmpJson.get("fpgValue").asText();
                            }
                        }
                    }
                }

            }

        }
        return dayDatas;
    }

    /**
     * 按时间排序后，除去第一条，剩下的要按照 measureWay 值的2,3,1 顺序排序
     *
     * @param list
     * @return
     */
    private List<JsonNode> sortList(List<JsonNode> list) {
        if (CollectionUtils.isNotEmpty(list) && list.size() > 2) {
            JsonNode firstNode = list.get(0);
            list.remove(0);
            try {
                Collections.sort(list, new Compartor());
            } catch (Exception e) {
                e.printStackTrace();
                log.info("排序异常 ", e);
            }
            list.add(0, firstNode);
        }
        return list;

    }

    /**
     * 月血糖
     *
     * @param registerId
     * @param personCard
     * @param begin_date
     * @return JsonResponseEntity
     */
    @RequestMapping(value = "/getMeasureHistoryByDate", method = RequestMethod.GET)
    @VersionRange
    @JsonEncode(encode = true)
    public JsonResponseEntity measureHistoryByDate(@RequestParam(name = "uid") String registerId,
                                                   @RequestParam(required = false) String personCard,
                                                   @RequestParam(name = "beginDate", required = false) String begin_date) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> rtnMap = new HashMap<>();
        String flagDate = StringUtils.isEmpty(begin_date) ? DateTime.now().toString("yyyy-MM-dd") : begin_date;
        DateTime beginDateTime = new DateTime(flagDate).plusMonths(-1).plusDays(1);
        DateTime endDateTime = new DateTime(flagDate);

        DateTime nextMonth = new DateTime(flagDate).plusMonths(1);
        if (!nextMonth.isAfter(DateTime.now())) {
            rtnMap.put("nextMonth", nextMonth.toString("yyyy-MM-dd"));
        }
        rtnMap.put("frontMonth", new DateTime(flagDate).plusMonths(-1).toString("yyyy-MM-dd"));
        try {
            List<JsonNode> monthDate = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            int days = -Days.daysBetween(endDateTime, beginDateTime).getDays() + 1;
            for (int i = 0; i < days; i++) {
                monthDate.add(((ObjectNode) mapper.readTree("{}")).put("date", DateFormatter.dateFormat(endDateTime.plusDays(-i).toDate())));
            }
            RegisterInfo info = userService.getOneNotNull(registerId);
            String param = "registerId=".concat(registerId)
                    .concat("&sex=").concat(StringUtils.isEmpty(info.getGender()) ? "1" : info.getGender())
                    .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard())
                    .concat("&begin_date=").concat(beginDateTime.toString("yyyy-MM-dd"))
                    .concat("&end_date=").concat(endDateTime.toString("yyyy-MM-dd"));
            String url = String.format(recentMeasureHistoryByDate, host, "3", param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.get("content").iterator();// 最近10天测量记录
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();// 单日测量记录
                            String date = jsonNode.get("date").asText();
                            if (date != null
                                    && !new DateTime(date).isBefore(beginDateTime.withTimeAtStartOfDay().getMillis())
                                    && new DateTime(date).isBefore(endDateTime.plusDays(1).withTimeAtStartOfDay())) {
                                Map<Integer, List<JsonNode>> testPeriodMap = groupByTestPerid(jsonNode);
                                String[] dayDatas = genedayDatas(testPeriodMap);
                                StringBuffer strBuf = new StringBuffer();
                                for (int j = 0; j < dayDatas.length; j++) {
                                    if (j == 0) {
                                        strBuf.append(dayDatas[j]);
                                    } else {
                                        strBuf.append(",").append(dayDatas[j]);
                                    }
                                }
                                ((ObjectNode) jsonNode).put("data", strBuf.toString());
                                monthDate.set(Days.daysBetween(new DateTime(date), endDateTime).getDays(), jsonNode);
                            }
                        }
                    }
                }
            }
            rtnMap.put("before", dateBeforeIsExistData(registerId, StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard(), new DateTime(flagDate).plusMonths(-1).plusDays(-1).toString("yyyy-MM-dd"), true));
            rtnMap.put("after", dateBeforeIsExistData(registerId, StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard(), new DateTime(flagDate).plusDays(1).toString("yyyy-MM-dd"), false));
            rtnMap.put("thisMonth", monthDate);
            rtnMap.put("thisMonth", monthDate);
            result.setData(rtnMap);
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }

    class Compartor implements Comparator<JsonNode> {
        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            String o1va = o1.get("measureWay").asText();
            String o2va = o2.get("measureWay").asText();
            if (o1va.equals(o2va)) {
                return 1;
            }
            if ("2".equals(o1va)) {
                return -1;
            }
            if ("2".equals(o2va)) {
                return 1;
            }
            if ("3".equals(o1va)) {
                return -1;
            }
            if ("3".equals(o2va)) {
                return 1;
            }
            return -1;
        }
    }

    public boolean dateBeforeIsExistData(String registerId, String personCard, String date, Boolean isBefore) {
        boolean result = false;
        try {
            String param = "registerId=".concat(registerId)
                    .concat("&personCard=").concat(personCard)
                    .concat("&date=").concat(date)
                    .concat("&isBefore=" + isBefore);
            String url = String.format(queryHistoryRecordByDate, host, param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    result = Integer.parseInt(response.getBody().get("data").toString()) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" dateBeforeIsExistData " + e.getMessage());
        }
        return result;
    }

    /**
     * 糖化血红蛋白历史数据分页
     *
     * @param registerId
     * @param personCard
     * @param flag       页数 从0 开始
     * @param pageSize   每页条数
     * @return
     */
    @VersionRange
    @GetMapping("history/hba1c")
    @JsonEncode(encode = true)
    public JsonResponseEntity<?> queryHba1cHistoryData(@RequestParam(defaultValue = "0") Integer flag,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam String registerId, String personCard) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("registerId=" + registerId).append("&personCard=" + personCard)
                    .append("&flag=" + flag).append("&pageSize=" + pageSize);
            String url = String.format(historyHba1cPath, host, sb.toString());
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("查询失败", e);
        }
        return new JsonResponseEntity<>(1000, "查询失败");
    }

    /**
     * BMI图表H5
     *
     * @param registerId
     * @param personCard
     * @param date       "2017-04-15"
     * @return
     */
    @VersionRange
    @GetMapping("chart/bmi")
    @JsonEncode(encode = true)
    public JsonResponseEntity<?> chartBmi(
            String registerId,
            @RequestParam(required = false) String personCard,
            String date,
            @RequestParam(defaultValue = "false") Boolean isBefore,
            @RequestParam(defaultValue = "5") Integer dayAmount) {
        try {

            StringBuffer param = new StringBuffer();
            param.append("registerId=" + registerId).append("&personCard=" + personCard)
                    .append("&date=" + date).append("&isBefore=" + isBefore).append("&dayAmount=" + dayAmount);
            String url = String.format(bmiH5ChartPath, host, param);

            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("查询失败", e);
        }
        return new JsonResponseEntity<>(1000, "查询失败");
    }

    /**
     * 查询一周血压
     * 根据天的维度展示
     *
     * @param registerId
     * @return json
     * @throws JsonProcessingException
     */
    @GetMapping("getHistoryByArrayDay")
    @VersionRange
    @JsonEncode(encode = true)
    public JsonResponseEntity getHistoryByArrayDay(
            String registerId,
            String personCard,
            String date,
            @RequestParam(defaultValue = "5") String dayAmount,
            @RequestParam(defaultValue = "true") Boolean isBefore, Pageable pageable) throws JsonProcessingException {
        try {
            StringBuffer str = new StringBuffer();
            str.append("registerId=").append(registerId)
                    .append("&personCard=").append(personCard)
                    .append("&isBefore=").append(isBefore)
                    .append("&date=").append(date)
                    .append("&dayAmount=").append(dayAmount);
            String url = String.format(requestHistoryByArrayDay, host, str);

            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
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

    /**
     * 查询7天是否有血糖测量
     *
     * @param registerId
     * @return json
     * @throws JsonProcessingException
     */
    @GetMapping("getWeekIsExistByArrayDay")
    @VersionRange
    @JsonEncode(encode = true)
    public JsonResponseEntity getWeekIsExistByArrayDay(String registerId, String date) {
        try {
            StringBuffer str = new StringBuffer();
            str.append("registerId=").append(registerId);

            String url = String.format(BLOODGLUCOSE_TOP_ONE, host, str);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
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

    /**
     * 查询历史记录 3.0
     * 根据天的维度展示
     *
     * @param type       0－BMI 1－血氧 2－血压 3－血糖 4－记步 5－腰臀比
     * @param registerId 注册认证码
     * @param flag       分页 页号
     * @return
     * @throws JsonProcessingException
     */
    @VersionRange
    @GetMapping("recentHistory/{type}")
    @JsonEncode(encode = true)
    public JsonResponseEntity getRecentMeasureHistory(@PathVariable int type, Integer flag, String registerId) throws JsonProcessingException {
        try {
            RegisterInfo info = userService.findRegOrAnonymous(registerId);
            String gender = info.getGender();
            String personcard = info.getPersoncard();

            String param = "registerId=".concat(registerId).concat("&sex=").concat(StringUtils.isEmpty(gender) ? "1" : gender)
                    .concat("&personCard=").concat(StringUtils.isEmpty(personcard) ? "" : personcard);
            String params = (flag == null) ? param : param.concat("&flag=").concat(String.valueOf(flag));
            String url = String.format(recentMeasureHistory, host, type, params);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "近期数据查询成功", response.getBody().get("data"));
                }
            }
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return new JsonResponseEntity(1000, "近期历史数据获取失败");
    }

    @GetMapping("userInfo")
    @VersionRange
    @JsonEncode(encode = true)
    public JsonResponseEntity userInfo(String registerId, String famId) throws JsonProcessingException {
        HeathUserInfoDto infoDto = new HeathUserInfoDto();
        if(!StringUtils.isBlank(registerId)){
            RegisterInfo registerInfo = userService.getOneNotNull(registerId);
            infoDto.setAddress(getAddress(registerId, false));
            infoDto.setMedicarecard(registerInfo.getMedicarecard());
            infoDto.setName(registerInfo.getName());
            infoDto.setGender(registerInfo.getGender());
            infoDto.setCardType("01");
            infoDto.setCardNumber(registerInfo.getPersoncard());
            infoDto.setMobilePhone(registerInfo.getRegmobilephone());
            if(registerInfo.getPersoncard() != null){
                infoDto.setAge(IdcardUtils.getAgeByIdCard(registerInfo.getPersoncard()));
            }
            infoDto.setAvatar(registerInfo.getHeadphoto());
            if(registerInfo.getBirthday() != null){
                    infoDto.setBirth(new SimpleDateFormat("yyyy-MM-dd").format(registerInfo.getBirthday()));
            }
            if(!StringUtils.isBlank(registerInfo.getPersoncard())){
                DoctorTubeSignUser singUser = doctorTubeSignUserRepository.queryInfoByCard(registerInfo.getPersoncard());
                if(singUser != null){
                    infoDto.setHypType("1".equals(singUser.getHypType()));
                    infoDto.setDiabetesType("1".equals(singUser.getDiabetesType()));
                    infoDto.setApoType("1".equals(singUser.getApoType()));
                    infoDto.setIsRisk("1".equals(singUser.getIsRisk()));
                    infoDto.setSignStatus("1".equals(singUser.getSignStatus()));
                }
            }
        }else if(!StringUtils.isBlank(famId)){
            DoctorTubeSignUser info = doctorTubeSignUserRepository.findOne(famId);
            List<RegisterInfo> regInfos = userService.findRegisterInfoByIdcard(info.getCardNumber());
            if ("0".equals(info.getSignStatus()) && info.getTubeType() == 1) {
                String adr = doctorTubeSignUserService.getGUserAddress(info.getCardNumber());
                infoDto.setAddress(adr);
            }
            if(regInfos != null && regInfos.size() > 0){
                RegisterInfo reg = regInfos.get(0);
                infoDto.setMedicarecard(reg.getMedicarecard());
                if(infoDto.getAddress() == null){
                    infoDto.setAddress(getAddress(reg.getRegisterid(), false));
                }
            }
            infoDto.setName(info.getName());
            infoDto.setGender(info.getGender());
            if(info.getBirth() !=null){
                infoDto.setBirth(new SimpleDateFormat("yyyy-MM-dd").format(info.getBirth()));
            }
            infoDto.setCardType(info.getCardType());
            infoDto.setCardNumber(info.getCardNumber());
            infoDto.setProfession(info.getProfession());
            infoDto.setEmployStatus(info.getEmployStatus());
            infoDto.setMobilePhone(info.getMoblilePhone());
            infoDto.setFixedPhone(info.getFixedPhone());
            infoDto.setContactPhone(info.getContactPhone());
            infoDto.setHypType("1".equals(info.getHypType()));
            infoDto.setDiabetesType("1".equals(info.getDiabetesType()));
            infoDto.setApoType("1".equals(info.getApoType()));
            infoDto.setIsRisk("1".equals(info.getIsRisk()));
            infoDto.setIdentifyType("1".equals(info.getIdentifytype()));
            infoDto.setSignStatus("1".equals(info.getSignStatus()));
            infoDto.setAge(info.getAge());
            infoDto.setAvatar(info.getAvatar());
        }else{
            return new JsonResponseEntity(1001, "用户数据获取失败");
        }
        return new JsonResponseEntity(0, "获取成功", infoDto);
    }

    @VersionRange
    @GetMapping("assessmentAbnormal")
    @JsonEncode(encode = true)
    public JsonListResponseEntity<AssessmentAbnormal> assessmentAbnormal(
            String registerId,
            @RequestParam(defaultValue = "1") Integer flag,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        JsonListResponseEntity<AssessmentAbnormal> entity = new JsonListResponseEntity<>();
        List<AssessmentAbnormal> arr = new ArrayList<AssessmentAbnormal>();
        boolean more = false;
        try {
            if(flag < 1){
                flag = 1;
            }
           int start = (flag -1) * pageSize;
            String date = new DateTime().plusDays(-90).toString("yyyy-MM-dd HH:mm:ss");
            List<Assessment> list = assessmentRepository.queryAssessment(registerId, date,start,pageSize);
            if(list != null && list.size() > 0){
                List<Assessment> res = assessmentRepository.queryAssessment(registerId, date,start + pageSize, pageSize);
                more =  res.size() > 0;
            }
            for (Assessment assessment : list) {
                AssessmentAbnormal as = new AssessmentAbnormal();
                as.setDate(new SimpleDateFormat("yyyy-MM-dd").format(assessment.getCreateDate()));
                as.setCause(cause(assessment));
                String result = "";
                String su = assessmentService.getResult(assessment);
                String[] split = su.split(",");
                for (String str : split) {
                    if ("1-2".equals(str) || "1-3".equals(str)) {
                        result += ",糖尿病";
                    } else if ("2-2".equals(str) || "2-3".equals(str)) {
                        result += ",高血压";
                    } else if ("3-2".equals(str) || "3-3".equals(str)) {
                        result += ",脑卒中";
                    }
                }
                if (!StringUtils.isBlank(result)) {
                    as.setResult(result.substring(1, result.length()) + "风险");
                }
                arr.add(as);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("数据获取失败", e);
            entity.setCode(1000);
            entity.setMsg("调用失败");
            return entity;
        }
        entity.setContent(arr, more, "", String.valueOf(flag + 1));
        return entity;
    }


    @VersionRange
    @GetMapping("heathUserInfo")
    @JsonEncode(encode = true)
    public JsonResponseEntity heathUserInfo(String registerId, String famId) {

        HeathUserInfoDto infoDto = new HeathUserInfoDto();
        RegisterInfo regInfo = null;
        DoctorTubeSignUser singUser = null;
        try {
            if(registerId != null){
                regInfo = userService.findOne(registerId);
                if(!StringUtils.isBlank(regInfo.getPersoncard())){
                    singUser = doctorTubeSignUserRepository.queryInfoByCard(regInfo.getPersoncard());
                    if(singUser != null){
                        infoDto.setHypType("1".equals(singUser.getHypType()));
                        infoDto.setDiabetesType("1".equals(singUser.getDiabetesType()));
                        infoDto.setApoType("1".equals(singUser.getApoType()));
                        infoDto.setIsRisk("1".equals(singUser.getIsRisk()));
                        infoDto.setSignStatus("1".equals(singUser.getSignStatus()));
                    }
                }

            }else if(famId != null){
                singUser = doctorTubeSignUserRepository.findOne(famId);
                List<RegisterInfo> regisInfos = userService.findRegisterInfoByIdcard(singUser.getCardNumber());
                if(regisInfos != null  && regisInfos.size() > 0){
                    regInfo = regisInfos.get(0);
                }
                infoDto.setHypType("1".equals(singUser.getHypType()));
                infoDto.setDiabetesType("1".equals(singUser.getDiabetesType()));
                infoDto.setApoType("1".equals(singUser.getApoType()));
                infoDto.setIsRisk("1".equals(singUser.getIsRisk()));
                infoDto.setSignStatus("1".equals(singUser.getSignStatus()));

                if ("0".equals(singUser.getSignStatus()) && singUser.getTubeType() == 1) {
                    String adr = doctorTubeSignUserService.getGUserAddress(singUser.getCardNumber());
                    infoDto.setAddress(adr);
                }
            }else{
                return new JsonResponseEntity(1001, "用户数据获取失败");
            }
            String personcard = singUser != null ? singUser.getCardNumber() : regInfo.getPersoncard();
            infoDto.setName(singUser != null ? singUser.getName() : regInfo.getName());
            if(regInfo != null && infoDto.getAddress() == null){
                infoDto.setAddress(getAddress(regInfo.getRegisterid(), true));
            }
            infoDto.setAge(IdcardUtils.getAgeByIdCard(personcard));
            infoDto.setIdentifyType("1".equals(singUser != null ? singUser.getIdentifytype() : regInfo.getIdentifytype()));
            infoDto.setAvatar(singUser != null ? singUser.getAvatar() : regInfo.getHeadphoto());
            infoDto.setPhone(singUser != null ? singUser.getMoblilePhone() : regInfo.getRegmobilephone());
            infoDto.setGender(singUser != null ? singUser.getGender() : regInfo.getGender());

            List<HeathIconDto> icons = new ArrayList<HeathIconDto>();

            String mainArea = "3101";
            String specArea ="";
            ImageText imageText = new ImageText();
            imageText.setAdcode(14);
            imageText.setSource("2");
            List<ImageText> imageTextList = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imageText);
            boolean isNew = false;
            if(regInfo != null){
                String date = new DateTime().plusDays(-90).toString("yyyy-MM-dd HH:mm:ss");
                List<Assessment> list = assessmentRepository.queryAssessment(regInfo.getRegisterid(), date,0,10);
                isNew = doctorIntervenService.hasTodoIntervensByRegisterId(regInfo.getRegisterid());
                if(list !=null && list.size() > 0){
                    isNew = true;
                }
            }
            if(imageTextList !=null){
                for (ImageText image : imageTextList) {
                    String hopLink = repliceUrl(image.getHoplink(), regInfo == null ? famId : regInfo.getRegisterid(), personcard);
                    HeathIconDto icon = new HeathIconDto(image.getMainTitle(), hopLink, image.getImgUrl());
                    if(!StringUtils.isBlank(image.getMainTitle()) && image.getMainTitle().contains("异常")){
                        icon.setIsNew(isNew ? 1  : 0);
                    }
                    icons.add(icon);
                    infoDto.setIcons(icons);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("数据获取失败", e);
            return new JsonResponseEntity(1000, "数据获取失败");
        }
        return new JsonResponseEntity(0, "数据获取成功", infoDto);
    }

    @VersionRange
    @GetMapping("findAbnormalType")
    @JsonEncode(encode = true)
    public Map findAbnormalType(String registerId){
        Map<String,Object> result = new HashedMap();
        String types = doctorIntervenService.findNotDealInterveneTypes(registerId);
        String interveneTypeNames = "json";
        if(StringUtils.isNotBlank(types)){
            interveneTypeNames = IntervenEnum.getIntervenTypeNames(types);
        }
        result.put("code" , 0);
        result.put("msg"  , "数据获取成功");
        result.put("data" , interveneTypeNames);

        return result;
    }

    private <T> ResponseEntity<T> buildGetEntity(String url, Class<T> responseType, Object... urlVariables) {
        RestTemplate template = new RestTemplate();
        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(buildHeader()), responseType, urlVariables);
    }

    private HttpHeaders buildHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("isStandard", "false");// 非标准版
        return headers;
    }

    public String getAddress(String registerId, boolean wantProvince){
        Address address = userService.getAddress(registerId);
        StringBuffer addrs = new StringBuffer();
        if(address != null){
            if(wantProvince){
                    addrs.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getProvince())))
                    .append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCity())));
            }
                    addrs.append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCounty())))
                    .append(StringUtils.trimToEmpty(dictCache.queryArea(address.getTown())))
                    .append(StringUtils.trimToEmpty(dictCache.queryArea(address.getCommittee())));
            if(address.getOther() != null){
                addrs.append(address.getOther());
            }
        }
        return addrs.toString();
    }

    public String repliceUrl(String url, String registerId, String idc){
        if(StringUtils.isBlank(url)){
            return null;
        }
        if(url.contains("{registerId}")){
            url = url.replace("{registerId}",registerId);
        }
        if (url.contains("{idc}")) {
            url = url.replace("{idc}",idc);
        }
        return url;
    }

    public static List<String> cause(Assessment assessment){
        List<String> list = new ArrayList<String>();
        if(assessment == null ){
            return list;
        }
        if(assessment.getAge()>=40){
            list.add("年龄="+assessment.getAge());
        }

        Double bmi = Double.valueOf(new DecimalFormat("##.00").
                format(Double.valueOf(assessment.getWeight())/Math.pow((Double.valueOf(assessment.getHeight())/100), 2)));
        if(bmi>=24){
            list.add( "超重/肥胖");
        }

        if (null != assessment.getWaist() && ((assessment.getGender().equals(AssessmentConstrains.GENDER_MAN) && assessment.getWaist() >= 90 )||
                (assessment.getGender().equals(AssessmentConstrains.GENDER_WOMAN) && assessment.getWaist() >= 85 ))) {
            list.add( "中心性肥胖");
        }
        if(!"0".equals(assessment.getDiabetesRelatives())){
            list.add( "亲属中有糖尿病患者");
        }
        if(!"0".equals(assessment.getHypertensionRelatives())){
            list.add( "亲属中有高血压患者");
        }
        if(!"0".equals(assessment.getStrokeRelatives())){
            list.add( "亲属中有脑卒中患者");
        }
        if(assessment.getIsDrink() != null && 3 == assessment.getIsDrink()){
            list.add( "每天都喝酒");
        }
        if(assessment.getIsSmoking() != null  && (1 == assessment.getIsSmoking() || 2 == assessment.getIsSmoking())){
            list.add(1 == assessment.getIsSmoking() ? "现在每天吸烟" : "现在吸烟，但不是每天吸烟");
        }
        if(assessment.getEatHabits() != null && 1 != assessment.getEatHabits()){
            list.add( 2 == assessment.getEatHabits()?"饮食习惯荤食为主":"饮食习惯素食为主");
        }
        if(assessment.getEatTaste() != null && !"4".equals(assessment.getEatTaste())){
            if(assessment.getEatTaste().contains("1")) list.add( "饮食口味嗜盐");
            if(assessment.getEatTaste().contains("2")) list.add( "饮食口味嗜油");
            if(assessment.getEatTaste().contains("3")) list.add( "饮食口味嗜糖");
        }
        if(assessment.getSport() !=null && (3 == assessment.getSport() || 4 == assessment.getSport())){
            list.add( "严重缺乏运动");
        }
        if(assessment.getPressure() != null && !"0".equals(assessment.getPressure())){
            String[] pressures = assessment.getPressure().split("/");
            Integer diastolic = pressures.length > 1 ? Integer.valueOf(pressures[1]) : 0;
            Integer systolic = Integer.valueOf(pressures[0]);
            if(diastolic >=85 || systolic >=130){
                list.add( "血压"+assessment.getPressure()+"mmHg");
            }
        }
        if(assessment.getTakeAntihypertensiveDrugs() !=null && 1 == assessment.getTakeAntihypertensiveDrugs()){
            list.add( "正在服用降压药");
        }
        if(assessment.getIsDyslipidemia() != null && 1 == assessment.getIsDyslipidemia()){
            list.add( "血脂异常");
        }
        if(assessment.getMedicalHistory() != null && !"6".equals(assessment.getMedicalHistory())){
            if(assessment.getMedicalHistory().contains("1")) list.add( "有糖调节受损（IGR，又称糖尿病前期)");
            if(assessment.getMedicalHistory().contains("2")) list.add( "动脉粥样硬化心脑血管疾病");
            if(assessment.getMedicalHistory().contains("3")) list.add( "有一过性类固醇糖尿病");
            if(assessment.getMedicalHistory().contains("4")) list.add( "房颤或明显的脉搏不齐");
            if(assessment.getMedicalHistory().contains("5")) list.add( "短暂脑缺血发作病史（TIA）");
        }
        if(assessment.getIsDepression() != null && 1 == assessment.getIsDepression()){
            list.add( "长期接受抗精神类药物（或）抗抑郁症药物治疗");
        }
        if("2".equals(assessment.getGender()) && !"4".equals(assessment.getFemaleMedicalHistory())){
            if(assessment.getFemaleMedicalHistory() != null){
                if(assessment.getFemaleMedicalHistory().contains("1")) list.add( "有巨大儿（出生体重>=4KG）生产史");
                if(assessment.getFemaleMedicalHistory().contains("2")) list.add( "有妊娠期糖尿病史");
                if(assessment.getFemaleMedicalHistory().contains("3")) list.add( "多囊卵巢综合症患者");
            }
        }
        return list;
    }
}
