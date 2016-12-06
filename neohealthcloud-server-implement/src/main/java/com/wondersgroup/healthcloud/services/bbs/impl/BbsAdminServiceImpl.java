package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("bbsAdminService")
public class BbsAdminServiceImpl implements BbsAdminService {

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public Boolean bindAppUser(String adminId, String mobile) {
        User adminInfo = userRepository.findOne(adminId);
        if (null == adminInfo){
            throw new CommonException(2001, "管理员无效");
        }
        //暂不提供修改/取消关联, 如要做记得要先把原关联的app user管理员重置掉
        if (StringUtils.isNotEmpty(adminInfo.getBindUid())){
            throw new CommonException(2002, "已关联手机段用户!");
        }
        RegisterInfo registerInfo = registerInfoRepository.findByMobile(mobile);
        if (null == registerInfo){
            throw new CommonException(2002, "手机号没有注册!");
        }
        User bindUser = userRepository.findByBindUid(registerInfo.getRegisterid());
        if (null != bindUser && !bindUser.getUserId().equals(adminId)){
            throw new CommonException(2003, "该手机号已被其他管理员绑定!");
        }
        //给绑定的用户设置手机断管理员
        registerInfo.setBanStatus(UserConstant.BanStatus.OK);
        registerInfo.setIsBBsAdmin(1);
        registerInfo.setUpdateDate(new Date());
        registerInfoRepository.saveAndFlush(registerInfo);
        //关联admin管理员
        adminInfo.setBindUid(registerInfo.getRegisterid());
        userRepository.saveAndFlush(adminInfo);
        return true;
    }

    @Override
    public void cancelBBSAdmin(String mobile) {

    }

    @Override
    public List<String> getAssociationUidsByAdminId(String admin_bindUid) {
        return null;
    }
}
