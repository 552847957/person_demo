package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountAndSessionDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by longshasha on 16/5/11.
 */
@RestController
@RequestMapping("/api")
public class DoctorAccessTokenController {
    @Autowired
    private DoctorAccountService doctorAccountService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorController doctorController;

    @WithoutToken
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAccountAndSessionDTO> fetchToken(
            @RequestHeader(value = "main-area", required = true) String mainArea,
            @RequestParam String account,
            @RequestParam String password
    ) {
        JsonResponseEntity<DoctorAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new DoctorAccountAndSessionDTO(doctorAccountService.login(account, password,mainArea)));
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }

    @WithoutToken
    @RequestMapping(value = "/fastLogin", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAccountAndSessionDTO> fastFetchToken(
            @RequestHeader(value = "main-area", required = true) String mainArea,
            @RequestParam String mobile,
            @RequestParam String verify_code) {
        JsonResponseEntity<DoctorAccountAndSessionDTO> body = new JsonResponseEntity<>();
        body.setData(new DoctorAccountAndSessionDTO(doctorAccountService.fastLogin(mobile, verify_code,false,mainArea)));//改为false
        body.setMsg("登录成功");
        attachInfo(body);
        return body;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> deleteToken(@RequestHeader("access-token") String token) {
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        doctorAccountService.logout(token);
        body.setMsg("退出成功");
        return body;
    }

    private void attachInfo(JsonResponseEntity<DoctorAccountAndSessionDTO> body) {
        body.getData().setInfo(doctorController.getDoctorInfo(body.getData().getUid()));
    }


}
