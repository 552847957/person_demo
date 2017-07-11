package com.wondersgroup.healthcloud.services.user.message;

import com.google.common.base.Joiner;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.services.user.message.enums.FamilyMsgTypeEnum;
import com.wondersgroup.healthcloud.utils.MapChecker;
import com.wondersgroup.healthcloud.utils.Page;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 家庭消息
 * Created by jialing.yao on 2016-12-12.
 */
@Service("familyMsgService")
public class FamilyMsgServiceImpl implements MsgService{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Page queryMsgListByUid(String uid, Page page,Boolean isSetRead) {
        int num=this.countMsgByUid(uid);
        List<Map<String, Object>> list =this.getMsgListByUid(uid,page.getOffset(),page.getPageSize(),isSetRead);
        page.setTotalCount(num);
        page.setResult(list);
        return page;
    }

    @Override
    public int countMsgByUid(String uid) {
        String query =String.format("select count(1) from app_tb_family_message where receiver_uid='%s' and del_flag='0' ",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public List<Map<String, Object>> getMsgListByUid(String uid, int pageNo, int pageSize,Boolean isSetRead) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,req_record_id as reqRecordID,'' as avatar,create_time" +
                " from app_tb_family_message where receiver_uid='%s' and del_flag='0' " +
                " order by create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return new ArrayList<>();
        }else{
            List<Integer> setReadIds=new ArrayList<>();//需设置为已读的消息ID
            List<String> notifierUids=new ArrayList<>();//需查询头像用的消息通知人UID
            for(Map<String, Object> row:list){
                //处理消息时间
                String msgCreateTime=String.valueOf(row.get("create_time"));
                Date date= DateUtils.parseString(msgCreateTime);
                //String msgtime = DateUtils.formatDate2Custom(date);
                String msgtime=DateUtils.convertMsgDate(date);
                row.put("time",msgtime);

                //如果msg_type=0, 获取邀请状态
                String type= String.valueOf(row.get("type"));
                String reqRecordID= String.valueOf(row.get("reqRecordID"));
                if(type.equals("0")){
                    String reqStatus=this.getReqStatusByReqID(reqRecordID);
                    row.put("reqStatus",reqStatus);
                }
                //设置title和content文案(这里以后可做优化，减少读库次数)
                this.setTitleAndContent(row,type);

                //处理是否已读状态
                String isReaded= String.valueOf(row.get("isReaded"));
                if(isReaded.equals("0")){//0:未读，1:已读
                    row.put("isReaded",false);
                }else if(isReaded.equals("1")){
                    row.put("isReaded",true);
                }
                //msg_type为0、1、2、3的消息，处理为已读
                String id= String.valueOf(row.get("id"));
                if(type.equals("0")
                        || type.equals("1")
                        || type.equals("2")
                        || type.equals("3")){
                    row.put("isReaded",true);
                    setReadIds.add(Integer.valueOf(id));
                }
                String notifierUID=String.valueOf(row.get("notifierUID"));
                notifierUids.add(notifierUID);
                //MAP null值处理为""
                MapChecker.checkMap(row);
            }
            //消息设为已读
            if(isSetRead){//兼容老版本 新版本是点击一下红点消失 老版本是拉取一个列表红点消失
                this.setRead(setReadIds);
            }
            //补全头像字段
            List<Map<String, Object>> avatarList=this.getAvatarByUids(notifierUids);
            if(avatarList != null){
                for(Map<String, Object> avatarData:avatarList){
                    String notifierUID=String.valueOf(avatarData.get("uid"));
                    String headphoto=avatarData.get("headphoto")==null?"":String.valueOf(avatarData.get("headphoto"));
                    for(Map<String, Object> row:list){
                        String target_notifierUID=String.valueOf(row.get("notifierUID"));
                        if(target_notifierUID.equals(notifierUID)){
                            row.put("avatar", StringUtils.defaultIfBlank(headphoto,""));
                        }
                    }
                }
            }
        }
        return list;
    }
    //设置title和content文案
    private void setTitleAndContent(Map<String, Object> row,String msgType){
        String notifierUID=String.valueOf(row.get("notifierUID"));
        String receiverUID=String.valueOf(row.get("receiverUID"));
        String _title=String.valueOf(row.get("title"));
        String _content=String.valueOf(row.get("content"));
        FamilyMsgTypeEnum msgEnum=FamilyMsgTypeEnum.fromTypeCode(msgType);
        switch (msgEnum) {
            case msgType0:
                String title;
                String content;
                String reqRecordID= String.valueOf(row.get("reqRecordID"));
                //如果邀请人和被邀请人UID一样，则为申请消息,title显示为关系，否则显示为通知人昵称;
                //content显示为"等待对方通过家庭成员申请"，否则显示为"请求添加你为家人";
                if(notifierUID.equals(receiverUID)){
                    title=String.format(_title,this.getRelationNameByReqID(reqRecordID));
                    content=_content.split("\\|")[0];
                }else {
                    title=String.format(_title,this.getNickNameByUID(notifierUID));
                    content=_content.split("\\|")[1];
                }
                row.put("title", title);
                row.put("content", content);
                break;
            default:
                row.put("title", String.format(_title,getNickNameByUID(notifierUID)));
        }
    }
    //根据邀请记录ID，获取邀请人关系
    private String getRelationNameByReqID(String reqID) {
        String query =String.format("select relation_name from app_tb_family_member_invitation where id='%s' and del_flag=0",reqID);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return "";
        }
        Object relationName= list.get(0).get("relation_name");
        return relationName ==null?"":String.valueOf(relationName);
    }

    //根据uid，获取昵称
    private String getNickNameByUID(String uid) {
        String query =String.format("select nickname from app_tb_register_info where registerid='%s' and del_flag=0",uid);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return "";
        }
        Object nickname= list.get(0).get("nickname");
        return nickname ==null?"":String.valueOf(nickname);
    }

    @Override
    public void setRead(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return;
        }
        Joiner joiner = Joiner.on(",").skipNulls();
        String sql=String.format("update app_tb_family_message set is_read=1 where  del_flag='0'  and id in(%s)",joiner.join(ids));
        jdbcTemplate.update(sql);
    }
    //根据邀请记录ID，获取邀请状态
    private String getReqStatusByReqID(String reqID) {
        String query =String.format("select status from app_tb_family_member_invitation where id='%s' and del_flag=0",reqID);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return "";
        }
        Object status= list.get(0).get("status");
        return status ==null?"":String.valueOf(status);
    }
    //批量获取请求人头像
    private List<Map<String, Object>> getAvatarByUids(List<String> ids){
        Joiner joiner = Joiner.on("','").skipNulls();
        String query =String.format("SELECT registerid as uid,headphoto FROM app_tb_register_info WHERE registerid in ('%s') AND del_flag=0",joiner.join(ids));
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return null;
        }
        return list;
    }

    @Override
    public Map<String, Object> findOneMessageByUid(String uid) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,req_record_id as reqRecordID,create_time" +
                " from app_tb_family_message where receiver_uid='%s' and is_read=0 and del_flag='0'  " +
                " order by create_time desc" +
                " limit 0, 1",uid);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        Map<String, Object> data;
        if (null == list || list.isEmpty()){
            return null;
        }else {
            data = list.get(0);
        }
        return data;
    }

    @Override
    public int countOfUnReadMessages(String uid) {
        String query =String.format("select count(1) from app_tb_family_message where receiver_uid='%s' and is_read=0 and del_flag='0' ",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public void setAllRead(String uid) {

    }

    @Override
    public int getCountByDate(String uid, String memberId, int type) {
        String query = "select count(*) from app_tb_family_message where notifier_uid = '" + uid + "' and receiver_uid = '" + memberId + "' and msg_type = " + type + " and del_flag='0'  and DATE_FORMAT(create_time,'%Y-%c-%d') = DATE_FORMAT(now(),'%Y-%c-%d')";
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public int countMsgByUidAndType(String uid, String typeCode) {
        return 0;
    }

    @Override
    public int countOfUnReadMessagesByUidType(String uid, String typeCode) {
        return 0;
    }

    @Override
    public Map<String, Object> findLastMessageByUidType(String uid, String typeCode) {
        return null;
    }

    @Override
    public void deleteMsg(String typeCode, String msgID) {
        if (StringUtils.isBlank(msgID)){
            return;
        }
        String sql=String.format("update app_tb_family_message set del_flag=1,is_read=1 where  id = '%s' ",msgID);
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteAllMsg(String uid, String typeCode) {
        if (StringUtils.isBlank(uid) ){
            return;
        }
        String sql=String.format("update app_tb_family_message set del_flag=1,is_read=1 where  receiver_uid = '%s' and (del_flag=0 or is_read=0)",uid);
        jdbcTemplate.update(sql);
    }

    @Override
    public Page queryMsgListByUidType(String uid, Page page, String msgType) {
        return null;
    }
}
