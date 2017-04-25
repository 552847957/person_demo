package com.wondersgroup.healthcloud.api.http.controllers.remind;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.remind.RemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Admin on 2017/4/19.
 */
@RestController
@RequestMapping(value = "/api/remind")
public class MedicationReminderController {

    @Autowired
    private RemindService remindService;

    @Value("${neohealthcloud-internal.connection.url}")
    private String internalUrl;

    @RequestMapping(value = "/medicationReminder", method = RequestMethod.GET)
    public JsonResponseEntity medicationReminder(@RequestParam String remindId, @RequestParam String remindTimeId) {
        JsonResponseEntity result = new JsonResponseEntity();
        int rtnInt = remindService.medicationReminder(remindId, remindTimeId, internalUrl);
        return result;
    }
}
