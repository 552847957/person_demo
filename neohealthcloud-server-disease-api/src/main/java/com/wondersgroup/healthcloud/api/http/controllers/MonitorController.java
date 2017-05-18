package com.wondersgroup.healthcloud.api.http.controllers;

import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ZZX on 2017/5/18.
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @WithoutToken
    @RequestMapping(value = "/sayHello", method = RequestMethod.GET)
    public JsonResponseEntity sayHello() {
        JsonResponseEntity result = new JsonResponseEntity();
        result.setCode(0);
        result.setMsg("Hello");
        return result;
    }
}
