package com.wondersgroup.healthcloud.services.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;

/**
 * Created by longshasha on 16/8/4.
 */
public interface UserAccountService {

    AccessToken login(String account, String password);

    AccessToken guestLogin();

    AccessToken fastLogin(String mobile, String verify_code, boolean b);

    AccessToken wechatLogin(String token, String openid);

    AccessToken weiboLogin(String token);

    AccessToken qqLogin(String token);

    AccessToken logout(String token);

    void getVerifyCode(String mobile, Integer type);

    Boolean validateCode(String mobile, String code, boolean b);

    Boolean resetPassword(String mobile, String verifyCode, String password);

    AccessToken register(String mobile, String verifyCode, String password);

    AccessToken register(String mobile, String verifyCode);

    Boolean verificationSubmit(String id, String name, String idCard, String photo);

    Boolean changeMobile(String id, String oldVerifyCode, String newMobile, String newVerifyCode);

    JsonNode verficationSubmitInfo(String id,Boolean isAnonymous);

    AnonymousAccount anonymousRegistration(String creator, String username, String password);

    Boolean checkAccount(String mobile);

    RegisterInfo fetchInfo(String userId);

    AccessToken smyLogin(String token, String username);

    Boolean childVerificationSubmit(String id,String childId, String name, String idCard, String idCardFile, String birthCertFile);

}
