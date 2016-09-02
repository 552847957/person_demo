package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.services.doctor.SigningVerficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "/signing/verification/external")
    public JsonResponseEntity<String> generateExternalVerificationCode(@RequestHeader(name = "token") String token,
                                                                       @RequestBody String request) {
        if (!"5eaa7d91d75f431582f9608b79d835a9".equals(token)) {
            throw new CommonException(1000, "token不正确");
        }
        JsonKeyReader reader = new JsonKeyReader(request);
        String hospitalId = reader.readString("hospital", false);
        String jobNumber = reader.readString("job_number", false);
        String doctorName = reader.readString("doctor_name", false);
        String doctorIdcard = reader.readString("doctor_idcard", false);
        String mobile = reader.readString("mobile", false);
        String name = reader.readString("patient_name", false);
        String idCard = reader.readString("patient_idcard", false);
        Boolean isDefault = reader.readDefaultBoolean("default", true);

//        signingVerficationService.doctorInvitationSend(doctorId, name, idCard, mobile, isDefault);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("邀请发送成功");
        return body;
    }
}
