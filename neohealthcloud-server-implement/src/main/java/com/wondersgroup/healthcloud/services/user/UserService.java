package com.wondersgroup.healthcloud.services.user;

import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
public interface UserService {
    Map<String,Object> findUserInfoByUid(String uid);

    RegisterInfo getOneNotNull(String id);

    Boolean updateNickname(String id, String nickname);

    Boolean updateGender(String id, String gender);

    RegisterInfo findRegisterInfoByMobile(String mobile);

    void updateUserInfo(UserInfoForm form);

    UserInfo getUserInfo(String uid);

    void updateAvatar(String id, String avatar);
}
