package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p>
 * Created by zhangzhixiu on 8/15/16.
 */
@Component
public class PushAreaService {

    @Autowired
    private UserPushInfoRepository userPushInfoRepository;

    @Autowired
    private PushClientSelector pushClientSelector;

    public PushClient getByUser(String uid) {
        UserPushInfo userPushInfo = userPushInfoRepository.findByUid(uid);
        if (userPushInfo == null) {
            return null;
        } else {
            return pushClientSelector.getByArea(userPushInfo.getArea(), false);
        }
    }

    public PushClient getByDoctor(String doctorId) {
        return null;//todo
    }

    public PushClient getByArea(String area, Boolean isDoctor) {
        return pushClientSelector.getByArea(area, isDoctor);
    }
}
