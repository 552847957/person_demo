package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorAccountService doctorAccountService;


    /**
     * 根据uid获取医生详情
     * @param uid
     * @return
     */
    @RequestMapping(value = "/doctor/info", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> info(@RequestParam String uid) {
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(uid);
        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        response.setData(doctorAccountDTO);
        return response;
    }

    /**
     * 获取验证码 type : 0 默认、1 手机动态码登录、2 重置密码
     * @param mobile
     * @param type
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/getVerificationCodes", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<String> getVerificationCodes(@RequestParam String mobile,
                                                           @RequestParam(defaultValue = "0") Integer type)  {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        doctorAccountService.getVerifyCode(mobile, type);
        response.setMsg("短信验证码发送成功");
        return response;
    }

    /**
     * 验证验证码
     * @param mobile
     * @param verifyCode
     * @return
     */
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/checkVerificationCodes", method = RequestMethod.GET)
    public JsonResponseEntity<String> validateCode(@RequestParam("mobile") String mobile,
                                                   @RequestParam("verify_code") String verifyCode) {
        Boolean result = doctorAccountService.validateCode(mobile, verifyCode, false);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setCode(result ? 0 : 1002);
        body.setMsg(result ? "短信验证码验证通过" : "短信验证码验证错误");
        return body;
    }

    /**
     * 重置密码
     * @param request
     * @return
     */
    @VersionRange
    @WithoutToken
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public JsonResponseEntity<String> resetPassword(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String verifyCode = reader.readString("verify_code", false);
        String password = reader.readString("password", false);

        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        Boolean verifyCodeResult = doctorAccountService.resetPassword(mobile, verifyCode, password);
        if (!verifyCodeResult) {
            response.setCode(1003);
            response.setMsg("短信验证码错误");
        }
        response.setMsg(verifyCodeResult ? "恭喜, 密码设置成功" : "密码设置失败");
        return response;
    }



    /**
     * 根据医生id获取用户信息
     * @param id
     * @return
     */
    public DoctorAccountDTO getDoctorInfo(String id) {
        Map<String,Object> doctor = doctorService.findDoctorInfoByUid(id);
        if(doctor == null){
            throw new ErrorDoctorAccountNoneException();
        }
        DoctorAccountDTO doctorAccountDTO = new DoctorAccountDTO(doctor);

        return doctorAccountDTO;
    }
}
