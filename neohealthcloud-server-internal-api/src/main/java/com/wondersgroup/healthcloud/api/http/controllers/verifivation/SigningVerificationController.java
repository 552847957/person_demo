package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.services.doctor.SigningVerficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/25/16.
 */
@RestController
@RequestMapping(path = "/api")
public class SigningVerificationController {

    @Autowired
    private SigningVerficationService signingVerficationService;

    @PostMapping(path = "/signing/verification")
    public JsonResponseEntity<String> generateVerificationCode(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String doctorId = reader.readString("doctor_id", false);
        String mobile = reader.readString("mobile", false);
        String name = reader.readString("name", false);
        String idCard = reader.readString("idcard", false);
        Boolean isDefault = reader.readDefaultBoolean("default", true);

        signingVerficationService.doctorInvitationSend(doctorId, name, idCard, mobile, isDefault);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("邀请发送成功");
        return body;
    }
}
