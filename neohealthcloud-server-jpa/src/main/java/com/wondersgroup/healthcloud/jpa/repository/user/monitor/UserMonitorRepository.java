package com.wondersgroup.healthcloud.jpa.repository.user.monitor;

import com.wondersgroup.healthcloud.jpa.entity.user.monitor.UserMonitor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhaozhenxing on 2016/12/13.
 */

public interface UserMonitorRepository extends JpaRepository<UserMonitor, String> {
    UserMonitor findByUid(String uid);
}