package com.wondersgroup.healthcloud.api.http.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wondersgroup.healthcloud.api.utls.CommonUtils;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;

@RestController
@RequestMapping("/api/measure/")
public class MeasureChartController {

    public static final Logger logger = LoggerFactory.getLogger("exlog");
    private static final String requestHistoryByArrayDay = "%s/api/measure/3.0/getHistoryByArrayDay?%s";
    private static final String BLOODGLUCOSE_TOP_ONE = "%s/api/measure/3.0/getBloodGlucoseTopOne?%s";
    @Value("${internal.api.service.measure.url}")
    private String host;
    private RestTemplate template = new RestTemplate();

    /**
     * 查询一周血压
     * 根据天的维度展示
     * @param registerId
     * @return json
     * @throws JsonProcessingException
     */
    @GetMapping("getHistoryByArrayDay")
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
//                String url = "http://127.0.0.1:8080/api/measure/3.0/getHistoryByArrayDay?registerId=ff80808154177829015417bbe1970020&sex=1&dayAmount=2";

                ResponseEntity<Map> response = buildGetEntity(url, Map.class);
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    Map<String, Object> responseBody = response.getBody();
                    if (0 == (int) responseBody.get("code"))
                        return new JsonResponseEntity<>(0, null, responseBody.get("data"));
                }
            } catch (RestClientException e) {
                logger.info("请求测量数据异常", e);
            }
            return new JsonResponseEntity<>(1000, "内部错误");

    }
    
    /**
     * 查询7天是否有血糖测量
     * @param registerId
     * @return json
     * @throws JsonProcessingException
     */
    @GetMapping("getWeekIsExistByArrayDay")
    public JsonResponseEntity getWeekIsExistByArrayDay(String registerId, String date){
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
                logger.info("请求测量数据异常", e);
            }
            return new JsonResponseEntity<>(1000, "内部错误");

    }

    private HttpHeaders buildHeader(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String version = request.getHeader("version");
        boolean isStandard =  CommonUtils.compareVersion(version, "3.1");
        HttpHeaders headers = new HttpHeaders();
        headers.add("isStandard", String.valueOf(isStandard));
        return headers;
    }

    private <T> ResponseEntity<T> buildGetEntity(String url, Class<T> responseType, Object... urlVariables){
        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(buildHeader()), responseType, urlVariables);
    }

}
