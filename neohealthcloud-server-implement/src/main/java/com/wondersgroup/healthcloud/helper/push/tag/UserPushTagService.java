package com.wondersgroup.healthcloud.helper.push.tag;

import com.wondersgroup.healthcloud.helper.push.area.PushAdminSelector;
import com.wondersgroup.healthcloud.helper.push.exception.TagExistedException;
import com.wondersgroup.healthcloud.helper.push.getui.PushAdminClient;
import com.wondersgroup.healthcloud.jpa.entity.app.UserPushInfo;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.entity.push.UserPushTag;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.app.UserPushInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.UserPushTagRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
 * Created by zhangzhixiu on 8/17/16.
 */
@Component
public class UserPushTagService {

    @Autowired
    private PushTagRepository tagRepository;

    @Autowired
    private UserPushTagRepository userTagRepository;

    @Autowired
    private UserPushInfoRepository userPushInfoRepository;

    @Autowired
    private PushAdminSelector pushAdminSelector;

    @Autowired
    private RegisterInfoRepository registerInfoRepo;


    public PushTag createNewTag(String tagName) {
        PushTag pushTag = tagRepository.findByName(tagName);
        if (pushTag != null) {
            throw new TagExistedException();
        } else {
            pushTag = new PushTag();
            pushTag.setTagname(tagName);
            pushTag.setUpdatetime(new Date());
            return tagRepository.save(pushTag);
        }
    }

    public void deleteOneTag(Integer id) {
        userTagRepository.deleteByTag(id);//todo
        tagRepository.delete(id);
    }

    public Set<String> getIdsByUid(String uid) {
        return userTagRepository.getIdsByUid(uid);
    }

    public void bindTagsToOneUser(String uid, String... tagIds) {
        Set<String> ids = getIdsByUid(uid);
        for (String tagId : tagIds) {
            if (!ids.contains(tagId)) {
                UserPushTag userPushTag = new UserPushTag();
                userPushTag.setUid(uid);
                userPushTag.setTagid(Integer.valueOf(tagId));
                userTagRepository.save(userPushTag);
            }
        }

        bindTagToClient(uid);
    }

    private void bindTagToClient(String uid) {
        UserPushInfo userPushInfo = userPushInfoRepository.findByUid(uid);
        if (userPushInfo != null) {
            Set<String> tags = getIdsByUid(uid);
            PushAdminClient client = pushAdminSelector.getByArea(userPushInfo.getArea(), false);
            client.overrideTagToClient(userPushInfo.getCid(), new LinkedList<>(tags));
        }
    }

    public void bindTag(String[] uids, String tagname) {
        PushTag pushTag = tagRepository.findByName(tagname);
        if(null == pushTag){
            pushTag = new PushTag();
            pushTag.setTagname(tagname);
            pushTag.setUpdatetime(new Date());
            pushTag = tagRepository.save(pushTag);
        }

        for(String uid : uids){
            this.bindTagsToOneUser(uid,pushTag.getTagid().toString());
        }
    }

    public List<RegisterInfo> bindPerson(String info, String tagname) {
        PushTag pushTag = tagRepository.findByName(tagname);
        if(null == pushTag){
            pushTag = new PushTag();
            pushTag.setTagname(tagname);
            pushTag.setUpdatetime(new Date());
            pushTag = tagRepository.save(pushTag);
        }

        List<RegisterInfo> list = registerInfoRepo.getByCardOrPhone(info);
        for(RegisterInfo register  : list){
            this.bindTagsToOneUser(register.getRegisterid(),pushTag.getTagid().toString());
        }
        return list;

    }
}
