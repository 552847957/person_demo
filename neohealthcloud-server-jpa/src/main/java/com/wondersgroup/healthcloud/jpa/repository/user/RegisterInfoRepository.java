package com.wondersgroup.healthcloud.jpa.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;

/**
 * Created by longshasha on 16/8/4.
 */
public interface RegisterInfoRepository extends JpaRepository<RegisterInfo,String> {

    @Query("select r from RegisterInfo r where r.talkid =?1 and r.delFlag='0'")
    RegisterInfo findByTalkid(String talkid);

    @Query("select r from RegisterInfo r where r.regmobilephone = ?1 and r.delFlag='0'")
    RegisterInfo findByMobile(String mobile);

    @Query("select r from RegisterInfo r where r.personcard =?1 and r.identifytype!='0' and r.delFlag='0'")
    List<RegisterInfo> findByPersoncard(String personcard);

    @Query("select r from RegisterInfo r where r.registerid =?1 and r.delFlag='0'")
    RegisterInfo findByRegisterid(String registerId);

    @Query("select r from RegisterInfo r where (r.personcard =?1 or r.regmobilephone = ?1) and r.delFlag='0'")
    List<RegisterInfo> getByCardOrPhone(String info);
    
    @Transactional
	@Modifying
    @Query("update RegisterInfo set bindPersoncard=?1 where registerId =?2")
    int updateByRegister(String bindPersoncard, String registerId);

    @Query("select count(a) from RegisterInfo where nickname=?1")
    int countRegisterInfoByNickname(String nickname);
}
