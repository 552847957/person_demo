package com.wondersgroup.healthcloud.services.doctor;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.utils.wonderCloud.AccessToken;
import com.wondersgroup.healthcloud.utils.wonderCloud.WondersUser;

/**
 * Created by longshasha on 16/8/1.
 */
public interface DoctorAccountService {

    DoctorAccount findOne(String id);

    /**
     * 根据账号、密码登录
     * @param account
     * @param password
     * @return
     */
    AccessToken login(String account,String password,String mainArea);

    void logout(String token);

    void getVerifyCode(String mobile, Integer type);

    Boolean validateCode(String mobile, String verifyCode, boolean b);

    Boolean resetPassword(String mobile, String verifyCode, String password);

    WondersUser getWondersBaseInfo(String userid);

    AccessToken fastLogin(String mobile, String verify_code, boolean onlyOne,String mainArea);
}
