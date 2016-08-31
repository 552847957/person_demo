package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
public class PushAreaService {

    private UserPushInfoRepository userPushInfoRepository;
    private PushClientSelector pushClientSelector;


    public PushClient getByUser(String uid) {
        List<UserPushInfo> list = userPushInfoRepository.findByUid(uid);
        if (list.size() == 0) {
            return null;
        } else {
            return pushClientSelector.getByArea(list.get(0).getArea(), false);
        }
    }

    public PushClient getByDoctor(String doctorId) {
        return null;//todo
    }

    public PushClient getByArea(String area, Boolean isDoctor) {
        return pushClientSelector.getByArea(area, isDoctor);
    }

    @Autowired
    public void setUserPushInfoRepository(UserPushInfoRepository userPushInfoRepository) {
        this.userPushInfoRepository = userPushInfoRepository;
    }

    @Autowired
    public void setPushClientSelector(PushClientSelector pushClientSelector) {
        this.pushClientSelector = pushClientSelector;
    }
}
