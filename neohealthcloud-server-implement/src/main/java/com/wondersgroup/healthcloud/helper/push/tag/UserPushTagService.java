package com.wondersgroup.healthcloud.helper.push.tag;

import com.wondersgroup.healthcloud.helper.push.exception.TagExistedException;
import com.wondersgroup.healthcloud.jpa.entity.push.PushTag;
import com.wondersgroup.healthcloud.jpa.entity.push.UserPushTag;
import com.wondersgroup.healthcloud.jpa.repository.push.PushTagRepository;
import com.wondersgroup.healthcloud.jpa.repository.push.UserPushTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
 * Created by zhangzhixiu on 8/17/16.
 */
@Component
public class UserPushTagService {

    @Autowired
    private PushTagRepository tagRepository;

    @Autowired
    private UserPushTagRepository userTagRepository;


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
        tagRepository.delete(id);
    }

    private void bindOneTagToOneUser(String uid, Integer tagId) {
        UserPushTag userPushTag = new UserPushTag();
        //todo
    }

    public void bindTagsToOneUser(String uid, List<Integer> tagIds) {

    }
}
