package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.api.utls.CommonUtils;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zhaozhenxing on 2016/12/15.
 */
@RestController
@RequestMapping("/api/diabetesHome")
public class DiabetesHomeController {

    private static final Logger log = Logger.getLogger("exlog");
    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String recentMeasureHistory = "%s/api/measure/3.0/recentHistory/%s?%s";
    private static final String queryNearestHistoryByTestPeriod = "%s/api/measure/3.0/queryNearestHistoryByTestPeriod?%s";
    @Autowired
    private UserService userService;

    @Autowired
    private DiabetesAssessmentService diabetesAssessmentService;

    @RequestMapping(value = "/measureHistory", method = RequestMethod.GET)
    public JsonResponseEntity measureHistory(@RequestParam(name = "uid") String registerId,
                                             @RequestParam(required = false) String personCard) {
        JsonResponseEntity result = new JsonResponseEntity();
        try {
            RegisterInfo info = userService.getOneNotNull(registerId);
            String param = "registarId=".concat(registerId)
                    .concat("&persionCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard())
                    .concat("&testPeriod=").concat(compareTime(true));
            String url = String.format(queryNearestHistoryByTestPeriod, host, param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.iterator();
                        Map<String, Object> dataMap = new HashMap<>();
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();
                            if (jsonNode.get("testPeriod") != null && jsonNode.get("testPeriod").asText().equals(compareTime(true))) {
                                dataMap.put("lastData", jsonNode);
                                if (contentJson.hasNext()) {
                                    dataMap.put("secondLastData", contentJson.next());
                                }
                                break;
                            } else {
                                dataMap.put("secondLastData", jsonNode);
                                break;
                            }
                        }
                        Map<String, Object> assessmentResult = diabetesAssessmentService.getLastAssessmentResult(registerId);
                        if (assessmentResult != null) {
                            dataMap.put("assessmentResult", assessmentResult);
                        }
                        result.setData(dataMap);
                    }
                }
            }
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }

    @RequestMapping(value = "/lastMeasure", method = RequestMethod.GET)
    public JsonResponseEntity lastMeasure(@RequestParam(name = "uid") String registerId,
                                          @RequestParam(required = false) String personCard) {
        JsonResponseEntity result = new JsonResponseEntity();
        try {
            RegisterInfo info = userService.getOneNotNull(registerId);
            String param = "registerId=".concat(registerId)
                    .concat("&sex=").concat(StringUtils.isEmpty(info.getGender()) ? "1" : info.getGender())
                    .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard());
            String url = String.format(recentMeasureHistory, host, "3", param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.get("content").iterator();
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();
                            Iterator<JsonNode> dataJson = jsonNode.get("data").iterator();
                            while (dataJson.hasNext()) {
                                result.setData(dataJson.next());
                                return result;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }

    private <T> ResponseEntity<T> buildGetEntity(String url, Class<T> responseType, Object... urlVariables) {
        RestTemplate template = new RestTemplate();
        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(buildHeader()), responseType, urlVariables);
    }

    private HttpHeaders buildHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String version = request.getHeader("version");
        boolean isStandard = CommonUtils.compareVersion(version, "4.1");
        HttpHeaders headers = new HttpHeaders();
        headers.add("isStandard", String.valueOf(isStandard));
        return headers;
    }

    /**
     * 比较当前时间为那个时间段
     * @param isRtnNumber
     * @return
     */
    public String compareTime(boolean isRtnNumber) {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime now = DateTime.now();
        String date = DateFormatter.dateFormat(now.toDate());
        if (!DateTime.parse(date + " 00:00:01", format).isAfter(now)
                && DateTime.parse(date + " 05:00:00", format).isAfter(now)) {
            return isRtnNumber ? "7" : "凌晨";
        }
        if (!DateTime.parse(date + " 05:00:01", format).isAfter(now)
                && DateTime.parse(date + " 08:00:00", format).isAfter(now)) {
            return isRtnNumber ? "0" : "早餐前";
        }
        if (!DateTime.parse(date + " 08:00:01", format).isAfter(now)
                && DateTime.parse(date + " 10:00:00", format).isAfter(now)) {
            return isRtnNumber ? "1" : "早餐后";
        }
        if (!DateTime.parse(date + " 10:00:01", format).isAfter(now)
                && DateTime.parse(date + " 12:00:00", format).isAfter(now)) {
            return isRtnNumber ? "2" : "午餐前";
        }
        if (!DateTime.parse(date + " 12:00:01", format).isAfter(now)
                && DateTime.parse(date + " 15:00:00", format).isAfter(now)) {
            return isRtnNumber ? "3" : "午餐后";
        }
        if (!DateTime.parse(date + " 15:00:01", format).isAfter(now)
                && DateTime.parse(date + " 18:00:00", format).isAfter(now)) {
            return isRtnNumber ? "4" : "晚餐前";
        }
        if (!DateTime.parse(date + " 18:00:01", format).isAfter(now)
                && DateTime.parse(date + " 20:00:00", format).isAfter(now)) {
            return isRtnNumber ? "5" : "晚餐后";
        }
        if (!DateTime.parse(date + " 20:00:01", format).isAfter(now)
                && DateTime.parse(date + " 23:59:59", format).isAfter(now)) {
            return isRtnNumber ? "6" : "睡前";
        }
        return null;
    }
}
