package com.wondersgroup.healthcloud.services.user.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorUserWondersBaseInfoException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorWondersCloudException;
import com.wondersgroup.healthcloud.services.user.UserAccountService;
import com.wondersgroup.healthcloud.services.user.exception.*;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.easemob.EasemobAccount;
import com.wondersgroup.healthcloud.utils.easemob.EasemobDoctorPool;
import com.wondersgroup.healthcloud.utils.wonderCloud.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * Created by longshasha on 16/8/4.
 */
@Service
public class UserAccountServiceImpl implements UserAccountService{

    private final String user_type_patient = "1";//用户类型 0:医生,1:患者

    private final Integer CHANNEL_TYPE_JKY = 1;
    private final Integer CHANNEL_TYPE_QQ = 2;
    private final Integer CHANNEL_TYPE_WEIBO = 3;
    private final Integer CHANNEL_TYPE_WECHAT = 4;
    private final Integer CHANNEL_TYPE_SMY = 5;

    @Autowired
    private HttpWdUtils httpWdUtils;

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    @Autowired
    private EasemobDoctorPool easemobDoctorPool;

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
            WondersUser user = new WondersUser(result.get("user"),CHANNEL_TYPE_JKY);
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
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_JKY);
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
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_WECHAT);
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
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_WEIBO);
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
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_QQ);
            mergeRegistration(user);
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        } else {
            throw new ErrorWondersCloudException(result.get("msg").get("error_description").asText());
        }
    }


    /**
     * 市民云三方登陆
     * @param token
     * @param username
     * @return
     */
    @Override
    public AccessToken smyLogin(String token, String username) {
        JsonNode result = httpWdUtils.smyLogin(token,username);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_SMY);
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

        //修改手机号时校验手机号是否被占用
        if(type == 5 && checkAccount(mobile)){
            throw new ErrorUserMobileHasNotRegisteredException("手机已被使用");
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
        if(result.get("code").asInt()==511){
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
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

    @Override
    public AccessToken register(String mobile, String verifyCode) {
        Boolean mobileIsValidate = validateCode(mobile, verifyCode, false);
        if (!mobileIsValidate) {
            throw new ErrorWondersCloudException("手机验证码错误");
        }
        if (checkAccount(mobile)) {
            throw new ErrorUserMobileHasBeenRegisteredException("该手机号码已经注册，是否直接登录");
        }
        return fastLogin(mobile, verifyCode, false);
    }

    /**
     * 提交实名认证信息
     * @param id       用户Id
     * @param name     姓名
     * @param idCard   身份证号
     * @param photoUrl 身份证照片地址
     * @return
     */
    @Override
    @Transactional
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
     * 儿童实名认证提交
     * @param parentUserid
     * @param name
     * @param idcard
     * @param idCardFileUrl
     * @param birthCertFileUrl
     */
    @Override
    @Transactional
    public Boolean childVerificationSubmit(String parentUserid,String childUserid , String name, String idcard, String idCardFileUrl, String birthCertFileUrl) {
        RegisterInfo parentUser = registerInfoRepository.findOne(parentUserid);
        if (parentUser == null) {
            throw new ErrorUserAccountException();
        }
        if(!parentUser.verified()){
            throw new ErrorChildVerificationException("您还未实名认证,请先去市民云实名认证");
        }else if(!"1".equals(parentUser.getIdentifytype())){
            throw new ErrorChildVerificationException("您未通过市民云实名认证");
        }
        if(StringUtils.isBlank(parentUser.getRegmobilephone())){
            throw new ErrorChildVerificationException("您未绑定手机号,请先绑定手机号");
        }
        byte[] idCardFile = new ImageUtils().getImageFromURL(idCardFileUrl);
        byte[] birthCertFile = new ImageUtils().getImageFromURL(birthCertFileUrl);
        JsonNode result = httpWdUtils.verificationChildSubmit(childUserid, name, parentUser.getRegmobilephone(), idcard, parentUserid,
                "", idCardFile, birthCertFile);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return true;
        } else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    @Override
    public JsonNode verficationSubmitInfo(String id,Boolean isAnonymous) {
        JsonNode result = httpWdUtils.verficationSubmitInfo(id);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            JsonNode info = result.get("info");

            if(!isAnonymous){
                RegisterInfo user = registerInfoRepository.findOne(id);
                if (user == null) {
                    throw new ErrorUserAccountException();
                }
                if (info.get("status").asInt() == 1 && !user.verified()) {
                    user.setIdentifytype("1");
                    user.setPersoncard(info.get("idcard").asText());
                    user.setName(info.get("name").asText());
                    user.setBirthday(DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(user.getPersoncard())));
                    user.setGender(IdcardUtils.getGenderByIdCard(user.getPersoncard()));
                    registerInfoRepository.saveAndFlush(user);
                }
            }else{
                AnonymousAccount anonymousAccount = anonymousAccountRepository.findOne(id);
                if (anonymousAccount == null) {
                    throw new ErrorAnonymousAccountException("不存在的用户");
                }
                if (info.get("status").asInt() == 1 && anonymousAccount.getIdcard() == null) {
                    anonymousAccount.setIdcard(info.get("idcard").asText());
                    anonymousAccount.setName(info.get("name").asText());
                    anonymousAccountRepository.saveAndFlush(anonymousAccount);
                }
            }
            return result.get("info");
        } else {
            return null;
        }
    }

    /**
     * 注册匿名账户
     * @param creator
     * @param username
     * @param password
     * @return
     */
    @Override
    public AnonymousAccount anonymousRegistration(String creator, String username, String password) {
        return anonymousRegistration(creator, username, password, false);
    }
    
    /**
     * 注册儿童实名认证
     * @param creator
     * @param username
     * @param password
     * @return
     */
    @Override
    public AnonymousAccount childVerificationRegistration(String creator, String username, String password) {
        return anonymousRegistration(creator, username, password, true);
    }
    
    public AnonymousAccount anonymousRegistration(String creator, String username, String password, Boolean isChild) {
        String encodedPassword;
        try {
            encodedPassword = RSAUtil.encryptByPublicKey(password, httpWdUtils.getPublicKey());
        } catch (Exception e) {
            throw new ErrorWondersCloudException("加密错误");
        }
        JsonNode result = httpWdUtils.registeByUsername(username,encodedPassword);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            Date time = new Date();
            AnonymousAccount anonymousAccount = new AnonymousAccount();
            anonymousAccount.setId(result.get("userid").asText());
            anonymousAccount.setUsername(username);
            anonymousAccount.setPassword(password);
            anonymousAccount.setCreator(creator);
            anonymousAccount.setCreateDate(time);
            anonymousAccount.setUpdateDate(time);
            anonymousAccount.setDelFlag("0");
            anonymousAccount.setIsChild(isChild);
            return anonymousAccountRepository.saveAndFlush(anonymousAccount);
        } else {
            throw new ErrorAnonymousAccountException("账户创建失败, 请再试一次");
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
        if (StringUtils.isNotBlank(register.getRegmobilephone())) {
            if (StringUtils.isBlank(oldVerifyCode)) {
                throw new ErrorChangeMobileException("请输入原手机的验证码");
            }
            if (register.getRegmobilephone().equals(newMobile)) {
                throw new ErrorChangeMobileException("手机号码相同, 无需更换");
            }
            if (!validateCode(register.getRegmobilephone(), oldVerifyCode, false)) {
                throw new ErrorChangeMobileException("验证码错误");
            }
        }
        if (!validateCode(newMobile, newVerifyCode, false)) {
            throw new ErrorChangeMobileException("验证码错误");
        }


        JsonNode result = httpWdUtils.updateMobile(uid,newMobile);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            register.setRegmobilephone(newMobile);
            register.setUpdateBy(register.getRegisterid());
            register.setUpdateDate(new Date());
            registerInfoRepository.saveAndFlush(register);

            //如果有医生账号的话修改医生账号的手机号
            DoctorAccount doctorAccount = doctorAccountRepository.findOne(register.getRegisterid());
            if(doctorAccount!=null){
                doctorAccount.setMobile(newMobile);
                doctorAccount.setUpdateDate(new Date());
                doctorAccount.setUpdateBy(register.getRegisterid());
                doctorAccountRepository.saveAndFlush(doctorAccount);
            }
            //为了使手机的验证码失效
            if(StringUtils.isNotBlank(oldVerifyCode)){
                validateCode(register.getRegmobilephone(), oldVerifyCode, true);
            }
            validateCode(newMobile, newVerifyCode, true);
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
            registerInfo = localRegistration(user.userId, user.mobile, user.username, user.name, user.isVerified, user.idCard,user.type, user.tagid, user.channelType);
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
            }else if(registerInfo.verified()){//解决数据库中已实名认证的性别为0的数据
                registerInfo.setGender(IdcardUtils.getGenderByIdCard(registerInfo.getPersoncard()));
                registerInfo.setBirthday(DateFormatter.parseIdCardDate(IdcardUtils.getBirthByIdCard(registerInfo.getPersoncard())));
            }
            if(StringUtils.isBlank(registerInfo.getTalkid())){
                EasemobAccount easemobAccount = easemobDoctorPool.fetchOneUser();
                if (easemobAccount!=null) {//注册环信
                    registerInfo.setTalkid(easemobAccount.id);
                    registerInfo.setTalkpwd(easemobAccount.pwd);
                }
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
    private RegisterInfo localRegistration(String id, String mobile, String username, String name, boolean isVerified, String idCard,String userSource, String tagid, Integer channelType) {
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

        EasemobAccount easemobAccount = easemobDoctorPool.fetchOneUser();
        if (easemobAccount!=null) {//注册环信
            registerInfo.setTalkid(easemobAccount.id);
            registerInfo.setTalkpwd(easemobAccount.pwd);
        }

        registerInfo.setTagid(tagid);
        registerInfo.setSourceId(userSource);
        registerInfo.setChannelType(channelType);
        registerInfo.setDelFlag("0");
        registerInfo.setRegtime(new Date());
        registerInfo.setCreateDate(new Date());
        registerInfo.setCreateBy(id);
        registerInfo.setUpdateBy(id);
        registerInfo.setUpdateDate(new Date());
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
    private WondersUser getWondersBaseInfo(String uuid,Integer channelType) {
        JsonNode result = httpWdUtils.basicInfo(uuid);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new WondersUser(result.get("user"),channelType);
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
    @Override
    public Boolean checkAccount(String mobile) {
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
            WondersUser user = getWondersBaseInfo(result.get("userid").asText(),CHANNEL_TYPE_JKY);
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

    @Override
    public RegisterInfo fetchInfo(String userId) {
        WondersUser user = getWondersBaseInfo(userId, 0);
        if (user != null) {
            RegisterInfo register = registerInfoRepository.findOne(user.userId);
            if (register != null) {
                return mergeRegistration(user);
            } else {//TODO(zhangzhixiu):return null is not very good, but there are two type of account.
                AnonymousAccount anonymousAccount = anonymousAccountRepository.findOne(user.userId);
                if(anonymousAccount==null){
                    return mergeRegistration(user);
                }else {
                    anonymousAccount.setName(user.name);
                    anonymousAccount.setIdcard(user.idCard);
                    anonymousAccountRepository.saveAndFlush(anonymousAccount);
                }
                return null;
            }
        }
        return null;
    }



    private WondersUser getWondersBaseInfo(String uuid, int channelType) {
        JsonNode result = httpWdUtils.basicInfo(uuid);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new WondersUser(result.get("user"), channelType);
        } else {
            throw new ErrorWondersCloudException("获取用户信息失败");
        }
    }



}
