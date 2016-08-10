package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.UserAccountAndSessionDTO;
import com.wondersgroup.healthcloud.api.http.dto.UserAccountDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountService userAccountService;


    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    @VersionRange
    @GetMapping(path = "/info")
    public JsonResponseEntity<UserAccountDTO> info(@RequestParam String uid) {
        RegisterInfo registerInfo = userService.getOneNotNull(uid);

        JsonResponseEntity<UserAccountDTO> response = new JsonResponseEntity<>();
        response.setData(new UserAccountDTO(registerInfo));
        return response;
    }


    /**
     * 获取验证码 type : `0`:默认, `1`:注册, `2`:手机动态码登陆, `3`:重置密码 ,4 :修改手机号 ,5:绑定手机号
     * @param mobile
     * @param type
     * @return
     */
    @WithoutToken
    @VersionRange
    @GetMapping(path = "/code")
    public JsonResponseEntity<String> getVerificationCodes(@RequestParam String mobile,
                                                           @RequestParam(defaultValue = "0") Integer type)  {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        userAccountService.getVerifyCode(mobile, type);
        String msg = "短信验证码发送成功";

        if(type==1){
            msg = "验证码已发送，请注意查看短信";
        }else if(type==3){
            msg = "验证码已发送至"+mobile;
        }
        response.setMsg(msg);

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
    @GetMapping(path = "/code/check")
    public JsonResponseEntity<String> validateCode(@RequestParam("mobile") String mobile,
                                                   @RequestParam("verify_code") String code) {
        Boolean result = userAccountService.validateCode(mobile, code, false);
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
    @WithoutToken
    @VersionRange
    @PostMapping(path = "/password/reset")
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
     * 修改手机号
     * @param request
     * @return
     */
    @PostMapping(path = "/mobile/update")
    public JsonResponseEntity<Map<String, String>> changeMobile(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String oldVerifyCode = reader.readString("old_verify_code", true);
        String newMobile = reader.readString("new_mobile", false);
        String newVerifyCode = reader.readString("new_verify_code", false);

        userAccountService.changeMobile(id, oldVerifyCode, newMobile, newVerifyCode);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        body.setMsg("更换手机号码成功");
        Map<String, String> data = Maps.newHashMap();
        data.put("mobile", newMobile);
        body.setData(data);
        return body;
    }

    /**
     * 注册账号
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(path = "/registe")
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
    @VersionRange
    @PostMapping(path = "/verification/submit")
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

    /**
     * 修改昵称
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(path = "/nickname/update")
    public JsonResponseEntity<Map<String, String>> changeNickname(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String nickname = reader.readString("nickname", false);

        userService.updateNickname(id, nickname);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        body.setMsg("昵称修改成功");
        Map<String, String> data = Maps.newHashMap();
        data.put("nickname", nickname);
        body.setData(data);
        return body;
    }

    /**
     * 修改性别
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(path = "/gender/update")
    public JsonResponseEntity<Map<String, String>> updateGender(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String gender = reader.readString("gender", false);

        userService.updateGender(id, gender);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        Map<String, String> data = Maps.newHashMap();
        data.put("gender", gender);
        body.setData(data);
        body.setMsg("性别修改成功");
        return body;
    }

}
