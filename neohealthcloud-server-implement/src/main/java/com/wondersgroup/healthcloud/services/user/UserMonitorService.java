package com.wondersgroup.healthcloud.services.user;

import com.wondersgroup.healthcloud.jpa.entity.user.monitor.UserMonitor;

/**
 * Created by zhaozhenxing on 2016/12/13.
 */

public interface UserMonitorService {
    UserMonitor detail(String uid);
    UserMonitor saveAndUpdate(UserMonitor userMonitor);
}