package com.wondersgroup.healthcloud.services.user.message;

import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

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
 * Created by zhangzhixiu on 16/4/6.
 */
@Service("messageReadServiceImpl")
public class MessageReadServiceImpl implements MessageReadService {

    private static final String[] types;
    private JedisPool pool;

    static {
        AppMessageUrlUtil.Type[] values = AppMessageUrlUtil.Type.values();
        types = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = values[i].id;
        }
    }

    private String key(String userId, String type) {
        return "nhc:msg:unread:t:" + type + ":u:" + userId;
    }

    @Override
    public void newMessage(UserPrivateMessage privateMessage) {
        try (Jedis jedis = pool.getResource()) {
            jedis.sadd(key(privateMessage.getUid(), privateMessage.getType()), privateMessage.getId());
        }
    }

    @Override
    public void setAsRead(UserPrivateMessage privateMessage) {
        try (Jedis jedis = pool.getResource()) {
            jedis.srem(key(privateMessage.getUid(), privateMessage.getType()), privateMessage.getId());
        }
    }

    @Override
    public Boolean hasUnread(String userId) {
        try (Jedis jedis = pool.getResource()) {
            for (String type : types) {
                if (jedis.scard(key(userId, type)).intValue() > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public int unreadCountByType(String userId, String... types) {
        try (Jedis jedis = pool.getResource()) {
            int count = 0;
            for (String type : types) {
                count += jedis.scard(key(userId, type)).intValue();
            }
            return count;
        }
    }

    @Override
    public void isRead(List<UserPrivateMessage> privateMessages) {
        try (Jedis jedis = pool.getResource()) {
            for (UserPrivateMessage privateMessage : privateMessages) {
                privateMessage.setIsRead(!jedis.sismember(key(privateMessage.getUid(), privateMessage.getType()), privateMessage.getId()));
            }
        }
    }

    @Override
    public void setAllAsRead(String uid, String type) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(key(uid, type));
        }
    }

    @Autowired
    public void setPool(JedisPool pool) {
        this.pool = pool;
    }
}
