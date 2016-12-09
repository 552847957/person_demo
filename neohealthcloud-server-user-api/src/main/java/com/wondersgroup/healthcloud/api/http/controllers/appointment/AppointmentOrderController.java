package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.AppointmentOrderDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorAppointmentException;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/8.
 */
@RestController
@RequestMapping("/api/reservation")
public class AppointmentOrderController {

    @Autowired
    private AppointmentApiService appointmentApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 发送(验证)短信
     *
     * @param uid 给注册用户发送验证码
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public JsonResponseEntity<String> sendSMS(@RequestParam("uid") String uid) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        RegisterInfo registerInfo = userService.getOneNotNull(uid);
        if(StringUtils.isBlank(registerInfo.getRegmobilephone())){
            throw new ErrorAppointmentException();
        }
        // todo  编辑预约挂号短信文案
        userAccountService.getVerifyCode(registerInfo.getRegmobilephone(), 6);
        response.setData("发送成功");
        return response;
    }

    /**
     * 我的预约列表
     *
     * @param uid
     * @param flag
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/orderHistory", method = RequestMethod.GET)
    public JsonListResponseEntity<AppointmentOrderDTO> history(@RequestParam String uid,
                                                               @RequestParam(value = "flag", defaultValue = "1", required = false) String flag) {
        JsonListResponseEntity<AppointmentOrderDTO> response = new JsonListResponseEntity<>();

        return response;
    }

}
