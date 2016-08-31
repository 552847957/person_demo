package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.UploaderUtil;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by longshasha on 16/8/12.
 */

@RestController
@RequestMapping("/api/doctor/common")
public class CommonController {
    private static final Logger log = Logger.getLogger(CommonController.class);


    /**
     * APP获取启动数据
     */
    @RequestMapping(value = "/appConfig", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> appConfig(@RequestHeader(value = "platform", required = false) String platform,
                                                             @RequestHeader(value = "screen-width", required = false) String width,
                                                             @RequestHeader(value = "screen-height", required = false) String height,
                                                             @RequestHeader(value = "app-version", required = false) String appVersion,
                                                             @RequestHeader(value = "main-area", required = true) String mainArea,
                                                             @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> common = new HashMap<>();
        common.put("publicKey", HttpWdUtils.publicKey);

        data.put("common",common);
        response.setData(data);
        return response;

    }

    @RequestMapping(value = "/getQiniuToken", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> qiniuConfig() {
        JsonResponseEntity<Map<String, Object>> response = new JsonResponseEntity<Map<String, Object>>();
        Map<String, Object> map = Maps.newHashMap();
        map.put("token", UploaderUtil.getUpToken());
        map.put("expires", UploaderUtil.expires);
        map.put("domain", UploaderUtil.domain);
        response.setData(map);
        return response;
    }



}
