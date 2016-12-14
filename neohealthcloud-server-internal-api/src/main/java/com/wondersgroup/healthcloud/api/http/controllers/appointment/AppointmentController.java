package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by longshasha on 16/12/14.
 */
@RestController
@RequestMapping(value = "/api/reservation")
public class AppointmentController {

    @Autowired
    private AppointmentApiService appointmentApiService;

    @RequestMapping(value = "/isOn", method = RequestMethod.GET)
    public JsonResponseEntity doctorDetail(
            @RequestHeader(name = "main-area", required = true) String mainArea) {
        JsonResponseEntity<Map> body = new JsonResponseEntity<>();
        Map<String,Object> map = new HashMap<>();
        Boolean isOn = appointmentApiService.getRegistrationIsOn(mainArea);
        map.put("registrationIsOn",isOn);
        body.setData(map);
        return body;
    }

}
