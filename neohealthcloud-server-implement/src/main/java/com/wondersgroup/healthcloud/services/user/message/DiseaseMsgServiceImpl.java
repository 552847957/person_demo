package com.wondersgroup.healthcloud.services.user.message;

import com.google.common.base.Joiner;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.utils.MapChecker;
import com.wondersgroup.healthcloud.utils.Page;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 慢病消息
 * Created by jialing.yao on 2016-12-12.
 */
@Service("diseaseMsgService")
public class DiseaseMsgServiceImpl implements MsgService{

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
        String query =String.format("select count(1) from app_tb_disease_message where receiver_uid='%s' and del_flag='0' ",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public List<Map<String, Object>> getMsgListByUid(String uid, int pageNo, int pageSize) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,create_time" +
                " from app_tb_disease_message where receiver_uid='%s' and del_flag='0' " +
                " order by create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return null;
        }else{

            List<Integer> setReadIds=new ArrayList<>();
            for(Map<String, Object> row:list){
                //处理消息时间
                String msgCreateTime=String.valueOf(row.get("create_time"));
                Date date= DateUtils.parseString(msgCreateTime);
                //String msgtime=DateUtils.formatDate2Custom(date);
                String msgtime=DateUtils.convertMsgDate(date);
                row.put("time",msgtime);

                //处理是否已读状态
                String isReaded= String.valueOf(row.get("isReaded"));
                if(isReaded.equals("0")){//0:未读，1:已读
                    row.put("isReaded",false);
                }else if(isReaded.equals("1")){
                    row.put("isReaded",true);
                }
                //筛查消息没有红点、干预消息有红点
                String type= String.valueOf(row.get("type"));
                String id= String.valueOf(row.get("id"));
                if(type.equals("1")){//干预提醒0、筛查提醒1
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
        if (CollectionUtils.isEmpty(ids)){
            return;
        }
        Joiner joiner = Joiner.on(",").skipNulls();
        String sql=String.format("update app_tb_disease_message set is_read=1 where id in(%s)",joiner.join(ids));
        jdbcTemplate.update(sql);
    }
    @Override
    public Map<String, Object> findOneMessageByUid(String uid) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,create_time" +
                " from app_tb_disease_message where receiver_uid='%s' and is_read=0 and del_flag='0'" +
                " order by create_time desc" +
                " limit 0, 1",uid);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        Map<String, Object> data;
        if (null == list || list.isEmpty()){
            return null;
        }else {
            data = list.get(0);
            //MAP null值处理为""
            MapChecker.checkMap(data);
        }
        return data;
    }

    @Override
    public int countOfUnReadMessages(String uid) {
        String query =String.format("select count(1) from app_tb_disease_message where receiver_uid='%s' and del_flag='0' and is_read=0",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public void setAllRead(String uid) {

    }

    @Override
    public int getCountByDate(String uid,String memberId, int type) {

        return 0;
    }

    /**
     * 根据用户id和消息类型查询消息
     * @param uid
     * @param typeCode
     * @return
     */
    @Override
    public int countMsgByUidAndType(String uid, String typeCode) {
        String query =String.format("select count(1) from app_tb_disease_message where receiver_uid='%s' and msg_type='%s' and del_flag =0 ",uid,typeCode);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public int countOfUnReadMessagesByUidType(String uid, String typeCode) {
        String query =String.format("select count(1) from app_tb_disease_message where receiver_uid='%s' and msg_type='%s' and is_read=0 and del_flag=0 ",uid,typeCode);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public Map<String, Object> findLastMessageByUidType(String uid, String typeCode) {
        String query =String.format("select id,notifier_uid as notifierUID,receiver_uid as receiverUID,msg_type as type,is_read as isReaded,title,content,jump_url as jumpUrl,create_time" +
                " from app_tb_disease_message where receiver_uid='%s' and del_flag=0 " +
                " order by create_time desc" +
                " limit 0, 1",uid);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        Map<String, Object> data;
        if (null == list || list.isEmpty()){
            return null;
        }else {
            data = list.get(0);
            //MAP null值处理为""
            MapChecker.checkMap(data);
        }
        return data;
    }
}
