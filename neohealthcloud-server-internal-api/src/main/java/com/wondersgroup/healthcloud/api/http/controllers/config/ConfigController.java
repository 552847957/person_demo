package com.wondersgroup.healthcloud.api.http.controllers.config;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/17.
 */
@RestController
@RequestMapping("/api/appConfig")
public class ConfigController {

    @Autowired
    private AppConfigService appConfigService;

    @Value("${yyservice.user.register.count}")
    private String yyServiceSignCount;

    /**
     * 获取yy服务签约的人数
     */
    @Admin
    @RequestMapping(value = "/yyService/signNum", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, String>> yyServiceSignNum() {
        JsonResponseEntity<Map<String, String>> entity = new JsonResponseEntity<>();
        String signCountStr = StringUtils.isNotEmpty(yyServiceSignCount) ? yyServiceSignCount : "";
        int strLen = signCountStr.length();
        Map<String, String> map = new HashMap<>();
        map.put("q", strLen >= 4 ? signCountStr.substring(strLen-4, strLen-3) : "0");
        map.put("b", strLen >= 3 ? signCountStr.substring(strLen-3, strLen-2) : "0");
        map.put("s", strLen >= 2 ? signCountStr.substring(strLen-2, strLen-1) : "0");
        map.put("g", strLen >= 1 ? signCountStr.substring(strLen-1) : "0");
        entity.setData(map);
        return entity;
    }

    /**
     * 根据关键字查询单个APP配置信息
     *
     * @param keyWord
     * @return
     */
    @Admin
    @RequestMapping(value = "/findSingleAppConfigByKeyWord", method = RequestMethod.GET)
    public JsonResponseEntity<AppConfig> findSingleAppConfigByKeyWord(@RequestHeader(name = "main-area", required = true) String mainArea,
                                                                      @RequestHeader(name = "spec-area", required = false) String specArea,
                                                                      @RequestParam(required = true) String keyWord,
                                                                      @RequestParam(required = false, defaultValue = "1") String source) {
        JsonResponseEntity<AppConfig> result = new JsonResponseEntity<>();
        // 全局接口不需指定区级区域ID
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, keyWord, source);
        if (appConfig != null) {
            result.setData(appConfig);
        } else {
            result.setCode(1001);
            result.setMsg("未查询到相关配置信息！");
        }
        return result;
    }

    /**
     * 查询全部离散APP配置数据
     * @return
     */
    @Admin
    @RequestMapping(value = "/findAllDiscreteAppConfig", method = RequestMethod.GET)
    public JsonResponseEntity<List<AppConfig>> findAllDiscreteAppConfig(@RequestHeader(name = "main-area", required = true) String mainArea,
                                                                        @RequestHeader(name = "spec-area", required = false) String specArea,
                                                                        @RequestParam(required = false, defaultValue = "1") String source) {
        JsonResponseEntity<List<AppConfig>> result = new JsonResponseEntity<>();
        List<AppConfig> appConfigs = appConfigService.findAllDiscreteAppConfig(mainArea, specArea, source);
        if (appConfigs != null && appConfigs.size() > 0) {
            result.setData(appConfigs);
        } else {
            result.setCode(1001);
            result.setMsg("未查询到相关配置信息！");
        }
        return result;
    }

    /**
     * 保存配置信息
     *
     * @param appConfig
     * @return
     */
    @Admin
    @RequestMapping(value = "/saveAppConfig", method = RequestMethod.POST)
    public JsonResponseEntity saveAppConfig(@RequestHeader(name = "main-area", required = true) String mainArea,
                                            @RequestHeader(name = "spec-area", required = false) String specArea,
                                            @RequestHeader(required = true) String source, @RequestBody AppConfig appConfig) {
        JsonResponseEntity result = new JsonResponseEntity();
        appConfig.setSource(source);
        appConfig.setMainArea(mainArea);
        if (StringUtils.isNotEmpty(specArea)) {
            appConfig.setSpecArea(specArea);
        }
        AppConfig rtnAppConfig = appConfigService.saveAndUpdateAppConfig(appConfig);
        if (rtnAppConfig != null) {
            result.setMsg("配置信息保存成功！");
        } else {
            result.setCode(1001);
            result.setMsg("配置信息保存失败！");
        }
        return result;
    }
}
