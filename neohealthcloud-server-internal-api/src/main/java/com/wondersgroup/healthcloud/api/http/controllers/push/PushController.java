package com.wondersgroup.healthcloud.api.http.controllers.push;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.helper.push.api.PushClientWrapper;
import com.wondersgroup.healthcloud.helper.push.area.PushAreaService;
import com.wondersgroup.healthcloud.helper.push.getui.PushClient;
import com.wondersgroup.healthcloud.jpa.entity.question.ReplyGroup;
import com.wondersgroup.healthcloud.jpa.repository.question.ReplyGroupRepository;
import com.wondersgroup.healthcloud.services.user.message.UserPrivateMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping(path = "/message")
public class PushController {

    public static final Logger logger = LoggerFactory.getLogger(PushController.class);

    private static final String error = "{\"code\":1000,\"msg\":\"未找到对应推送客户端\"}";

    @Autowired
    private PushAreaService pushAreaService;

    @Autowired
    private UserPrivateMessageService userPrivateMessageService;

    @Autowired
    private PushClientWrapper pushClientWrapper;

    @Autowired
    private ReplyGroupRepository replyGroupRepository;

    @PostMapping(path = "/push/single", produces = "application/json")
    public String pushToAlias(@RequestBody AppMessage pushMessage,
                              @RequestParam String alias) {
        PushClient client;
        if (pushMessage.isDoctor) {
            client = pushAreaService.getByDoctor(alias);
            if (client != null) {
                pushMessage.area = client.identityName().substring(0, client.identityName().length() - 1);
            } else {
                return error;
            }
        } else {
            client = pushAreaService.getByUser(alias);
            if (client != null) {
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
                            @RequestParam String area) {
        PushClient client = pushAreaService.getByArea(area, pushMessage.isDoctor);
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
                            @RequestParam String tags) {
        PushClient client = pushAreaService.getByArea(area, pushMessage.isDoctor);
        if (client == null) {
            return error;
        }
        pushMessage.area = area;
        client.pushToTags(pushMessage.toPushMessage(), Lists.newArrayList(tags.split(",")));
        return "{\"code\":0}";
    }

    @PostMapping(path = "/plan", produces = "application/json")
    public String plan(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String planId = reader.readString("planId", false);

        return "{\"code\":0}";
    }

    @PostMapping(path = "/closeQuestion/psuh", produces = "application/json")
    public String closeQuestionPush(@RequestBody String request) {
        JsonKeyReader reader = new JsonKeyReader(request);
        String userId = reader.readString("userId", false);
        String questionId = reader.readString("questionId", false);
        List<ReplyGroup> commentGroupList = replyGroupRepository.getCommentGroupList(questionId);

        if (commentGroupList.isEmpty()){
            AppMessage message= AppMessage.Builder.init().title("您的问题已关闭").content("您提交的问题已经关闭")
                    .type(AppMessageUrlUtil.Type.QUESTION).urlFragment(AppMessageUrlUtil.question(questionId)).persistence().build();
            Boolean aBoolean = pushClientWrapper.pushToAlias(message, userId);
        }
        return "{\"code\":0}";
    }
}
