package com.wondersgroup.healthcloud.api.http.controllers.common;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.UploaderUtil;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by longshasha on 16/8/12.
 */

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    private static final Logger log = Logger.getLogger(CommonController.class);


    @RequestMapping(value = "/getQiniuToken", method = RequestMethod.GET)
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
