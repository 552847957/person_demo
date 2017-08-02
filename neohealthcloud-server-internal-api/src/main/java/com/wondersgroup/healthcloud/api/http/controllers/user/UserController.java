package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.user.UserAccountDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.Debug;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.user.AnonymousAccountService;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import com.wondersgroup.healthcloud.utils.sms.SMS;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


/**
 * Created by longshasha on 16/9/1.
 */
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnonymousAccountService anonymousAccountService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private SessionUtil sessionUtil;

    @Resource(name = "verification")
    private SMS sms;

    @Autowired
    private Debug debug;


    @PostMapping(path = "/userInfo/update")
    public JsonResponseEntity updateUserInfo(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        JsonResponseEntity body = new JsonResponseEntity<>();
        UserInfoForm form = new UserInfoForm();

        form.registerId = reader.readString("uid", false);

        form.height = reader.readObject("height", true, Integer.class);
        form.weight = reader.readObject("weight", true, Float.class);
        form.waist = reader.readObject("waist", true, Float.class);
        try {
            userService.updateUserInfo(form);
        } catch (Exception e) {
            body.setData(3101);
            body.setMsg("信息修改失败");
            return body;
        }

        body.setMsg("信息修改成功");
        return body;
    }

    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    @GetMapping(path = "/userInfo")
    public JsonResponseEntity<Map<String, String>> getUserInfo(@RequestHeader(name = "access-token", required = false) String token,
                                                               @RequestParam String uid) {
        JsonResponseEntity<Map<String, String>> response = new JsonResponseEntity<>();
        Map<String, String> map = Maps.newHashMap();
        try {
            if(debug.sandbox() && StringUtils.isBlank(token)) {
                response.setCode(13);
                response.setMsg("请登录后操作！");
                return response;
            }else if(debug.sandbox() && StringUtils.isNotBlank(token)){
                Session session = sessionUtil.get(token);
                boolean deOrTe = null == session || false == session.getIsValid()
                        || StringUtils.isEmpty(session.getUserId());
                if(debug.sandbox()){
                    deOrTe = deOrTe || !session.getUserId().trim().equalsIgnoreCase(uid);
                }

                if(deOrTe) {
                    response.setCode(13);
                    response.setMsg("请登录后操作！");
                    return response;
                }
            }


            RegisterInfo registerInfo = userService.getOneNotNull(uid);
            map.put("personcard", registerInfo.getPersoncard());
            response.setData(map);
            return response;
        } catch (ErrorUserAccountException ex) {
            AnonymousAccount anonymousAccount = anonymousAccountService.getAnonymousAccount(uid, true);
            if (anonymousAccount != null && StringUtils.isNotBlank(anonymousAccount.getIdcard())) {
                map.put("personcard", anonymousAccount.getIdcard());
                response.setData(map);
            }
            return response;
        }
    }

    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    @GetMapping(path = "/info")
    public JsonResponseEntity<UserAccountDTO> info(@RequestParam String uid) {
        RegisterInfo registerInfo = userService.getOneNotNull(uid);

        UserInfo userInfo = userService.getUserInfo(uid);
        JsonResponseEntity<UserAccountDTO> response = new JsonResponseEntity<>();

        UserAccountDTO userAccountDTO = new UserAccountDTO(registerInfo, userInfo);
        response.setData(userAccountDTO);
        return response;
    }

    /**
     * 根据用户登录token判断地址是否完整(用于慢病H5页面判断)
     * 只有上海市的需要必填到街道 外省市的必填到省市区
     * @param token
     * @return
     */
    @GetMapping(path = "/judgeAddress")
    public JsonResponseEntity<Map<String, String>> judgeAddressIsComplete(@RequestParam(required = true) String token){
        JsonResponseEntity<Map<String, String>> response = new JsonResponseEntity<>();
        Map<String, String> map = Maps.newHashMap();
        Boolean judgeAddressIsComplete = true;
        try {
            AccessToken accessToken = userAccountService.getAccessToken(token);
            Address address = userService.getAddress(accessToken.getUid());
            if(address == null ||
                    StringUtils.isBlank(address.getProvince())){
                judgeAddressIsComplete = false;
            }
        }catch (Exception e){
            response.setCode(3104);
            response.setMsg("用户信息获取失败");
            return response;
        }
        map.put("addIsComplete",judgeAddressIsComplete.toString());
        response.setData(map);
        return response;
    }

    /**
     * 获取验证码 type : `0`:默认, `1`:注册, `2`:手机动态码登陆, `3`:重置密码 ,4 :修改手机号 ,5:绑定手机号
     * `7`:手机动态码登陆(未注册时不发送验证码 提示未注册) ,"8":计免
     *
     * @param mobile
     * @param type
     * @return
     */
    @GetMapping(path = "/code")
    public JsonResponseEntity<Map<String, String>> getVerificationCodes(@RequestParam String mobile,
                                                                        @RequestParam(defaultValue = "0") Integer type) {
        JsonResponseEntity<Map<String, String>> response = new JsonResponseEntity<>();
        userAccountService.getVerifyCode(mobile, type);
        String msg = "短信验证码发送成功";

        if (!(StringUtils.isNumeric(mobile) && mobile.length() == 11 && StringUtils.startsWith(mobile, "1"))) {
            throw new CommonException(1000, "手机号码不正确");
        }

        if (type == 1) {
            msg = "验证码已发送，请注意查看短信";
        } else if (type == 3) {
            msg = "验证码已发送至" + mobile;
        }
        response.setMsg(msg);
        //动态登录获取验证码时返回手机是否注册的状态
        if(type == 2){
            Map<String, String> data = Maps.newHashMap();
            data.put("is_registe",userAccountService.checkAccount(mobile)?"1":"0");
            response.setData(data);
        }
        return response;
    }

    /**
     * 验证验证码
     *
     * @param mobile
     * @param code
     * @return
     */
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
     * 发送短信
     *
     * @return
     */
    @PostMapping(path = "/message/send")
    public JsonResponseEntity<String> sendMessage(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String mobile = reader.readObject("mobile", true, String.class);
        String content = reader.readObject("content", true, String.class);
        if (!(StringUtils.isNumeric(mobile) && mobile.length() == 11 && StringUtils.startsWith(mobile, "1"))) {
            throw new CommonException(1000, "手机号码不正确");
        }
        sms.send(mobile, content);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setCode(0);
        body.setMsg("短信发送成功");
        return body;
    }



}
