package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.wondersgroup.healthcloud.api.utls.CommonUtils;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesAssessmentService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
    @Autowired
    private UserService userService;
    @Autowired
    private AppUrlH5Utils h5Utils;
    @Autowired
    private DiabetesAssessmentService diabetesAssessmentService;

    @RequestMapping(value = "/measureHistory", method = RequestMethod.GET)
    public JsonResponseEntity measureHistory(@RequestParam(name = "uid") String registerId,
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
                        Map<String, Object> dataMap = new HashMap<>();
                        DateTime today = new DateTime(new Date());
                        //List<JsonNode> lastWeekData = new ArrayList<>();
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();
                            String date = jsonNode.get("date").asText();
                            if (date != null
                                    && (new DateTime(date).isAfter(new DateTime(today).plusDays(-6).withTimeAtStartOfDay().getMillis())
                                    || new DateTime(date).isEqual(new DateTime(today).plusDays(-6).withTimeAtStartOfDay().getMillis()))
                                    && new DateTime(date).isBefore(new DateTime(today).plusDays(1).withTimeAtStartOfDay())) {
                                //lastWeekData.add(jsonNode);
                            }

                            Iterator<JsonNode> dataJson = jsonNode.get("data").iterator();
                            if (dataMap.get("lastData") == null) {
                                while (dataJson.hasNext()) {
                                    JsonNode lastData = dataJson.next();
                                    if (dataMap.get("lastData") == null) {
                                        dataMap.put("lastData", lastData);
                                        continue;
                                    }
                                    if (dataMap.get("secondLastData") == null) {
                                        dataMap.put("secondLastData", lastData);
                                        break;
                                    }
                                }
                            }

                            if (dataMap.get("secondLastData") == null) {
                                while (dataJson.hasNext()) {
                                    JsonNode lastData = dataJson.next();
                                    if (dataMap.get("secondLastData") == null) {
                                        dataMap.put("secondLastData", lastData);
                                        break;
                                    }
                                }
                            }
                        }
                        String assessmentResult = diabetesAssessmentService.getLastAssessmentResult(url);
                        if (StringUtils.isNotEmpty(assessmentResult)) {
                            dataMap.put("assessmentResult", assessmentResult);
                        }
                        //dataMap.put("lastWeekData", lastWeekData);
                        result.setData(dataMap);
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
}
