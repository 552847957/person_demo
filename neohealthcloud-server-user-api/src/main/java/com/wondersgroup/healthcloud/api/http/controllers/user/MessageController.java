package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.user.MessageDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.services.user.UserPrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
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
    public JsonListResponseEntity<MessageDTO> rootList(@RequestHeader("main-area") String area,
                                                       @RequestParam String uid) {
        List<UserPrivateMessage> results = messageService.findRoot(area, uid);
        JsonListResponseEntity<MessageDTO> response = new JsonListResponseEntity<>();
        List<MessageDTO> messages = Lists.newLinkedList();
        for (UserPrivateMessage result : results) {
            MessageDTO message = new MessageDTO(result, area);
            message.id = null;
            message.url = null;
            message.title = AppMessageUrlUtil.Type.getById(message.type).name;
            messages.add(message);
        }
        response.setContent(messages);
        return response;
    }

    @GetMapping(path = "/type")
    @VersionRange
    public JsonListResponseEntity<MessageDTO> typeList(@RequestHeader("main-area") String area,
                                                       @RequestParam String uid,
                                                       @RequestParam String type,
                                                       @RequestParam(required = false) Long flag) {
        List<UserPrivateMessage> results = messageService.findType(area, uid, type, flag);
        JsonListResponseEntity<MessageDTO> response = new JsonListResponseEntity<>();
        LinkedList<MessageDTO> messages = Lists.newLinkedList();
        int count = 0;
        String newFlag = null;
        for (UserPrivateMessage result : results) {
            if (count < 10) {
                MessageDTO message = new MessageDTO(result, area);
                messages.add(message);
            } else {
                newFlag = String.valueOf(messages.getLast().nativeMessage.getCreateTime().getTime() / 1000L);
                break;
            }
            count++;
        }
        if (newFlag != null) {
            response.setContent(messages, true, "time_desc", newFlag);
        } else {
            response.setContent(messages);
        }
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
