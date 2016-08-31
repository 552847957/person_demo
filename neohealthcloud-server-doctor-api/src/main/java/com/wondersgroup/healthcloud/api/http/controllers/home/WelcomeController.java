package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ys on 16/8/31.
 *
 */
@RestController
@RequestMapping("/api")
public class WelcomeController {

    @RequestMapping(value = "", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity welcome(){
        JsonResponseEntity body = new JsonResponseEntity();
        Map<String, Object> info = new HashMap<>();
        info.put("version", "3.0.0");
        body.setData(info);
        return body;
    }

}
