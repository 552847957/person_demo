package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.WechatRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhuchunliu on 2016/9/22.
 */
public interface WechatRegisterRepository extends JpaRepository<WechatRegister, String>,JpaSpecificationExecutor<WechatRegister> {

    @Query(value = "select a from WechatRegister a where  a.openid = ?1 and a.delFlag = '0'")
    WechatRegister getByOpenId(String openId);
}
