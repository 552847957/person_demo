package com.wondersgroup.healthcloud.services.user.message;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.helper.push.api.AppMessageUrlUtil;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.services.bbs.BbsSysMsgService;
import com.wondersgroup.healthcloud.services.user.message.dto.MessageCenterDto;
import com.wondersgroup.healthcloud.services.user.message.enums.MsgTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息中心
 * Created by jialing.yao on 2016-12-13.
 */
@Service("messageCenterService")
public class MessageCenterServiceImpl {
    @Autowired
    private MessageReadService messageReadService;//系统消息、我的咨询
    @Autowired
    private UserPrivateMessageService messageService;//系统消息、我的咨询
    @Autowired
    private BbsSysMsgService dynamicMsgService;//健康圈-动态消息
    @Autowired
    private BbsSysMsgService sysMsgService;//健康圈-通知消息
    @Autowired
    private MsgService familyMsgService;//家庭消息
    @Autowired
    private MsgService diseaseMsgService;//慢病消息

    /**
     * 首页消息中心入口红点提示
     */
    public Boolean hasUnread(String uid){
        //系统消息、我的咨询
        boolean hasUnReadSystem=messageReadService.hasUnread(uid);
        if(hasUnReadSystem){
            return true;
        }
        //健康圈-动态消息、通知消息
        int unReadDynamicMsg=dynamicMsgService.countOfUnReadMessages(uid);
        if(unReadDynamicMsg > 0){
            return true;
        }
        int unReadSysMsg=sysMsgService.countOfUnReadMessages(uid);
        if(unReadSysMsg > 0){
            return true;
        }
        //家庭消息
        int unReadFamilyMsg=familyMsgService.countOfUnReadMessages(uid);
        if(unReadFamilyMsg > 0){
            return true;
        }
        //慢病消息
        int unReadDiseaseMsg=diseaseMsgService.countOfUnReadMessages(uid);
        if(unReadDiseaseMsg > 0){
            return true;
        }
        return false;
    }

    //消息中心的消息类型按照健康圈消息1，家庭消息2，慢病消息3，我的咨询4和系统消息5顺序排列
    public List<MessageCenterDto> getRootList(String area, String uid){
        List<MessageCenterDto> messages = Lists.newLinkedList();
        List<MessageCenterDto> lastSystem=this.getLastSystem(area,uid);
        List<MessageCenterDto> lastBbsMsg=this.getBbsMessage(uid);
        MessageCenterDto lastFamilyMsg=this.getFamilyMsg(uid);
        MessageCenterDto lastDiseaseMsg=this.getDiseaseMsg(uid);
        messages.addAll(lastBbsMsg);
        messages.add(lastFamilyMsg);
        messages.add(lastDiseaseMsg);
        messages.addAll(lastSystem);
        return messages;
    }
    //家庭消息
    private MessageCenterDto getFamilyMsg(String uid){
        Map<String, Object> msg=familyMsgService.findOneMessageByUid(uid);
        String content="您有一条新消息";
        String msgCreateTime=String.valueOf(msg.get("create_time"));
        Date date= DateUtils.parseString(msgCreateTime);
        String time=MessageCenterDto.parseDate(date);
        MessageCenterDto message = new MessageCenterDto();
        message.setTitle(MsgTypeEnum.msgType4.getTypeName());
        message.setContent(content);
        message.setTime(time);
        message.setRead(true);//是否有未读的消息
        message.setSort(1);
        return message;
    }
    //慢病消息
    private MessageCenterDto getDiseaseMsg(String uid){
        Map<String, Object> msg=diseaseMsgService.findOneMessageByUid(uid);
        String content="";
        String type=String.valueOf(msg.get("type"));
        //干预提醒0、筛查提醒1
        if(type.equals("0")){
            content="您有一条新的干预提醒";
        }else if(type.equals("1")){
            content="您有一条新的筛查提醒";
        }
        String msgCreateTime=String.valueOf(msg.get("create_time"));
        Date date= DateUtils.parseString(msgCreateTime);
        String time=MessageCenterDto.parseDate(date);
        MessageCenterDto message = new MessageCenterDto();
        message.setTitle(MsgTypeEnum.msgType4.getTypeName());
        message.setContent(content);
        message.setTime(time);
        message.setRead(true);//是否有未读的消息
        message.setSort(1);
        return message;
    }
    /*
    消息内容为最新一条未读消息；
    如果有未读动态消息，无未读通知消息，取动态消息；
    如果有未读动态消息，有未读通知消息，取动态消息；
    如果无未读动态消息，有未读通知消息，取通知消息；
    如果无未读动态消息，无未读通知消息，为暂无数据；
     */
    private List<MessageCenterDto> getBbsMessage(String uid){
        List<MessageCenterDto> messages = Lists.newLinkedList();
        Map<String, Object> dynamicMsg=dynamicMsgService.findOneDynamicMessageByUid(uid);
        Map<String, Object> systemMsg=sysMsgService.findOneSysMessageByUid(uid);
        //如果无未读动态消息，无未读通知消息，为暂无数据；
        if(dynamicMsg == null && systemMsg==null){
            MessageCenterDto bbsMsg = new MessageCenterDto();
            bbsMsg.setTitle(MsgTypeEnum.msgType4.getTypeName());
            bbsMsg.setContent("暂无新消息");
            bbsMsg.setSort(1);
            messages.add(bbsMsg);
            return messages;
        }
        //如果有未读动态消息，无未读通知消息，取动态消息；
        //如果有未读动态消息，有未读通知消息，取动态消息；
        String content="";
        String msgCreateTime="";
        if(dynamicMsg != null){
            //content=String.valueOf(dynamicMsg.get("message")==null?"":dynamicMsg.get("message"));
            content="您关注的好友有新的动态";
            msgCreateTime=String.valueOf(dynamicMsg.get("create_time"));
        }else if(dynamicMsg == null && systemMsg != null){
            //如果无未读动态消息，有未读通知消息，取通知消息；
            ///content=String.valueOf(systemMsg.get("content")==null?"":systemMsg.get("content"));
            content="您收到了一条健康圈通知";
            msgCreateTime=String.valueOf(systemMsg.get("create_time"));
        }
        Date date= DateUtils.parseString(msgCreateTime);
        String time=MessageCenterDto.parseDate(date);
        MessageCenterDto message = new MessageCenterDto();
        message.setTitle(MsgTypeEnum.msgType4.getTypeName());
        message.setContent(content);
        message.setTime(time);
        message.setRead(true);//是否有未读的消息
        message.setSort(1);
        messages.add(message);
        return messages;
    }
    //获取系统消息、咨询消息中最新的未读消息
    private List<MessageCenterDto> getLastSystem(String area, String uid){
        List<MessageCenterDto> messages = Lists.newLinkedList();
        List<UserPrivateMessage> results = messageService.findRoot(area, uid);
        //若没有新消息显示暂无新消息
        if(results == null){
            MessageCenterDto sysMsg = new MessageCenterDto();
            sysMsg.setTitle(AppMessageUrlUtil.Type.SYSTEM.name);
            sysMsg.setContent("暂无新消息");
            sysMsg.setSort(5);
            MessageCenterDto questionMsg = new MessageCenterDto();
            questionMsg.setTitle(AppMessageUrlUtil.Type.QUESTION.name);
            questionMsg.setContent("暂无新消息");
            questionMsg.setSort(4);
            messages.add(questionMsg);
            messages.add(sysMsg);
            return messages;
        }
        for (UserPrivateMessage result : results) {
            MessageCenterDto message = new MessageCenterDto();
            AppMessageUrlUtil.Type type = AppMessageUrlUtil.Type.getById(result.getType());
            if(result.getType().equals(AppMessageUrlUtil.Type.QUESTION.id)){
                message.setTitle(type.name);
                message.setContent(type.showTitleInRoot ? result.getTitle() : result.getContent());
                message.setTime(MessageCenterDto.parseDate(result.getCreateTime()));
                message.setRead(messageReadService.unreadCountByType(uid, result.getType()) == 0);
                message.setSort(4);
            }else if( result.getType().equals(AppMessageUrlUtil.Type.SYSTEM.id)){
                message.setTitle(type.name);
                message.setContent(type.showTitleInRoot ? result.getTitle() : result.getContent());
                message.setTime(MessageCenterDto.parseDate(result.getCreateTime()));
                message.setRead(messageReadService.unreadCountByType(uid, result.getType()) == 0);
                message.setSort(5);
            }
            messages.add(message);
        }
        return messages;
    }

    /**
     * 消息状态设置 <br/>
     * 系统消息、我的咨询、家庭消息列表中的消息，读一条红点即消失;其他的进入列表所有红点消失;
     */
    @Transactional
    public void setAsRead(String msgType,String msgID){
        MsgTypeEnum type= MsgTypeEnum.fromTypeCode(msgType);
        switch (type){
            case msgType0:
                messageReadService.setAsRead(messageService.findOne(msgID));
                break;
            case msgType1:
                messageReadService.setAsRead(messageService.findOne(msgID));
                break;
            case msgType2:
                familyMsgService.setRead(Lists.newArrayList(Integer.valueOf(msgID)));
                break;
            default:
        }
    }
}
