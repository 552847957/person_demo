package com.wondersgroup.healthcloud.services.user.impl;

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
//        String caseSql = " case type when '4' then 'report' when '6' then 'report' when '3' then 'report' when '2H' then 'questionnaire' else 'system' end ";
//        String query = String.format(" select a.id,a.type,a.time,a.content,a.title,a.args,a.newtype from  "
//                + " ( select id,time,type,content,title,args, " + caseSql + " as newtype from user_private_message_tb where (uid= '%s' or uid is null) and time>FROM_UNIXTIME(%d)) a  "
//                + " inner join "
//                + " (select max(id) id,time," + caseSql + " as newtype from user_private_message_tb where (uid= '%s' or uid is null) and time>FROM_UNIXTIME(%d) group by " + caseSql + " ,time) b on a.id=b.id "
//                + " INNER JOIN (select max(time) as time, " + caseSql + " as newtype from user_private_message_tb where (uid= '%s' or uid is null) and time>FROM_UNIXTIME(%d) "
//                + " group by " + caseSql + ") c on a.time=c.time and a.newtype=c.newtype order by newtype", uid, startTime, uid, startTime, uid, startTime);
//        List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
//        List<PrivateMessage> messages = Lists.newLinkedList();
//        for (Map<String, Object> result : results) {
//            messages.add(buildFrom(uid, result));
//        }
//        return messages;
        return null;
    }

    @Override
    public List<UserPrivateMessage> findType(String uid, String typeId) {
        return null;
    }
}
