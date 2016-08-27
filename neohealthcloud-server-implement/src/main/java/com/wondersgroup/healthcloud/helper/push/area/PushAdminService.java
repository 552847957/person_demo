package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.helper.push.getui.PushAdminClient;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Created by zhangzhixiu on 8/16/16.
 */
@Component
public class PushAdminService {

    private PushAdminSelector selector;
    private UserPushInfoRepository userPushInfoRepository;

    public void setTags(String cid, String area, List<String> tags) {
        PushAdminClient adminClient = selector.getByArea(area, false);
        adminClient.overrideTagToClient(cid, tags);
    }

    public void setTags(String uid, List<String> tags) {
        UserPushInfo userPushInfo = userPushInfoRepository.findByUid(uid);
        if (userPushInfo != null) {
            PushAdminClient adminClient = selector.getByArea(userPushInfo.getArea(), false);
            adminClient.overrideTagToClient(userPushInfo.getCid(), tags);
        }
    }

    @Autowired
    public void setSelector(PushAdminSelector selector) {
        this.selector = selector;
    }

    @Autowired
    public void setUserPushInfoRepository(UserPushInfoRepository userPushInfoRepository) {
        this.userPushInfoRepository = userPushInfoRepository;
    }
}
