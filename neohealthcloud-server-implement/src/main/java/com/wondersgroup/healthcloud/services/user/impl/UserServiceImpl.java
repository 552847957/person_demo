package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUpdateGenderException;
import com.wondersgroup.healthcloud.services.user.exception.ErrorUserAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by longshasha on 16/8/4.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private JdbcTemplate jt;


    private String query = "select i.registerid ,i.`name`,i.nickname ,i.regmobilephone ,i.headphoto , " +
            " i.personcard ,i.gender ,i.identifytype ,i.talkid ,i.talkpwd ,i.tagid , " +
            " i.medicarecard ,i.bind_personcard " +
            " from app_tb_register_info i ";

    @Override
    public Map<String, Object> findUserInfoByUid(String uid) {

        String sql = query + "where i.registerid = '%s'";
        sql = String.format(sql,uid);
        return jt.queryForMap(sql);
    }

    @Override
    public RegisterInfo getOneNotNull(String id) {
        RegisterInfo registerInfo = registerInfoRepository.findOne(id);
        if(registerInfo == null){
            throw new ErrorUserAccountException();
        }
        return registerInfo;
    }

    @Override
    public Boolean updateNickname(String userId, String nickname) {
        RegisterInfo register = registerInfoRepository.findOne(userId);
        register.setNickname(nickname);
        register.setUpdateDate(new Date());
        registerInfoRepository.saveAndFlush(register);
        return true;
    }

    @Override
    public Boolean updateGender(String userId, String gender) {
        RegisterInfo register = getOneNotNull(userId);
        if (register.verified()) {
            throw new ErrorUpdateGenderException("实名认证后性别不能修改");
        }
        register.setGender(gender);
        registerInfoRepository.saveAndFlush(register);

        return true;
    }
}
