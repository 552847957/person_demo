package com.wondersgroup.healthcloud.services.doctor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorUserWondersBaseInfoException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorWondersCloudException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserGuestLogoutException;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import com.wondersgroup.healthcloud.utils.wonderCloud.HttpWdUtils;
import com.wondersgroup.healthcloud.utils.wonderCloud.WondersUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by longshasha on 16/8/1.
 */
@Service
public class DoctorAccountServiceImpl implements DoctorAccountService {

    @Autowired
    private DoctorAccountRepository repository;

    @Autowired
    private HttpWdUtils httpWdUtils;

    private final String user_type_doctor = "0";

    private final Integer CHANNEL_TYPE_JKY = 1;
    private final Integer CHANNEL_TYPE_QQ = 2;
    private final Integer CHANNEL_TYPE_WEIBO = 3;
    private final Integer CHANNEL_TYPE_WECHAT = 4;


    private static final String[] smsContent = {
            "您的验证码是：:code，请在10分钟内按照提示提交验证码。切勿将验证码泄露于他人。",
            "验证码:code，10分钟有效。这不只是您注册的一小步，更是您迈向健康的一大步。",
            "偷偷告诉您个秘密，用:code就能在10分钟内登录。千万不要告诉别人哦。",
            "您的验证码:code已经快马加鞭送到您手上，您可以在10分钟内重置您的密码。",
            "您正在更改绑定的手机号，验证码:code。慎重操作，打死都不能告诉别人。",
            "您正在绑定手机号哦，为了您的账号安全请用验证码:code绑定。",
            "恭喜您人品爆发通过摇一摇赢得奖品，请用验证码:code来完成领取。"
    };


    @Override
    public DoctorAccount findOne(String id) {
        return repository.findOne(id);
    }

    /**
     * 医生登录
     * @param account  手机号或登录名
     * @param password 密码
     * @return
     */
    @Override
    public AccessToken login(String account, String password,String mainArea) {
        DoctorAccount doctorAccount = repository.findDoctorByAccountAndMainArea(account,mainArea);
        if(doctorAccount == null){
            throw new ErrorDoctorAccountException("该账号不存在,请重新输入。");
        }
        if(StringUtils.isBlank(doctorAccount.getIsAvailable()) || "1".equals(doctorAccount.getIsAvailable())){
            throw new ErrorDoctorAccountException("该医生账号正在审核中,请稍后再试。");
        }
        JsonNode result = httpWdUtils.login(account, password);
        if (wondersCloudResult(result)) {
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        }else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    @Override
    public AccessToken fastLogin(String mobile, String verify_code, boolean onceCode,String mainArea) {
        DoctorAccount doctorAccount = repository.findDoctorByAccountAndMainArea(mobile,mainArea);
        if(doctorAccount == null){
            throw new ErrorDoctorAccountException("该账号不存在,请重新输入。");
        }
        if(StringUtils.isBlank(doctorAccount.getIsAvailable()) || "1".equals(doctorAccount.getIsAvailable())){
            throw new ErrorDoctorAccountException("该医生账号正在审核中,请稍后再试。");
        }
        JsonNode result = httpWdUtils.fastLogin(mobile, verify_code, onceCode);
        if (wondersCloudResult(result)) {
            return fetchTokenFromWondersCloud(result.get("session_token").asText());
        }else {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }


    /**
     * 登出
     * @param token
     */
    @Override
    public void logout(String token) {
        AccessToken accessToken = getAccessToken(token);
        String uid = accessToken.getUid();
        if (uid == null) {
            throw new ErrorUserGuestLogoutException("游客无需登出");
        } else {
            JsonNode result = httpWdUtils.logout(uid);
        }
    }

    /**
     * 获取验证码
     * @param mobile
     * @param type  0 默 、1 注册 、2 快速登录 3、重置密码(修改密码) 4、修改手机号 5、绑定手机号
     */
    @Override
    public void getVerifyCode(String mobile, Integer type) {
        if (type == null || type < 0 || type >= smsContent.length) {
            throw new ErrorDoctorAccountException("未知的短信请求");
        }

        if(type!=5){
            DoctorAccount doctorAccount = repository.findDoctorByAccount(mobile);
            if(doctorAccount == null){
                throw new ErrorDoctorAccountException("手机号码无效,请重新输入。");
            }
        }


        JsonNode result = httpWdUtils.sendCode(mobile,smsContent[type]);
        Boolean success = result.get("success").asBoolean();
        if (!success) {
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
    }

    /**
     * 验证验证码
     * @param mobile
     * @param verifyCode
     * @param onlyOne
     * @return
     */
    @Override
    public Boolean validateCode(String mobile, String verifyCode, boolean onlyOne) {
        JsonNode result = httpWdUtils.verifyCode(mobile,verifyCode,onlyOne);
        if(result.get("code").asInt()==511){
            throw new ErrorWondersCloudException(result.get("msg").asText());
        }
        return result.get("success").asBoolean();
    }

    /**
     * 重置密码
     * @param mobile
     * @param verifyCode
     * @param password
     * @return
     */
    @Override
    public Boolean resetPassword(String mobile, String verifyCode, String password) {
        if (validateCode(mobile, verifyCode, true)) {
            JsonNode result = httpWdUtils.resetPassword(mobile,password);
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

    @Override
    public WondersUser getWondersBaseInfo(String userid) {
        JsonNode result = httpWdUtils.basicInfo(userid);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new WondersUser(result.get("user"));
        } else {
            throw new ErrorUserWondersBaseInfoException();
        }
    }



    private AccessToken fetchTokenFromWondersCloud(String session) {
        String key = IdGen.uuid();
        httpWdUtils.addSessionExtra(session, key,this.user_type_doctor);
        AccessToken accessToken = getAccessToken(session);
        return accessToken;
    }

    private AccessToken getAccessToken(String session) {
        JsonNode result = httpWdUtils.getSession(session);
        Boolean success = result.get("success").asBoolean();
        if (success) {
            return new AccessToken(session, result.get("data"));
        } else {
            throw new ErrorWondersCloudException(12,"令牌无效");
        }
    }

    private boolean wondersCloudResult(JsonNode result) {
        return result.get("success").asBoolean();
    }
}
