package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.http.dto.UserAccountAndSessionDTO;
import com.wondersgroup.healthcloud.api.http.dto.UserAccountDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountService userAccountService;




    /**
     * 获取验证码 type : `0`:默认, `1`:注册, `2`:手机动态码登陆, `3`:重置密码
     * @param mobile
     * @param type
     * @return
     */
    @WithoutToken
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<String> getVerificationCodes(@RequestParam String mobile,
                                                           @RequestParam(defaultValue = "0") Integer type)  {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        userAccountService.getVerifyCode(mobile, type);
        response.setMsg("短信验证码发送成功");
        return response;
    }

    /**
     * 验证验证码
     * @param mobile
     * @param code
     * @return
     */
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/code/check", method = RequestMethod.GET)
    public JsonResponseEntity<String> validateCode(@RequestParam("mobile") String mobile,
                                                   @RequestParam("verify_code") String code) {
        Boolean result = userAccountService.validateCode(mobile, code, false);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setCode(result ? 0 : 1002);
        body.setMsg(result ? "短信验证码验证通过" : "短信验证码验证错误");
        return body;
    }

    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    public JsonResponseEntity<String> resetPassword(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String verifyCode = reader.readString("verify_code", false);
        String password = reader.readString("password", false);

        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        Boolean verifyCodeResult = userAccountService.resetPassword(mobile, verifyCode, password);
        if (!verifyCodeResult) {
            response.setCode(1002);
            response.setMsg("无效的验证码，请重新输入");
            return response;
        }
        response.setMsg(verifyCodeResult ? "恭喜, 密码设置成功" : "密码设置失败");
        return response;
    }

    /**
     * 注册账号
     * @param request
     * @return
     */
    @RequestMapping(value = "/registe", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<UserAccountAndSessionDTO> register(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readString("mobile", false);
        String verifyCode = reader.readString("verify_code", false);
        String password = reader.readString("password", false);

        JsonResponseEntity<UserAccountAndSessionDTO> body = new JsonResponseEntity<>();

        body.setData(new UserAccountAndSessionDTO(userAccountService.register(mobile, verifyCode,password)));
        body.getData().setInfo(getInfo(body.getData().getUid()));
        body.setMsg("注册成功");
        return body;
    }

    /**
     * 提交实名认证信息
     * @return
     */
    @RequestMapping(value = "/verification/submit", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> verificationSubmit(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid",false);
        String name = reader.readString("name",false);
        String idCard = reader.readString("idcard",false);
        String photo = reader.readString("photo",false);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        name = name.trim();//去除空字符串
        idCard = idCard.trim();
        userAccountService.verificationSubmit(id, name, idCard, photo);
        body.setMsg("提交成功");
        return body;
    }

    public UserAccountDTO getInfo(String uid) {
        Map<String, Object> user = userService.findUserInfoByUid(uid);
        UserAccountDTO accountDto = new UserAccountDTO(user);
        return  accountDto;
    }
}
