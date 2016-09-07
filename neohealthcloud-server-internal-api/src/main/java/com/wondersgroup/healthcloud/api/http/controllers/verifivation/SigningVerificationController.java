package com.wondersgroup.healthcloud.api.http.controllers.verifivation;

import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.services.doctor.SigningVerficationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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

    @Autowired
    private JedisPool pool;

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

    private static final long millis = 24 * 60 * 60 * 1000;

    @PostMapping(path = "/signing/verification/external")
    public JsonResponseEntity<String> generateExternalVerificationCode(@RequestHeader(name = "token") String token,
                                                                       @RequestBody String request) {
        if (!"5eaa7d91d75f431582f9608b79d835a9".equals(token)) {
            throw new CommonException(1000, "token不正确");
        } else {
            try (Jedis jedis = pool.getResource()) {
                long count = jedis.incr("ext:signing:token:" + token + ":" + System.currentTimeMillis() / millis);
                if (count > 3000) {
                    throw new CommonException(1000, "当天已请求超过3000次");
                }
            }
        }

        JsonKeyReader reader = new JsonKeyReader(request);
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("hospial", reader.readString("hospital", false));
        builder.put("job_number", reader.readString("job_number", false));
        builder.put("doctor_name", reader.readString("doctor_name", false));
        builder.put("doctor_idcard", reader.readString("doctor_idcard", false));
        String mobile = reader.readString("mobile", false);
        String name = reader.readString("patient_name", false);
        String idCard = reader.readString("patient_idcard", false);
        Boolean isDefault = reader.readDefaultBoolean("default", true);

        signingVerficationService.externalDoctorInvitationSend(builder.build(), name, idCard, mobile, isDefault);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setMsg("邀请发送成功");
        return body;
    }
}
