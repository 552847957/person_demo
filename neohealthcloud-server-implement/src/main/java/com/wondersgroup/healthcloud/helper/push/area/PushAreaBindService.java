package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.push.getui.PushAdminClient;
import com.wondersgroup.healthcloud.helper.push.tag.UserPushTagService;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Set;

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
 * Created by zhangzhixiu on 8/21/16.
 */
@Component
public class PushAreaBindService {

    @Autowired
    private UserPushInfoRepository userPushInfoRepository;

    @Autowired
    private UserPushTagService userPushTagService;

    @Autowired
    private PushAdminSelector pushAdminSelector;

    public void bindInfoAfterSignin(String uid, String cid, String area) {
        unbindInfoAfterSignout(uid);//signout first and then sign in

        UserPushInfo clientLastSignin = userPushInfoRepository.findByCid(cid);
        if (clientLastSignin != null) {
            userPushInfoRepository.delete(clientLastSignin.getId());
        }
        bindTagToClient(uid, cid, area);
        bindUidAndCid(uid, cid, area);
    }

    public void unbindInfoAfterSignout(String uid) {
        UserPushInfo userLastSignin = userPushInfoRepository.findByUid(uid);
        if (userLastSignin != null) {
            userPushInfoRepository.delete(userLastSignin.getId());

            PushAdminClient client = pushAdminSelector.getByArea(userLastSignin.getArea(), false);//clear tag binded to previous device
            client.overrideTagToClient(userLastSignin.getCid(), new LinkedList<String>());
        }
    }

    private void bindUidAndCid(String uid, String cid, String area) {
        UserPushInfo userPushInfo = new UserPushInfo();
        userPushInfo.setId(IdGen.uuid());
        userPushInfo.setUid(uid);
        userPushInfo.setCid(cid);
        userPushInfo.setArea(area);
        userPushInfoRepository.save(userPushInfo);
    }

    private void bindTagToClient(String uid, String cid, String area) {
        Set<String> tags = userPushTagService.getIdsByUid(uid);
        PushAdminClient client = pushAdminSelector.getByArea(area, false);
        client.overrideTagToClient(cid, new LinkedList<>(tags));
    }
}
