package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.services.user.message.UserPrivateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping(path = "/message")
public class PushController {

    public static final Logger logger = LoggerFactory.getLogger(PushController.class);

    private static final String error = "{\"code\":1000,\"msg\":\"未找到对应推送客户端\"}";

    @Autowired
    private PushAreaService pushAreaService;

    @Autowired
    private UserPrivateMessageService userPrivateMessageService;

    @PostMapping(path = "/push/single", produces = "application/json")
    public String pushToAlias(@RequestBody AppMessage pushMessage,
                              @RequestParam String alias,
                              @RequestParam(name = "is_doctor", defaultValue = "false") Boolean isDoctor) {
        PushClient client;
        if (isDoctor) {
            client = pushAreaService.getByDoctor(alias);
            if (client == null) {
                pushMessage.area = client.identityName().substring(0, client.identityName().length() - 1);
            } else {
                return error;
            }
        } else {
            client = pushAreaService.getByUser(alias);
            if (client == null) {
                pushMessage.area = client.identityName();
            } else {
                return error;
            }
        }
        client.pushToAlias(pushMessage.toPushMessage(), alias);
        userPrivateMessageService.saveOneMessage(pushMessage, alias);
        return "{\"code\":0}";
    }

    @PostMapping(path = "/push/all", produces = "application/json")
    public String pushToAll(@RequestBody AppMessage pushMessage,
                            @RequestParam String area,
                            @RequestParam(name = "is_doctor", defaultValue = "false") Boolean isDoctor) {
        PushClient client = pushAreaService.getByArea(area, isDoctor);
        if (client == null) {
            return error;
        }
        pushMessage.area = area;
        client.pushToAll(pushMessage.toPushMessage());
        return "{\"code\":0}";
    }

    @PostMapping(path = "/push/tag", produces = "application/json")
    public String pushToTag(@RequestBody AppMessage pushMessage,
                            @RequestParam String area,
                            @RequestParam String tags,
                            @RequestParam(name = "is_doctor", defaultValue = "false") Boolean isDoctor) {
        PushClient client = pushAreaService.getByArea(area, isDoctor);
        if (client == null) {
            return error;
        }
        pushMessage.area = area;
        client.pushToTags(pushMessage.toPushMessage(), Lists.newArrayList(tags.split(",")));
        return "{\"code\":0}";
    }
}
