package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.user.MessageDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.services.user.UserPrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
 * Created by zhangzhixiu on 8/19/16.
 */
@RestController
@RequestMapping(path = "/api/message")
public class MessageController {

    @Autowired
    private UserPrivateMessageService messageService;

    @GetMapping(path = "/root")
    @VersionRange
    public List<UserPrivateMessage> rootList(@RequestHeader("main-area") String area,
                                             @RequestParam String uid) {
        return messageService.findRoot(uid);
//        JsonListResponseEntity<MessageDTO> response = new JsonListResponseEntity<>();
//        List<MessageDTO> messages = Lists.newLinkedList();
//        MessageDTO m1 = new MessageDTO();
//        m1.title = "系统消息";
//        m1.content = "实名认证成功";
//        m1.isRead = false;
//        m1.type = "system";
//        m1.time = new Date();
//        messages.add(m1);
//        MessageDTO m2 = new MessageDTO();
//        m2.title = "轻问诊";
//        m2.content = "你好.....";
//        m2.isRead = true;
//        m2.type = "question";
//        m2.time = new Date();
//        messages.add(m2);
//        response.setContent(messages);
//        return response;
    }

    @GetMapping(path = "/type")
    @VersionRange
    public JsonListResponseEntity<MessageDTO> typeList(@RequestHeader("main-area") String area,
                                                       @RequestParam String uid,
                                                       @RequestParam String type,
                                                       @RequestParam(required = false) String flag) {
        JsonListResponseEntity<MessageDTO> response = new JsonListResponseEntity<>();
        List<MessageDTO> messages = Lists.newLinkedList();
        MessageDTO m1 = new MessageDTO();
        if ("system".equals(type)) {
            m1.id = "1";
            m1.title = "实名认证结果";
            m1.content = "实名认证成功";
            m1.isRead = false;
            m1.type = "system";
            m1.url = "com.wondersgroup.healthcloud." + area + "://user/verification";
            m1.time = new Date();
            messages.add(m1);
        } else {
            MessageDTO m2 = new MessageDTO();
            m2.id = "2";
            m2.title = "轻问诊";
            m2.content = "你好.....";
            m2.isRead = true;
            m2.type = "question";
            m2.url = "com.wondersgroup.healthcloud." + area + "://user/question?id=222";
            m2.time = new Date();
            messages.add(m2);
        }
        response.setContent(messages);
        return response;
    }

    @GetMapping(path = "/prompt")
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> prompt(@RequestParam String uid) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("has_unread", true);

        return new JsonResponseEntity<>(0, null, map);
    }

    @PostMapping(path = "/status")
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> status(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String messageId = reader.readString("message_id", false);
        Map<String, Object> map = Maps.newHashMap();
        map.put("read", true);

        return new JsonResponseEntity<>(0, null, map);
    }
}
