package com.wondersgroup.healthcloud.services.user.impl;

import java.util.Date;

import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.user.monitor.UserMonitor;
import com.wondersgroup.healthcloud.jpa.repository.user.monitor.UserMonitorRepository;
import com.wondersgroup.healthcloud.services.user.UserMonitorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaozhenxing on 2016/12/13.
 */

@Service
public class UserMonitorServiceImpl implements UserMonitorService {

    private static final Logger log = Logger.getLogger("exlog");

    @Autowired
    private UserMonitorRepository userMonitorRepository;

    @Override
    public UserMonitor detail(String uid) {
        UserMonitor rtnEntity = null;
        try {
            rtnEntity = userMonitorRepository.findByUid(uid);
        } catch (Exception ex) {
            log.error(Exceptions.getStackTraceAsString(ex));
        }
        return rtnEntity;
    }

    @Override
    public UserMonitor saveAndUpdate(UserMonitor userMonitor) {
        try {
            if(userMonitorRepository.findByUid(userMonitor.getUid()) == null ) {
                userMonitor.setCreateTime(new Date());
            }
            userMonitor.setUpdateTime(new Date());
            return userMonitorRepository.save(userMonitor);
        } catch (Exception ex) {
            log.error(Exceptions.getStackTraceAsString(ex));
        }
        return null;
    }
}