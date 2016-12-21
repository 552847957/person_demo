package com.wondersgroup.healthcloud.helper.push.area;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.push.getui.PushAdminClient;
import com.wondersgroup.healthcloud.helper.push.tag.UserPushTagService;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
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
@Transactional(readOnly = true)
public class PushAreaBindService {

    @Autowired
    private UserPushInfoRepository userPushInfoRepository;

    @Autowired
    private UserPushTagService userPushTagService;

    @Autowired
    private PushAdminSelector pushAdminSelector;

    @Transactional
    public void bindInfoAfterSignin(String uid, String cid, String area) {
        unbindInfoAfterSignout(uid);//signout first and then sign in

        List<UserPushInfo> byCid = userPushInfoRepository.findByCid(cid);
        userPushInfoRepository.delete(byCid);

        bindTagToClient(uid, cid, area);
        bindUidAndCid(uid, cid, area);
    }

    @Transactional
    public void unbindInfoAfterSignout(String uid) {
        List<UserPushInfo> userLastSignin = userPushInfoRepository.findByUid(uid);
        for (UserPushInfo userPushInfo : userLastSignin) {
            userPushInfoRepository.delete(userPushInfo.getId());

            PushAdminClient client = pushAdminSelector.getByArea(userPushInfo.getArea(), false);//clear tag binded to previous device
//            client.overrideTagToClient(userPushInfo.getCid(), new LinkedList<String>());
            client.unbindAliasAll(uid);
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
