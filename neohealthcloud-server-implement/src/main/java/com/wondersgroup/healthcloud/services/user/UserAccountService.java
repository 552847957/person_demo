package com.wondersgroup.healthcloud.services.user;

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

    Boolean verificationSubmit(String id, String name, String idCard, String photo);
}
