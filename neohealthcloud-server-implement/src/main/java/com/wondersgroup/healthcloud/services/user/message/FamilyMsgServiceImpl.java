package com.wondersgroup.healthcloud.services.user.message;

import com.google.common.base.Joiner;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.utils.Page;
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
    public Page queryMsgListByUid(String uid, Page page) {
        int num=this.countMsgByUid(uid);
        List<Map<String, Object>> list =this.getMsgListByUid(uid,page.getOffset(),page.getPageSize());
        page.setTotalCount(num);
        page.setResult(list);
        return page;
    }

    @Override
    public int countMsgByUid(String uid) {
        String query =String.format("select count(1) from app_tb_family_message where receiver_uid='%s'",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public List<Map<String, Object>> getMsgListByUid(String uid, int pageNo, int pageSize) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,req_record_id as reqRecordID,create_time" +
                " from app_tb_family_message where receiver_uid='%s'" +
                " order by create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return new ArrayList<>();
        }else{

            List<Integer> setReadIds=new ArrayList<>();
            for(Map<String, Object> row:list){
                //处理消息时间
                String msgCreateTime=String.valueOf(row.get("create_time"));
                Date date= DateUtils.parseString(msgCreateTime);
                String msgtime=DateUtils.formatDate2Custom(date);
                row.put("time",msgtime);

                //如果msg_type=0, 获取邀请状态
                String type= String.valueOf(row.get("type"));
                String reqRecordID= String.valueOf(row.get("reqRecordID"));
                if(type.equals("0")){
                    String reqStatus=this.getReqStatusByReqID(reqRecordID);
                    row.put("reqStatus",reqStatus);
                }

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
            }
            //消息设为已读
            this.setRead(setReadIds);
        }
        return list;
    }
    @Override
    public void setRead(List<Integer> ids){
        Joiner joiner = Joiner.on(",").skipNulls();
        String sql=String.format("update app_tb_family_message set is_read=1 where id in(%s)",joiner.join(ids));
        jdbcTemplate.update(sql);
    }
    //根据邀请记录ID，获取邀请状态
    public String getReqStatusByReqID(String reqID) {
        String query =String.format("select status from app_tb_family_member_invitation where id='%s' and del_flag=0",reqID);
        String reqStatus = jdbcTemplate.queryForObject(query, String.class);
        return reqStatus != null ? reqStatus : "";
    }

    @Override
    public Map<String, Object> findOneMessageByUid(String uid) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,req_record_id as reqRecordID,create_time" +
                " from app_tb_family_message where receiver_uid='%s'" +
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
        String query =String.format("select count(1) from app_tb_family_message where receiver_uid='%s' and is_read=0",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public void setAllRead(String uid) {

    }
}
