package com.wondersgroup.healthcloud.api.http.controllers.measure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wondersgroup.healthcloud.api.utils.CommonUtils;
import com.wondersgroup.healthcloud.api.utils.JacksonHelper;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.measure.MeasureManagement;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.measure.MeasureManagementService;
import com.wondersgroup.healthcloud.services.user.UserService;

/**
 * Created by Jeffrey on 16/8/19.
 */
@RestController
@RequestMapping("api/measure/")
public class MeasureController {

    private static final Logger log = LoggerFactory.getLogger(MeasureController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;

    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String requestFamilyPath = "%s/api/measure/family/nearest?%s";
    private static final String requestUploadPath = "%s/api/measure/upload/%s";
    private static final String requestModifyPath = "%s/api/measure/3.0/modify/%s";
    private static final String requestChartPath = "%s/api/measure/chart/%s?%s";
    private static final String requestDayHistoryPath = "%s/api/measure/dayHistory/?%s";
    private static final String requestYearHistoryPath = "%s/api/measure/yearHistory/%s?%s";
    private static final String requestDayHistoriesListPath = "%s/api/measure/topHistories/?%s";
    private static final String recentMeasureHistory = "%s/api/measure/3.0/recentHistory/%s?%s";
    private static final String requestAbnormalHistories = "%s/api/measure/3.0/dayHistory?%s";
    private RestTemplate template = new RestTemplate();
    
    @Autowired
    private MeasureManagementService managementService;

    @Autowired
    private AppUrlH5Utils h5Utils;

    @GetMapping(value = "home", produces = MediaType.APPLICATION_JSON_VALUE)
    @VersionRange
    public String measureHome(String registerId) {

        RegisterInfo info = userService.findRegOrAnonymous(registerId);
        List histories = new ArrayList();
        String parameters = "registerId=".concat(registerId).concat("&sex=").concat(getGender(info.getGender()))
                .concat("&personCard=").concat(getPersonCard(info.getPersoncard()));
        String url = String.format(requestDayHistoriesListPath, host, parameters);
        ResponseEntity<Map> response = buildGetEntity(url, Map.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            Map body = response.getBody();
            if (0 == (int) body.get("code")) {
                histories = (List) body.get("data");
            }
        }
        boolean hashMore = histories.size() > 4;
        Map<String, Object> homeMap = new HashMap<>();
        homeMap.put("types", managementService.displays());
        homeMap.put("more", hashMore);
        homeMap.put("histories", hashMore ? histories.subList(0, 4) : histories);
        JsonResponseEntity<Map> result = new JsonResponseEntity<>(0, null);
        result.setData(homeMap);
        Map<Class, String[]> filters = new HashMap<>();
        filters.put(MeasureManagement.class, new String[]{"id", "createdDate", "lastModifiedDate", "createdBy", "lastModifiedBy", "display"});
        return JacksonHelper.getInstance().serializeExclude(filters).writeValueAsString(result);
    }

    @VersionRange
    @GetMapping("family/nearest")
    public JsonResponseEntity<?> nearestMeasure(@RequestParam String familyMateId) {
        try {
            RegisterInfo info = userService.findRegOrAnonymous(familyMateId);
            String parameters = "registerId=".concat(familyMateId).concat("&sex=").concat(getGender(info.getGender()))
                    .concat("&personCard=").concat(getPersonCard(info.getPersoncard()));
            String url = String.format(requestFamilyPath, host, parameters);
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
//                    if (type == 0) {
//                        String registerId = (String) paras.get("registerId");
//                        String heightInitValue = (String) paras.get("height");
//                        String weightInitValue = (String) paras.get("weight");
//                        Integer height = Integer.parseInt(heightInitValue.contains(".") ? heightInitValue.substring(0, heightInitValue.indexOf(".")) : heightInitValue);
//                        Float weight = Float.parseFloat((weightInitValue.contains(".") ? weightInitValue.substring(0, weightInitValue.indexOf(".")) : weightInitValue));
//                        UserInfoForm userInfoForm = new UserInfoForm(registerId, height, weight);
//                        userService.updateUserHeightAndWeight(userInfoForm);
//                    }
                    return new JsonResponseEntity<>(0, "数据上传成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("上传体征数据失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据上传失败");
    }

    @VersionRange
    @PostMapping("modify/{type}")
    public JsonResponseEntity<?> updateMeasureIndexs(@PathVariable int type, @RequestBody Map<String, Object> paras) {
        try {
        	String registerId = (String) paras.get("registerId");
        	RegisterInfo info = userService.findRegOrAnonymous(registerId);
        	
        	String personCard = info.getPersoncard();
        	if(personCard != null){
        		paras.put("personCard", personCard);
        	}
        	
            String url = String.format(requestModifyPath, host, type);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add("access-token", "version3.0");
            ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "数据更新成功");
                }
            }
        } catch (RestClientException e) {
            log.info("体征数据更新失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据更新失败");
    }

    /**
     * 查询测量趋势图
     *
     * @param type       测量类型
     * @param registerId 注册认证码
     * @param flag       分页 页码
     * @return
     */
    @VersionRange
    @GetMapping("chart/{type}")
    public JsonResponseEntity queryMeasureChart(@PathVariable int type, String registerId, int flag) {
    	 RegisterInfo info = userService.findRegOrAnonymous(registerId);
        try {
            String params = "registerId=".concat(registerId).concat("&flag=").concat(String.valueOf(flag)).concat("&personCard=").concat(getPersonCard(info.getPersoncard()));
            String url = String.format(requestChartPath, host, type, params);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "数据查询成功", response.getBody().get("data"));
                }
            }
        } catch (Exception e) {
            log.info("type =" + type + "的图表数据获取失败", e);
        }
        return new JsonResponseEntity(1000, "数据获取失败");
    }

    /**
     * 查询历史记录
     * 根据年的维度展示,不分页
     *
     * @param type       测量类型
     * @param registerId 注册认证码
     * @return
     * @throws JsonProcessingException
     */
    @VersionRange
    @GetMapping("yearHistory/{type}")
    public JsonResponseEntity queryMeasureHistory(@PathVariable int type, String registerId) throws JsonProcessingException {
        try {
            RegisterInfo info = userService.findRegOrAnonymous(registerId);
            String url = String.format(requestYearHistoryPath, host, type, "registerId=".concat(registerId).concat("&sex=").concat(getGender(info.getGender()))
                    .concat("&personCard=").concat(getPersonCard(info.getPersoncard())));
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "历史数据查询成功", response.getBody().get("data"));
                }
            }
        } catch (Exception e) {
            log.info("type =" + type + "的历史数据查询失败", e);
        }
        return new JsonResponseEntity(1000, "历史数据查询失败");
    }

    /**
     * 查询历史记录
     * 根据天的维度展示
     *
     * @param registerId 注册认证码
     * @param flag       分页 测量时间
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("dayHistory")
    public JsonResponseEntity queryMeasureHistory(String registerId, String flag) throws JsonProcessingException {
        try {
            RegisterInfo info = userService.findRegOrAnonymous(registerId);
            String param = "registerId=".concat(registerId).concat("&sex=").concat(getGender(info.getGender()))
                    .concat("&personCard=").concat(getPersonCard(info.getPersoncard()));
            String params = (flag == null) ? param : param.concat("&flag=").concat(flag);
            String url = String.format(requestDayHistoryPath, host, params);
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

    /**
     * 查询历史记录 3.0
     * 根据天的维度展示
     *
     * @param registerId 注册认证码
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping("3.0/abnormal/dayHistory")
    public JsonResponseEntity queryAbnormalHistory(@RequestParam String registerId, String personCard) throws JsonProcessingException {
        Map<String, Object> result = new HashMap<>();

        try {
            RegisterInfo info = userService.findRegOrAnonymous(registerId);
            if(StringUtils.isEmpty(info.getPersoncard()) ){
                result.put("h5Url", Collections.EMPTY_MAP );
            }else{
                personCard = info.getPersoncard();
                result.put("h5Url", h5Utils.generateLinks(personCard)) ;
            }
            String param = "registerId=".concat(registerId).concat("&sex=").concat(getGender(info.getGender()))
                    .concat("&personCard=").concat(getPersonCard(info.getPersoncard()));
            String url = String.format(requestAbnormalHistories, host, param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    Object content = response.getBody().get("data");
                    result.put("content", content);
                    return new JsonResponseEntity<>(0, "近期异常数据查询成功", result);
                }
            }
        } catch (Exception e) {
            log.info("近期异常数据获取失败", e);
            return new JsonResponseEntity(1000, "近期异常数据获取失败");
        }
        return new JsonResponseEntity(0, "查询成功", result);
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
    public JsonResponseEntity getRecentMeasureHistory(@PathVariable int type, Integer flag, String registerId) throws JsonProcessingException {
        try {
            RegisterInfo info = userService.findRegOrAnonymous(registerId);
            String gender = info.getGender();
            String personcard = info.getPersoncard();
           
            String param = "registerId=".concat(registerId).concat("&sex=").concat(getGender(gender))
                    .concat("&personCard=").concat(getPersonCard(personcard));
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

    public String getGender(String gender){
        return StringUtils.isEmpty(gender) ? "1" : gender;
    }
    
    public String getPersonCard(String personcard){
        return StringUtils.isEmpty(personcard) ? "" : personcard;
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
