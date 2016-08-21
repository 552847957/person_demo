package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.common.utils.IdGen;
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
 * Created by zhangzhixiu on 8/21/16.
 */
@Component
public class PushAreaBindService {

    @Autowired
    private UserPushInfoRepository userPushInfoRepository;

    public void bindInfoAfterSignin(String uid, String cid, String area) {
        unbindInfoAfterSignout(uid);//signout first and then sign in
        UserPushInfo clientLastSignin = userPushInfoRepository.findByCid(cid);
        if (clientLastSignin != null) {
            unbindTagFromClient(clientLastSignin.getCid());
            userPushInfoRepository.delete(clientLastSignin.getId());
        }
        bindTagToClient(uid, cid);
        bindUidAndCid(uid, cid, area);
    }

    public void unbindInfoAfterSignout(String uid) {
        UserPushInfo userLastSignin = userPushInfoRepository.findByUid(uid);
        if (userLastSignin != null) {
            unbindTagFromClient(userLastSignin.getCid());
            userPushInfoRepository.delete(userLastSignin.getId());
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

    private void bindTagToClient(String uid, String cid) {
        //todo
    }

    private void unbindTagFromClient(String cid) {
        //todo
    }
}
