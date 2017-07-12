package com.wondersgroup.healthcloud.api.http.controllers.message;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.message.MessageCenterServiceImpl;
import com.wondersgroup.healthcloud.services.user.message.dto.MessageCenterDto;
import com.wondersgroup.healthcloud.services.user.message.enums.MsgTypeEnum;
import com.wondersgroup.healthcloud.services.user.message.exception.EnumMatchException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 1. 首页消息中心入口红点提示接口 <br/>
 * 2. 消息中心根列表接口 <br/>
 * 3. 消息状态设置接口 <br/>
 * Created by jialing.yao on 2016-12-12.
 */
@RestController
@RequestMapping(path = "/api/msgcenter")
public class MsgCenterController {
    @Autowired
    private MessageCenterServiceImpl messageCenterService;

    /**
     * 首页消息中心入口红点提示接口 新老版本可通用
     * @param uid
     * @return
     */
    @GetMapping(path = "/prompt")
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> prompt(@RequestParam String uid) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("has_unread", messageCenterService.hasUnread(uid));
        return new JsonResponseEntity<>(0, null, map);
    }

    @GetMapping(path = "/index")
    @VersionRange(to = "4.3")
    public JsonListResponseEntity<MessageCenterDto> rootList(@RequestHeader("main-area") String area,
                                                       @RequestParam String uid) {
        List<MessageCenterDto> rootList=messageCenterService.getRootList(area,uid);
        JsonListResponseEntity<MessageCenterDto> response = new JsonListResponseEntity();
        response.setContent(rootList);
        return response;
    }

    @PostMapping(path = "/message/status")
    @VersionRange
    public JsonResponseEntity<Map<String, Object>> status(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String msgType = reader.readString("msgType", false);
        String msgID = reader.readString("msgID", false);

        //此接口只跟系统消息、我的咨询、家庭消息、慢病消息有关
        MsgTypeEnum.fromTypeCode(msgType);
        if(!msgType.equals(MsgTypeEnum.msgType0.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType1.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType2.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType5.getTypeCode())){
            throw new EnumMatchException("消息类型["+msgType+"]不匹配.");
        }
        messageCenterService.setAsRead(msgType,msgID);
        Map<String, Object> map = Maps.newHashMap();
        map.put("read", true);
        return new JsonResponseEntity<>(0, null, map);
    }


    /**
     * 消息中心首页接口
     * @param area
     * @param uid
     * @return
     * lss
     */
    @GetMapping(path = "/index")
    @VersionRange(from = "4.4")
    public JsonListResponseEntity<MessageCenterDto> msgRootList(@RequestHeader("main-area") String area,
                                                             @RequestParam String uid) {
        List<MessageCenterDto> rootList=messageCenterService.getMsgRootList(area, uid);
        JsonListResponseEntity<MessageCenterDto> response = new JsonListResponseEntity();
        response.setContent(rootList);
        return response;
    }

    //单条设置已读 只针对系统消息、我的咨询、家庭消息、 医生建议 、报告提醒 、随访提醒
    @PostMapping(path = "/message/status")
    @VersionRange(from = "4.4")
    public JsonResponseEntity<Map<String, Object>> setAsRead(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String msgType = reader.readString("msgType", false);
        String msgID = reader.readString("msgID", false);

        //此接口只跟系统消息、我的咨询、家庭消息、 医生建议 、报告提醒 、随访提醒
        MsgTypeEnum.fromTypeCode(msgType);
        if(!msgType.equals(MsgTypeEnum.msgType0.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType1.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType2.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType6.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType7.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType8.getTypeCode())){
            throw new EnumMatchException("消息类型["+msgType+"]不匹配.");
        }
        messageCenterService.setAsReadV4(msgType, msgID);
        Map<String, Object> map = Maps.newHashMap();
        map.put("read", true);
        return new JsonResponseEntity<>(0, null, map);
    }


    /**
     * 除家庭消息外可以删除
     * 单条删除消息
     * @param msgType
     * @param msgID
     * @return
     */
    @DeleteMapping(path = "/delete")
    @VersionRange
    public JsonResponseEntity<String> delateMsg(
                    @RequestParam(required = true) String msgType,
                    @RequestParam(required = true) String msgID) {
        MsgTypeEnum.fromTypeCode(msgType);
        if(!msgType.equals(MsgTypeEnum.msgType0.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType1.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType4.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType6.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType7.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType8.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType9.getTypeCode())){
            throw new EnumMatchException("消息类型["+msgType+"]不匹配.");
        }
        messageCenterService.delateMsg(msgType, msgID);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        response.setMsg("删除成功");
        return response;
    }

    /**
     * 根据消息类型批量删除(除去家庭消息)
     * @param uid
     * @param msgType
     * @param bbsType 1:动态消息 2:系统消息
     * @return
     */
    @DeleteMapping(path = "/msgTypeDelete")
    @VersionRange
    public JsonResponseEntity<String> delateMsgByType(@RequestParam(required = true) String uid,
                                                      @RequestParam(required = true) String msgType,
                                                      @RequestParam(required = false) String bbsType) {
        MsgTypeEnum.fromTypeCode(msgType);
        if(!msgType.equals(MsgTypeEnum.msgType0.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType1.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType4.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType6.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType7.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType8.getTypeCode())
                && !msgType.equals(MsgTypeEnum.msgType9.getTypeCode())){
            throw new EnumMatchException("消息类型["+msgType+"]不匹配.");
        }

        if(msgType.equals(MsgTypeEnum.msgType4.getTypeCode())
        && (StringUtils.isBlank(bbsType) || !(bbsType.equals("1") || bbsType.equals("2")) )){
            throw new EnumMatchException("健康圈消息类型需传bbsType");
        }
        messageCenterService.delateAllMsg(uid, msgType, bbsType);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        response.setMsg("删除成功");
        return response;
    }

}
