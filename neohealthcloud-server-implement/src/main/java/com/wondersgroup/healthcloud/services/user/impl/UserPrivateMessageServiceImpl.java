package com.wondersgroup.healthcloud.services.user.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.jpa.repository.user.UserPrivateMessageRepository;
import com.wondersgroup.healthcloud.services.user.UserPrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
 * Created by zhangzhixiu on 8/21/16.
 */
@Service("userPrivateMessageServiceImpl")
public class UserPrivateMessageServiceImpl implements UserPrivateMessageService {

    @Autowired
    private UserPrivateMessageRepository messageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveOneMessage(AppMessage message, String uid) {
        if (message.persistence) {
            UserPrivateMessage userPrivateMessage = new UserPrivateMessage();
            userPrivateMessage.setId(IdGen.uuid());
            userPrivateMessage.setUid(uid);
            userPrivateMessage.setTitle(message.title);
            userPrivateMessage.setContent(message.content);
            if (message.areaSpecial) {
                userPrivateMessage.setArea(message.area);
            }
            userPrivateMessage.setUrl(message.urlFragment);
            userPrivateMessage.setType(message.type.id);
            userPrivateMessage.setCreateTime(new Date());

            messageRepository.save(userPrivateMessage);
        }
    }

    @Override
    public List<UserPrivateMessage> findRoot(String uid) {
        if (uid.length() != 32) {
            return Lists.newLinkedList();
        }
        String sql = "select *,max(create_time) from app_tb_user_private_message where uid=?1 group by type";
        List<UserPrivateMessage> results = jdbcTemplate.queryForList(sql, UserPrivateMessage.class, uid);
//        List<PrivateMessage> messages = Lists.newLinkedList();
//        for (Map<String, Object> result : results) {
//            messages.add(buildFrom(uid, result));
//        }
        return results;
    }

    @Override
    public List<UserPrivateMessage> findType(String uid, String typeId) {
        return null;
    }
}
