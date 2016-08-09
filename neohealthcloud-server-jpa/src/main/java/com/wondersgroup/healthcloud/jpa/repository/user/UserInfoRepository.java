package com.wondersgroup.healthcloud.jpa.repository.user;

import com.wondersgroup.healthcloud.jpa.entity.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by longshasha on 16/8/9.
 */
public interface UserInfoRepository extends JpaRepository<UserInfo,String> {
}
