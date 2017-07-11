package com.wondersgroup.healthcloud.services.user.message;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.helper.push.api.AppMessage;
import com.wondersgroup.healthcloud.jpa.entity.user.UserPrivateMessage;
import com.wondersgroup.healthcloud.jpa.repository.user.UserPrivateMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
    private MessageReadService messageReadService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserPrivateMessage findOne(String id) {
        UserPrivateMessage message = messageRepository.findOne(id);
        if (message == null) {
            throw new RuntimeException("message not exist " + id);
        }
        return message;
    }

    @Override
    public void saveOneMessage(AppMessage message, String uid) {
        if (message.persistence) {
            if (!message.isDoctor) {
                UserPrivateMessage userPrivateMessage = new UserPrivateMessage();
                userPrivateMessage.setId(message.id == null ? IdGen.uuid() : message.id);
                userPrivateMessage.setUid(uid);
                userPrivateMessage.setTitle(message.title);
                userPrivateMessage.setContent(message.content);
                if (message.areaSpecial) {
                    userPrivateMessage.setArea(message.area);
                }
                userPrivateMessage.setUrl(message.urlFragment);
                userPrivateMessage.setType(message.type.id);
                userPrivateMessage.setCreateTime(new Date());

                userPrivateMessage = messageRepository.save(userPrivateMessage);

                messageReadService.newMessage(userPrivateMessage);
            }
        }
    }

    @Override
    public List<UserPrivateMessage> findRoot(String area, String uid) {
        return messageRepository.findRootMessages(area, uid);
    }

    @Override
    public List<UserPrivateMessage> findType(String area, String uid, String typeId, Long flag) {
        if (flag == null) {
            flag = System.currentTimeMillis() / 1000L;
        }
        return messageRepository.findTypeMessages(area, uid, typeId, flag);
    }

    @Override
    public UserPrivateMessage findLastQuestionMsgByUid(String area, String uid,String type) {
        return messageRepository.findLastQuestionMsgByUid(area,uid,type);
    }

    /**
     * app_tb_user_private_message表type为0的数据和 app_tb_disease_message表type为3的数据
     * @param area
     * @param uid
     * @return
     */
    @Override
    public UserPrivateMessage findLastSysMsgByUid(String area, String uid) {
        String sql = " select a.id,a.uid,a.type,a.title,a.content,a.url ,a.create_time  " +
                " from app_tb_user_private_message a " +
                " where a.uid = '%s' and a.type = '0' and (main_area is null or main_area='%s') and a.del_flag = '0' " +
                "union all " +
                " select a.id,a.receiver_uid as uid,a.msg_type as type,a.title,a.content,a.jump_url as url,a.create_time " +
                " from app_tb_disease_message a\n" +
                " where a.receiver_uid = '%s' and a.msg_type = '3' and a.del_flag = '0' " +
                "order by create_time desc  limit 1";
        sql = String.format(sql,uid,area,uid);
        List<UserPrivateMessage> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper(UserPrivateMessage.class));
        if(list!=null && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<UserPrivateMessage> findSystemMsgList(String area, String uid,String type, int pageNo, int pageSize) {
        String sql = "";
        //系统消息
        if("0".equals(type)){
             sql = " select a.id,a.uid,a.type,a.title,a.content,a.url ,a.create_time,null as xtIsRead  " +
                    " from app_tb_user_private_message a " +
                    " where a.uid = '%s' and a.type = '0' and (main_area is null or main_area='%s') and a.del_flag = '0' " +
                    "union all " +
                    " select a.id,a.receiver_uid as uid,a.msg_type as type,a.title,a.content,a.jump_url as url,a.create_time," +
                     " a.is_read as xtIsRead " +
                    " from app_tb_disease_message a\n" +
                    " where a.receiver_uid = '%s' and a.msg_type = '3' and a.del_flag = '0' " +
                    " order by create_time desc " +
                    " limit "+(pageNo)*pageSize+","+(pageSize+1);
            sql = String.format(sql,uid,area,uid);
        //我的咨询
        }else if("1".equals(type)){
             sql = " select a.id,a.uid,a.type,a.title,a.content,a.url ,a.create_time  " +
                    " from app_tb_user_private_message a " +
                    " where a.uid = '%s' and a.type = '1' and (main_area is null or main_area='%s') and a.del_flag = '0' " +
                    " order by create_time desc " +
                    " limit "+(pageNo)*pageSize+","+(pageSize+1);
            sql = String.format(sql,uid,area);
        }
        //todo 空数据会报错
        List<UserPrivateMessage> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper(UserPrivateMessage.class));
        return list;
    }

    @Override
    public void deleteMsg(String type, String msgID) {
        messageRepository.deleteMsg(type,msgID);

    }

    @Override
    public void deleteAllMsg(String uid, String type) {
        messageRepository.deleteAllMsg(uid, type);
    }


}
