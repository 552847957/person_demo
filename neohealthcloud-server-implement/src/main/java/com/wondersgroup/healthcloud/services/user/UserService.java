package com.wondersgroup.healthcloud.services.user;

import com.wondersgroup.healthcloud.jpa.entity.user.Address;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import com.wondersgroup.healthcloud.services.user.dto.UserInfoForm;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
public interface UserService {
    Map<String, Object> findUserInfoByUid(String uid);

    RegisterInfo getOneNotNull(String id);

    List<RegisterInfo> findRegisterInfoByIdcard(String idcard);

    Boolean updateNickname(String id, String nickname);

    Boolean updateGender(String id, String gender);

    RegisterInfo findRegisterInfoByMobile(String mobile);

    void updateUserInfo(UserInfoForm form);

    UserInfo getUserInfo(String uid);

    void updateAvatar(String id, String avatar);

    Address updateAddress(String id, String province, String city, String county, String town, String committee, String other);

    Address getAddress(String uid);

    List<Map<String,Object>> findUserListByPager(int pageNum, int size, Map parameter);

    int countUserByParameter(Map parameter);

    Map<String,Object> findUserDetailByUid(String registerid);
}
