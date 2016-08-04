package com.wondersgroup.healthcloud.services.user;

import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
public interface UserService {
    Map<String,Object> findUserInfoByUid(String uid);
}
