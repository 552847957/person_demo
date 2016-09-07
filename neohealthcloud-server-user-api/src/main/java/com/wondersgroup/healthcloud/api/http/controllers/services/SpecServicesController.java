package com.wondersgroup.healthcloud.api.http.controllers.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.user.HealthActivityInfoService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.healthactivity.HealthActivityAPIEntity;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/30.
 */
@RestController
@RequestMapping("/api/spec/services")
public class SpecServicesController {

    private static final Logger log = Logger.getLogger(SpecServicesController.class);

    @Autowired
    private ImageTextService imageTextService;
    @Autowired
    private HealthActivityInfoService haiService;
    @Autowired
    private UserService userService;
    
    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String requestFamilyPath = "%s/api/measure/family/nearest?%s";
    private RestTemplate template = new RestTemplate();

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> list(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                        @RequestHeader(value = "spec-area", required = false) String specArea,
                                                        @RequestHeader(value = "app-version", required = true) String version,
                                                        @RequestHeader(value="screen-width")String width,
                                                        @RequestHeader(value="screen-height")String height,
                                                        @RequestParam(required = false) String registerId) {
        JsonResponseEntity<Map<String, Object>> result = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();
        List<ImageText> imageTexts = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_SERVICE_BTN.getType(), version);
        if (imageTexts != null && imageTexts.size() > 0) {
            List funcList = new ArrayList<>();
            Map map = null;
            for (ImageText imageText : imageTexts) {
                map = new HashMap<>();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", imageText.getHoplink());
                map.put("mainTitle", imageText.getMainTitle());
                map.put("subTitle", imageText.getSubTitle());
                funcList.add(map);
            }
            data.put("services", funcList);
        }

        // 健康活动 不区分区域-2016/09/06测试(TY)确认
        List<HealthActivityInfo> infoList = haiService.getHealthActivityInfos(null, fillingArea(mainArea), null, 1, 1);
        if (infoList != null && infoList.size() > 0) {
            HealthActivityInfo healthActivityInfo = infoList.get(0);
            HealthActivityAPIEntity entity = new HealthActivityAPIEntity(healthActivityInfo,width,height);
            data.put("activities", entity);
        }

        // 近期异常指标 begin
        try {
            RegisterInfo info = userService.getOneNotNull(registerId);
            String parameters = "registerId=".concat(registerId).concat("&personCard=0").concat("&sex=" + info.getGender());
            String url = String.format(requestFamilyPath, host, parameters);
            ResponseEntity<Map> response = template.getForEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                Map<String, Object> responseBody = response.getBody();
                if (0 == (int) responseBody.get("code")) {
                    ObjectMapper om = new ObjectMapper();
                    String jdata = om.writeValueAsString(new JsonResponseEntity<>(0, null, responseBody.get("data")));
                    JsonNode jsonNode = om.readTree(jdata);
                    JsonNode unusuals = jsonNode.findPath("unusual");
                    if (unusuals != null && unusuals.get(0) != null) {
                        JsonNode unusual = unusuals.get(0);
                        Map<String, Object> rtnMap = new HashMap();
                        switch (unusual.get("flag").asText()){
                            /*case "0": rtnMap.put("status", "正常"); break;*/
                            case "1":// 偏高
                                rtnMap.put("prefix", "您最近一次测量" + unusual.get("name").asText());
                                rtnMap.put("status", "偏高");
                                rtnMap.put("color", "#F54949");
                                rtnMap.put("suffix", "哦~");
                                break;
                            case "2":// 偏低
                                rtnMap.put("prefix", "您最近一次测量" + unusual.get("name").asText());
                                rtnMap.put("status", "偏低");
                                rtnMap.put("color", "#F54949");
                                rtnMap.put("suffix", "哦~");
                                break;
                        }
                        if (rtnMap.size() > 0) {
                            data.put("signMeasurements", rtnMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("请求测量数据异常-->" + e.getLocalizedMessage());
        }
        // 未登录或无异常情况下显示默认数据-2016/09/06测试(XJJ)确认
        if (data.get("signMeasurements") == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("prefix", "健康测量，体征记录");
            data.put("signMeasurements", map);
        }
        // 近期异常指标 end
        if (data.size() > 0) {
            result.setData(data);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息！");
        }

        return result;
    }

    private String fillingArea(String areaId) {
        if (StringUtils.isNotEmpty(areaId)) {
            if (areaId.length() < 12) {
                int missing = 12 - areaId.length();
                for (int i = 0; i < missing; i++) {
                    areaId += "0";
                }
            }
        }
        return areaId;
    }
}
