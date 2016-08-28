package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.controllers.doctor.DoctorController;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.api.http.dto.user.AddressDTO;
import com.wondersgroup.healthcloud.api.http.dto.user.UserAccountAndSessionDTO;
import com.wondersgroup.healthcloud.api.http.dto.user.UserAccountDTO;
import com.wondersgroup.healthcloud.api.http.dto.user.VerificationInfoDTO;
import com.wondersgroup.healthcloud.api.utils.FamilyDoctorUtil;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.GwWebSignedUrlUtils;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.SigningVerficationService;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.HashMap;
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

    @Autowired
    private DictCache dictCache;

    @Autowired
    private SigningVerficationService signingVerficationService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorController doctorController;

    @Autowired
    private GwWebSignedUrlUtils gwWebSignedUrlUtils;

    private FamilyDoctorUtil familyDoctorUtil = new FamilyDoctorUtil();


    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");



    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    @VersionRange
    @GetMapping(path = "/info")
    public JsonResponseEntity<UserAccountDTO> info(@RequestParam String uid,@RequestParam(defaultValue = "false") Boolean withAddress) {
        RegisterInfo registerInfo = userService.getOneNotNull(uid);

        UserInfo userInfo = userService.getUserInfo(uid);
        JsonResponseEntity<UserAccountDTO> response = new JsonResponseEntity<>();

        UserAccountDTO userAccountDTO = new UserAccountDTO(registerInfo,userInfo);

        if(withAddress){
            Address address = userService.getAddress(uid);
            if (address != null) {
                userAccountDTO.setAddressDTO(new AddressDTO(address, dictCache));
                if (userAccountDTO.getAddressDTO().getDisplay() == null) {
                    userAccountDTO.setAddressDTO(null);
                }
            }
        }
        response.setData(userAccountDTO);
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


    @GetMapping(path = "/verification/signing")
    @VersionRange
    public JsonResponseEntity<String> offlineInvitation(@RequestParam("uid") String uid,
                                                        @RequestParam("name") String name,
                                                        @RequestParam("idcard") String idCard,
                                                        @RequestParam("mobile") String mobile,
                                                        @RequestParam("code") String code) {
        Boolean result = signingVerficationService.doctorInvitation(uid, mobile, name, idCard, code);
        JsonResponseEntity<String> body = new JsonResponseEntity<>();
        body.setCode(result ? 0 : 1024);
        body.setMsg(result ? "核实成功" : "核实失败");
        return body;
    }

    /**
     * 根据用户Id获取实名认证信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/verification/submit/info", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<VerificationInfoDTO> verificationSubmitInfo(@RequestParam("uid") String id) {
        JsonResponseEntity<VerificationInfoDTO> body = new JsonResponseEntity<>();
        RegisterInfo person = userService.getOneNotNull(id);
        if (person.verified()) {
            VerificationInfoDTO data = new VerificationInfoDTO();
            data.setUid(id);
            data.setName(person.getName());
            data.setIdcard(IdcardUtils.maskIdcard(person.getPersoncard()));
            data.setSuccess(true);
            data.setCanSubmit(false);
            body.setData(data);
        } else {
            body.setData(new VerificationInfoDTO(id, userAccountService.verficationSubmitInfo(id,false)));
        }
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
        String nickname = reader.readString("nick_name", false);

        userService.updateNickname(id, nickname);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        body.setMsg("昵称修改成功");
        Map<String, String> data = Maps.newHashMap();
        data.put("nick_name", nickname);
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


    /**
     * 修改 性别 年龄 身高 体重 腰围
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(path = "/userInfo/update")
    public JsonResponseEntity<Map<String, Object>> updateUserInfo(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);

        UserInfoForm form = new UserInfoForm();

        form.registerId = reader.readString("uid", false);

        form.age = reader.readObject("age", true, Integer.class);
        form.height = reader.readObject("height", true,Integer.class);
        form.weight = reader.readObject("weight", true, Float.class);
        form.waist = reader.readObject("waist", true, Float.class);

        form.gender = reader.readString("gender",true);

        userService.updateUserInfo(form);
        JsonResponseEntity<Map<String, Object>> body = new JsonResponseEntity<>();
        Map<String, Object> data = Maps.newHashMap();
        if(form.age!=null)
            data.put("age", form.age);
        if(form.height!=null)
            data.put("height", form.height);
        if(form.weight!=null)
            data.put("weight", decimalFormat.format(form.weight).toString());
        if(form.waist!=null)
            data.put("waist", decimalFormat.format(form.waist).toString());
        if(form.gender!=null)
            data.put("gender", form.gender);

        body.setData(data);
        body.setMsg("信息修改成功");
        return body;
    }

    /**
     * 修改昵称
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(path = "/avatar/update")
    public JsonResponseEntity<Map<String, String>> updateAvatar(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String avatar = reader.readString("avatar", false);

        userService.updateAvatar(id, avatar);
        JsonResponseEntity<Map<String, String>> body = new JsonResponseEntity<>();
        body.setMsg("头像修改成功");
        Map<String, String> data = Maps.newHashMap();
        data.put("avatar", avatar);
        body.setData(data);
        return body;
    }


    @VersionRange
    @PostMapping(path = "/address/update")
    public JsonResponseEntity<AddressDTO> updateAddress(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String id = reader.readString("uid", false);
        String province = reader.readString("province", true);
        String city = reader.readString("city", true);
        String county = reader.readString("county", true);
        String town = reader.readString("town", true);
        String committee = reader.readString("committee", true);
        String other = reader.readString("other", true);

        Address address = userService.updateAddress(id, province, city, county, town, committee, other);
        JsonResponseEntity<AddressDTO> body = new JsonResponseEntity<>();
        AddressDTO data = new AddressDTO(address, dictCache);
        if (data.getDisplay() != null) {
            body.setData(data);
        }
        body.setMsg("地址修改成功");
        return body;
    }


    /**
     * 查询是否有家庭医生
     * @param uid
     * @return
     */
    @RequestMapping(value = "/user/isSignDoctor", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Map<String,String>> isSignDoctor (@RequestParam(value = "uid",required = true) String uid) {
        JsonResponseEntity<Map<String,String>> body = new JsonResponseEntity<>();
        Map<String,String> map = new HashMap<>();
        Boolean hasSignDoctor = false;
        RegisterInfo userInfo = userService.getOneNotNull(uid);
        if(userInfo.verified() && StringUtils.isNotBlank(userInfo.getPersoncard())) {
            familyDoctorUtil.setHttpRequestExecutorManager(new HttpRequestExecutorManager(new OkHttpClient()));
            JsonNode result = familyDoctorUtil.getFamilyDoctorByUserPersoncard(gwWebSignedUrlUtils.getBasePath(),userInfo.getPersoncard());
            if (result.get("code").asInt() == 0) {
                String doctorIdcard = result.get("data").get("personcard") == null ? "" : result.get("data").get("personcard").asText();
                if (StringUtils.isNotBlank(doctorIdcard) && !"-1".equals(doctorIdcard)) {
                    Map<String,Object> doctorInfor = doctorService.findDoctorInfoByIdcard(doctorIdcard);
                    if(doctorInfor!=null){
                        hasSignDoctor = true;
                    }else{
                        body.setMsg("您所签约的家庭医生暂未开通健康云账号");
                    }
                }
            } else {
                body.setMsg(result.get("msg").asText());
            }
        }else{
            body.setMsg("该用户没有实名认证");
        }
        map.put("isSignDoctor",hasSignDoctor.toString());
        body.setData(map);
        return body;
    }


    /**
     * 获取家庭医生信息
     * @param uid
     * @return
     */
    @RequestMapping(value = "/user/familyDoctor", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> familyDoctor(@RequestParam(value = "uid",required = true) String uid) {
        JsonResponseEntity<DoctorAccountDTO> body = new JsonResponseEntity<>();
        String doctorIdcard = "";
        RegisterInfo userInfo = userService.getOneNotNull(uid);
        if(userInfo.verified() && StringUtils.isNotBlank(userInfo.getPersoncard())) {
            familyDoctorUtil.setHttpRequestExecutorManager(new HttpRequestExecutorManager(new OkHttpClient()));
            JsonNode result = familyDoctorUtil.getFamilyDoctorByUserPersoncard(gwWebSignedUrlUtils.getBasePath(),userInfo.getPersoncard());
            if(result.get("code").asInt()!=0){
                throw new ErrorUserAccountException(result.get("msg").asText());
            }
            doctorIdcard = result.get("data").get("personcard")==null?"":result.get("data").get("personcard").asText();
            if(StringUtils.isBlank(doctorIdcard)||"-1".equals(doctorIdcard) ){
                throw new ErrorUserAccountException("该用户没有签约的家庭医生");
            }
        }else{
            throw new ErrorUserAccountException("该用户没有实名认证");
        }
        Map<String,Object> doctorInfor  = doctorService.findDoctorInfoByIdcard(doctorIdcard);
        if(doctorInfor==null)
            throw new ErrorUserAccountException("您所签约的家庭医生暂未健康云账号");
        String doctorId = doctorInfor.get("id").toString();
        DoctorAccountDTO doctorAccountDTO = doctorController.getDoctorInfo(uid,doctorId);
        body.setData(doctorAccountDTO);
        return body;
    }


}
