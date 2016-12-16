package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.appenum.DynamicMsgContentEnum;
import com.wondersgroup.healthcloud.common.appenum.DynamicMsgTypeEnum;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.services.bbs.BbsSysMsgService;
import com.wondersgroup.healthcloud.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 动态消息服务
 * Created by jialing.yao on 2016-8-12.
 */
@Service("dynamicMsgService")
public class DynamicMsgServiceImpl implements BbsSysMsgService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public Page queryMsgListByUid(String uid, Page page){
        int fansNum=this.countMsgByUid(uid);
        List<Map<String, Object>> list =this.getMsgListByUid(uid,page.getOffset(),page.getPageSize());
        page.setTotalCount(fansNum);
        page.setResult(list);
        return page;
    }

    /**
     * 根据UID统计动态消息数
     * @param uid
     * @return
     */
    @Override
    public int countMsgByUid(String uid) {
        String query =String.format("select count(1) from app_tb_register_info a,tb_bbs_dynamic_message c,tb_bbs_topic d" +
                " where a.registerid=d.uid and c.type_id=d.id" +
                " and c.type=0" +
                " and c.uid='%s'",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    /**
     * 根据UID查询动态消息列表，分页
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getMsgListByUid(String uid, int pageNo, int pageSize) {
        /*String query =String.format("select c.id as msgid,a.id as uid,a.nickName,a.avatar,b.birthday as baby_birthday," +
                "d.id,d.title,d.is_best as isBest,d.is_vote as isVote,c.create_time" +
                " from tb_account_user a,tb_baby_info b,tb_bbs_dynamic_message c,tb_bbs_topic d" +
                " where a.id=b.parent_id and a.id=d.uid and c.type_id=d.id" +
                " and c.type=0" +
                " and c.uid='%s'" +
                " order by c.create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);*/
        String query =String.format("select c.id as msgid,a.registerid as uid,a.nickname as nickName,a.headphoto as avatar," +
                "d.id,d.title,d.is_best as isBest,d.is_vote as isVote,c.create_time" +
                " from app_tb_register_info a,tb_bbs_dynamic_message c,tb_bbs_topic d" +
                " where a.registerid=d.uid and c.type_id=d.id" +
                " and c.type=0" +
                " and c.uid='%s'" +
                " order by c.create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return null;
        }else{
            //List<Integer> ids=new ArrayList<>();
            for(Map<String, Object> row:list){
                //处理msgtime
                String msgCreateTime=String.valueOf(row.get("create_time"));
                Date date= DateUtils.parseString(msgCreateTime);
                String msgtime = DateUtils.formatDate2Custom(date);
                row.put("lastCommentTime",msgtime);
                //MAP null值处理为""
                //MapChecker.checkMap(row);
            }
            //消息设为已读
            this.setAllRead(uid);
        }
        return list;
    }
    @Override
    public void setAllRead(String uid) {
        String sql=String.format("update tb_bbs_dynamic_message set is_read=1 where is_read=0 and uid='%s'",uid);
        jdbcTemplate.update(sql);
    }
    /*public void setRead(List<Integer> ids){
        Joiner joiner = Joiner.on(",").skipNulls();
        String sql=String.format("update tb_bbs_dynamic_message set is_read=1 where id in(%s)",joiner.join(ids));
        jdbcTemplate.update(sql);
    }*/

    @Override
    public Map<String, Object> findOneDynamicMessageByUid(String uid) {
        String query =String.format("select a.id,a.uid,a.type,a.is_read,a.type_id,a.create_time from tb_bbs_dynamic_message a" +
                " where a.uid='%s' and a.is_read=0  order by a.create_time desc limit 0,1",uid);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        Map<String, Object> data;
        if (null == list || list.isEmpty()){
            return null;
        }else{
            data=list.get(0);
            String msgType= String.valueOf(data.get("type"));
            //根据类型,设置文案
            DynamicMsgTypeEnum dme= DynamicMsgTypeEnum.fromValue(msgType);
            switch (dme){
                case msgType0:
                    data.put("message", DynamicMsgContentEnum.dynamic_msgType0.value());
                    break;
                default:
            }
        }
        return data;
    }

    @Override
    public Map<String, Object> findOneSysMessageByUid(String uid) {
        return null;
    }

    @Override
    public int countOfUnReadMessages(String uid) {
        String query =String.format("select count(1) from tb_bbs_dynamic_message where uid='%s' and is_read=0",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }
}
