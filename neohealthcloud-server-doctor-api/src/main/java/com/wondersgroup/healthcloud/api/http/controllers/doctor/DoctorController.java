package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorInforUpdateLengthException;
import org.apache.commons.lang3.StringUtils;
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

    @RequestMapping(value = "/doctor/updateIntro", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> updateIntro(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);

        String uid = reader.readString("uid", false);
        String intro = reader.readString("intro", true);

        if(StringUtils.isNotBlank(intro) && intro.length()>500){
            throw new ErrorDoctorInforUpdateLengthException("个人简介不能超过500字");
        }

        DoctorInfo doctorInfo = doctorService.updateIntro(uid,intro);

        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(uid);
        response.setData(doctorAccountDTO);
        response.setMsg("用户信息设置成功");
        return response;
    }

    /**
     * 修改医生疾病特长
     * @param body
     * @return
     */
    @RequestMapping(value = "/doctor/updateExpertin", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> updateExpertin(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);

        String uid = reader.readString("uid", false);
        String expertin = reader.readString("expertin", true);

        if(StringUtils.isNotBlank(expertin) && expertin.length()>500){
            throw new ErrorDoctorInforUpdateLengthException("擅长疾病不能超过500字");
        }

        doctorService.updateExpertin(uid,expertin);

        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(uid);
        response.setData(doctorAccountDTO);
        response.setMsg("用户信息设置成功");
        return response;
    }

    /**
     * 修改用户头像
     * @param body
     * @return
     */
    @RequestMapping(value = "/doctor/updateAvatar", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> updateInfo(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String uid = reader.readString("uid", false);
        String avatar = reader.readString("avatar", true);

        doctorService.updateDoctorAvatar(uid,avatar);

        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(uid);
        response.setData(doctorAccountDTO);
        response.setMsg("头像设置成功");
        return response;
    }

    /**
     * 获取验证码 type : 0 默 、1 注册 、2 快速登录 3、重置密码(修改密码) 4、修改手机号 5、绑定手机号
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
