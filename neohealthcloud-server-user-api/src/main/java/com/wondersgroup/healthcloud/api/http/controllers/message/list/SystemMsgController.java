package com.wondersgroup.healthcloud.api.http.controllers.message.list;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.user.MessageDTO;
import com.wondersgroup.healthcloud.common.appenum.SysMsgTypeEnum;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.services.user.message.MessageReadService;
import com.wondersgroup.healthcloud.services.user.message.UserPrivateMessageService;
import com.wondersgroup.healthcloud.services.user.message.enums.MsgTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * 消息列表-系统消息/我的咨询接口
 * Created by jialing.yao on 2016-12-12.
 */
@RestController
@RequestMapping("/api/system/message")
public class SystemMsgController {
    @Autowired
    private UserPrivateMessageService messageService;
    @Autowired
    private MessageReadService messageReadService;

    @GetMapping(path = "/list")
    @VersionRange(to = "4.3")
    public JsonListResponseEntity<MessageDTO> sysMsgList(@RequestHeader("main-area") String area,
                                                       @RequestParam String uid,
                                                       @RequestParam String type,
                                                       @RequestParam(required = false) Long flag) {
        List<UserPrivateMessage> results = messageService.findType(area, uid, type, flag);
        messageReadService.isRead(results);
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

    /**
     * 4.4版本原系统消息 + 血糖测量提醒(存在慢病消息表)
     * @param area
     * @param uid
     * @param type 0 系统消息,1我的咨询
     * @param flag
     * @return
     */
    @GetMapping(path = "/list")
    @VersionRange(from = "4.4")
    public JsonListResponseEntity<MessageDTO> sysMsg4List(@RequestHeader("main-area") String area,
                                                         @RequestParam String uid,
                                                         @RequestParam String type,
                                                          @RequestParam(defaultValue = "0") String flag) {

        JsonListResponseEntity<MessageDTO> response = new JsonListResponseEntity<>();
        LinkedList<MessageDTO> messages = Lists.newLinkedList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<UserPrivateMessage> results = messageService.findSystemMsgList(area, uid,type, pageNo,pageSize);
        messageReadService.isRead(results);
        for (UserPrivateMessage result : results) {
            if (messages.size()<pageSize) {
                MessageDTO message = new MessageDTO(result, area);
                if(result.getType().equals("3")){
                    message.isRead = result.getXtIsRead().equals("1")?true:false;
                    message.type = MsgTypeEnum.msgType0.getTypeCode();
                }
                messages.add(message);
            }
        }
        if(results.size()>pageSize){
            more = true;
            flag = String.valueOf(pageNo+1);

        }
        response.setContent(messages, more, null, flag);
        return response;
    }


}
