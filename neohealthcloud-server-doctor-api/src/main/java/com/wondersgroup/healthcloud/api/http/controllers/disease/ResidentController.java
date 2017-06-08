package com.wondersgroup.healthcloud.api.http.controllers.disease;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by limenghua on 2017/6/7.
 *
 * @author limenghua
 */
@RestController
@RequestMapping(value = "/api")
public class ResidentController {

    @GetMapping("/group/patientList")
    public JsonListResponseEntity list(
            @RequestParam(required = true) String groupId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer pageSize) {
        JsonListResponseEntity response = new JsonListResponseEntity();

        return null;
    }
}
