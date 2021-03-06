package com.wondersgroup.healthcloud.api.helper;

import com.wondersgroup.healthcloud.jpa.entity.app.AppKeyConfigurationInfo;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.repository.app.AppConfigurationInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by Administrator on 2015/12/1.
 */
@Component
public class UserHelper {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AppConfigurationInfoRepository appConfigurationInfoRepository;

    /**
     * 获取当前用户
     * @return
     */
    public User getCurrentUser(){
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if(StringUtils.isEmpty(username)){
            return null;
        }
        User user = userRepo.findByLoginName(username);
        return user;
    }

    public AppKeyConfigurationInfo getKeyCfgByArea(String area) {
        AppKeyConfigurationInfo appKCfg = appConfigurationInfoRepository.findByArea(area);
        return appKCfg;
    }
}
