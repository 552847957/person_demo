package com.wondersgroup.healthcloud.services.user.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorUserWondersBaseInfoException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorWondersCloudException;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.exception.*;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.ImageUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.WondersUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by longshasha on 16/8/4.
 */
@Service
public class UserAccountServiceImpl implements UserAccountService{

    private final String user_type_patient = "1";//用户类型 0:医生,1:患者

    @Autowired
    private HttpWdUtils httpWdUtils;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    private static final String[] smsContent = {
            "您的验证码为:code，10分钟内有效。",
            "您的验证码为:code，10分钟内有效。",
            "您的验证码为:code，10分钟内有效。",
            "您的验证码为:code，10分钟内有效。",
            "您正在更改绑定的手机号，验证码:code。慎重操作，打死都不能告诉别人",
            "您正在绑定手机号哦，为了您的账号安全请用验证码:code绑定。"
    };

    @Override
    public AccessToken login(String account, String password) {
        if(checkAccountIsNew(account)){
            throw new ErrorUserMobileHasNotRegisteredException("该手机号未注册，请先注册");
        }
        JsonNode result = httpWdUtils.login(account, password);
        if (wondersCloudResult(result)) {
            WondersUser user = new WondersUser(result.get("user"));
            RegisterInfo registerInfo = mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    /**
     * 游客登录
     *
     * @return
     */
    @Override
    public AccessToken guestLogin() {
        JsonNode result = httpWdUtils.guestLogin();
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    /**
     * 使用验证码动态登录
     *
     * @param mobile
     * @param verify_code
     * @return
     */
    @Override
    public AccessToken fastLogin(String mobile, String verify_code, boolean onceCode) {
        JsonNode result = httpWdUtils.fastLogin(mobile, verify_code, onceCode);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText());
            mergeRegistration(user);
            AccessToken accessToken = fetchTokenFromWondersCloud(result.get("session_token").asText());
            return accessToken;
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }


    /**
     * 微信登录
     *
     * @param token
     * @param openid
     * @return
     */
    @Override
    public AccessToken wechatLogin(String token, String openid) {
        JsonNode result = httpWdUtils.wechatLogin(token, openid);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText());
            mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").get("errmsg").asText());
        }
    }

    /**
     * 微博登录
     *
     * @param token
     * @return
     */
    @Override
    public AccessToken weiboLogin(String token) {
        JsonNode result = httpWdUtils.weiboLogin(token);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText());
            mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").get("error").asText());
        }
    }

    /**
     * QQ 登录
     *
     * @param token QQ token
     * @return
     */
    @Override
    public AccessToken qqLogin(String token) {
        JsonNode result = httpWdUtils.qqLogin(token);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText());
            mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").get("error_description").asText());
        }
    }

    /**
     * 退出登录
     *
     * @param token
     * @return
     */
    @Override
    public AccessToken logout(String token) {
        AccessToken accessToken = getAccessToken(token);
        String uid = accessToken.getUid();
        if (uid == null) {
            throw new ErrorUserGuestLogoutException("游客无需登出");
        } else {
            JsonNode result = httpWdUtils.logout(uid);
            Boolean success = result.get("success").asBoolean();
            if (success) {
                return guestLogin();
            } else {
                throw new ErrorWondersCloudException(result.get("msg").asText());
            }
        }
    }

    @Override
    public void getVerifyCode(String mobile, Integer type) {
        if (type == null || type < 0 || type >= smsContent.length) {
            throw new ErrorSmsRequestException();
        }

        if (type == 1 && checkAccount(mobile)) {
            throw new ErrorUserMobileHasBeenRegisteredException("该手机已注册，请直接登录");
        }

        if(type == 3 && checkAccountIsNew(mobile)){
            throw new ErrorUserMobileHasNotRegisteredException("该手机号未注册，请先注册");
        }

        JsonNode result = httpWdUtils.sendCode(mobile, smsContent[type]);
        Boolean success = result.get("success").asBoolean();
        if (!success) {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }

    }

    /**
     * 验证验证码
     *
     * @param mobile
     * @param verifyCode
     * @param onlyOne
     * @return
     */
    @Override
    public Boolean validateCode(String mobile, String verifyCode, boolean onlyOne) {
        JsonNode result = httpWdUtils.verifyCode(mobile, verifyCode, onlyOne);
        return result.get("success").asBoolean();
    }

    /**
     * 重置密码
     *
     * @param mobile
     * @param verifyCode
     * @param password
     * @return
     */
    @Override
    public Boolean resetPassword(String mobile, String verifyCode, String password) {
        if (validateCode(mobile, verifyCode, true)) {
            JsonNode result = httpWdUtils.resetPassword(mobile, password);
            Boolean success = result.get("success").asBoolean();
            if (success) {
                return true;
            } else {
                throw new ErrorWondersCloudException(result.get("msg").asText());
            }
        } else {
            return false;
        }
    }

    /**
     * 注册账号
     *
     * @param mobile
     * @param verifyCode
     * @return
     */
    @Override
    public AccessToken register(String mobile, String verifyCode, String password) {
        Boolean mobileIsValidate = validateCode(mobile, verifyCode, false);
        if (!mobileIsValidate) {
            throw new ErrorWondersCloudException("手机验证码错误");
        }
        if (checkAccount(mobile)) {
            throw new ErrorUserMobileHasBeenRegisteredException("该手机号码已经注册，是否直接登录");
        }
        return registe(mobile, password);
    }

    /**
     * 提交实名认证信息
     *
     * @param id       用户Id
     * @param name     姓名
     * @param idCard   身份证号
     * @param photoUrl 身份证照片地址
     * @return
     */
    @Override
    public Boolean verificationSubmit(String id, String name, String idCard, String photoUrl) {
        //根据图片的url获取图片的byte
        byte[] photo = new ImageUtils().getImageFromURL(photoUrl);
        JsonNode result = httpWdUtils.verificationSubmit(id, name, idCard, "", photo);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return true;
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    /**
     * 修改手机号
     * @param uid
     * @param oldVerifyCode
     * @param newMobile
     * @param newVerifyCode
     * @return
     */
    @Override
    public Boolean changeMobile(String uid, String oldVerifyCode, String newMobile, String newVerifyCode) {
        RegisterInfo register = findOneRegister(uid,false);
        if (register.getRegmobilephone() != null) {
            if (StringUtils.isBlank(oldVerifyCode)) {
                throw new ErrorChangeMobileException("请输入原手机的验证码");
            }
            if (register.getRegmobilephone().equals(newMobile)) {
                throw new ErrorChangeMobileException("手机号码相同, 无需更换");
            }
            if (!validateCode(register.getRegmobilephone(), oldVerifyCode, true)) {
                throw new ErrorChangeMobileException("验证码错误");
            }
        }
        if (!validateCode(newMobile, newVerifyCode, true)) {
            throw new ErrorChangeMobileException("验证码错误");
        }

        JsonNode result = httpWdUtils.updateMobile(uid,newMobile);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            register.setRegmobilephone(newMobile);
            register.setUpdateBy(register.getRegisterid());
            register.setUpdateDate(new Date());
            registerInfoRepository.saveAndFlush(register);
            return true;
        } else {
            throw new ErrorChangeMobileException(1002,result.get("msg").asText());
        }
    }


    /**
     * 同步本地账号
     *
     * @param user
     * @return
     */
    private RegisterInfo mergeRegistration(WondersUser user) {
        RegisterInfo registerInfo = registerInfoRepository.findOne(user.userId);
        //是否有本地账号 如果没有保存本地
        if (registerInfo == null) {
            registerInfo = localRegistration(user.userId, user.mobile, user.username, user.name, user.isVerified, user.idCard);
        } else {
            Boolean isVerified = user.isVerified;
            registerInfo.setRegisterid(user.userId);
            registerInfo.setRegmobilephone(user.mobile);
            registerInfo.setIdentifytype(isVerified ? "1" : registerInfo.getIdentifytype());
            if (isVerified) {
                registerInfo.setName(user.name);
                registerInfo.setPersoncard(user.idCard);
                registerInfo.setGender(IdcardUtils.getGenderByIdCard(user.idCard));
                registerInfo.setBirthday(DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(user.idCard)));
            }

            registerInfo = saveRegisterInfo(registerInfo);

        }
        return registerInfo;
    }

    /**
     * 注册本地账号
     *
     * @param id
     * @param mobile
     * @param username
     * @param name
     * @param isVerified
     * @param idCard
     * @return
     */
    private RegisterInfo localRegistration(String id, String mobile, String username, String name, boolean isVerified, String idCard) {
        ThirdPartyUser thirdPartyUser = thirdPartyBinding(id);
        Boolean fromThirdParty = thirdPartyUser != null;
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setRegisterid(id);
        registerInfo.setRegmobilephone(mobile);
        if (fromThirdParty) {
            registerInfo.setNickname(thirdPartyUser.nickname);
        } else if (mobile != null) {
            registerInfo.setNickname("健康用户" + StringUtils.substring(mobile, 7));
        } else {
            registerInfo.setNickname(username);
        }
        registerInfo.setHeadphoto(fromThirdParty ? thirdPartyUser.avatar : null);
        registerInfo.setIdentifytype(isVerified ? "1" : "0");
        if (isVerified) {
            registerInfo.setName(name);
            registerInfo.setPersoncard(idCard);
            registerInfo.setGender(IdcardUtils.getGenderByIdCard(idCard));
            registerInfo.setBirthday(DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(idCard)));
        } else {
            registerInfo.setGender(fromThirdParty ? thirdPartyUser.gender : null);
        }
        registerInfo.setCreateDate(new Date());
        registerInfo = saveRegisterInfo(registerInfo);
        return registerInfo;
    }


    private AccessToken fetchTokenFromWondersCloud(String session) {
        String key = IdGen.uuid();
        httpWdUtils.addSessionExtra(session, key,this.user_type_patient);
        return getAccessToken(session);
    }

//    @Override
    public AccessToken getAccessToken(String session) {
        JsonNode result = httpWdUtils.getSession(session);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new AccessToken(session, result.get("data"));
        } else {
            throw new ErrorWondersCloudException(12, "令牌无效");
        }
    }

    /**
     * 校验手机号是否可用
     *
     * @param mobile
     * @return
     */
    private boolean checkAccountIsNew(String mobile) {
        JsonNode result = httpWdUtils.checkAccount(mobile);
        int code = result.get("code").asInt();
        return 214 == code;
    }

    private boolean wondersCloudResult(JsonNode result) {
        return result.get("success").asBoolean();
    }

    private class ThirdPartyUser {
        public String userId;
        public String nickname;
        public String mobile;
        public String gender;
        public String avatar;

        public ThirdPartyUser(JsonNode tpu) {
            this.userId = tpu.get("userid").asText();
            this.mobile = tpu.get("platform_mobile").isNull() ? null : tpu.get("platform_mobile").asText();
            this.nickname = tpu.get("platform_username").isNull() ? null : tpu.get("platform_username").asText();
            JsonNode info = tpu.get("info");
            if (info != null) {
                this.gender = info.get("sex").isNull() ? null : info.get("sex").asText();
                if ("0".equals(this.gender)) {
                    this.gender = null;
                }
                this.avatar = info.get("headimgurl").isNull() ? null : info.get("headimgurl").asText();
            }
        }
    }

    private ThirdPartyUser thirdPartyBinding(String userId) {
        JsonNode result = httpWdUtils.thirdPartyBinding(userId);
        Boolean success = result.get("success").asBoolean() && result.get("exist").asBoolean();
        if (success) {
            return new ThirdPartyUser(result);
        }
        return null;
    }

    /**
     * 获取万达云基本信息
     *
     * @param uuid
     * @return
     */
    private WondersUser getWondersBaseInfo(String uuid) {
        JsonNode result = httpWdUtils.basicInfo(uuid);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new WondersUser(result.get("user"));
        } else {
            throw new ErrorUserWondersBaseInfoException();
        }
    }

    /**
     * 保存用户账号信息
     *
     * @param registerInfo
     * @return
     */
    private RegisterInfo saveRegisterInfo(RegisterInfo registerInfo) {
        registerInfo.setUpdateDate(new Date());
        return registerInfoRepository.saveAndFlush(registerInfo);
    }

    /**
     * 验证手机号是否可用 code=413 是手机号已被使用
     *
     * @param mobile
     * @return
     */
    private boolean checkAccount(String mobile) {
        JsonNode result = httpWdUtils.checkAccount(mobile);
        int code = result.get("code").asInt();
        return 413 == code;
    }

    /**
     * 去万达云注册
     *
     * @param mobile
     * @param password
     * @return
     */
    public AccessToken registe(String mobile, String password) {
        JsonNode result = httpWdUtils.registe(mobile, password);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText());
            mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    private RegisterInfo findOneRegister(String id, Boolean nullable) {
        RegisterInfo register = registerInfoRepository.findOne(id);
        if (register != null || nullable) {
            return register;
        } else {
            throw new ErrorUserAccountException();
        }
    }


}
