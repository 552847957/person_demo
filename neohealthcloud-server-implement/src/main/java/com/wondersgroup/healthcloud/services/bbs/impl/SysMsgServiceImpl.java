package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.services.bbs.BbsSysMsgService;
import com.wondersgroup.healthcloud.utils.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统消息服务
 * Created by jialing.yao on 2016-8-12.
 */
@Service("sysMsgService")
public class SysMsgServiceImpl implements BbsSysMsgService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Page queryMsgListByUid(String uid, Page page){
        int num=this.countMsgByUid(uid);
        List<Map<String, Object>> list =this.getMsgListByUid(uid,page.getOffset(),page.getPageSize());
        page.setTotalCount(num);
        page.setResult(list);
        return page;
    }

    /**
     * 根据UID统计系统消息数
     * @param uid
     * @return
     */
    @Override
    public int countMsgByUid(String uid) {
        String query =String.format("select count(1) from tb_bbs_system_message where uid='%s'",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    /**
     * 根据UID查询系统消息列表，分页
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getMsgListByUid(String uid, int pageNo, int pageSize) {
        String query =String.format("select id as msgid,uid,type as msgtype,content,jump_url,create_time " +
                " from tb_bbs_system_message where uid='%s'" +
                " order by create_time desc" +
                " limit %s, %s",uid,pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()){
            return null;
        }else{
            List<Integer> ids=new ArrayList<>();
            //处理msgtime
            for(Map<String, Object> row:list){
                String msgCreateTime=String.valueOf(row.get("create_time"));
                Date date= DateUtils.parseString(msgCreateTime);
                String msgtime=DateUtils.formatDate2Custom(date);
                row.put("msgtime",msgtime);
                //MAP null值处理为""
                //MapChecker.checkMap(row);
            }
            //根据类型,设置文案
            for(Map<String, Object> row:list){
                String content= String.valueOf(row.get("content"));
                String jump_url= String.valueOf(row.get("jump_url"));
                if (StringUtils.isNotEmpty(jump_url)){
                    row.put("content", content+",点击查看");
                }
            }
            //消息设为已读
            this.setAllRead(uid);
        }
        return list;
    }
    @Override
    public void setAllRead(String uid) {
        String sql = String.format("update tb_bbs_system_message set is_read=1 where is_read=0 and uid='%s'",uid);
        jdbcTemplate.update(sql);
    }

    @Override
    public Map<String, Object> findOneDynamicMessageByUid(String uid) {
        return null;
    }

    @Override
    public Map<String, Object> findOneSysMessageByUid(String uid) {
        String query =String.format("select a.id,a.uid,a.type,a.is_read,a.content,a.create_time from tb_bbs_system_message a" +
                " where a.uid='%s' and a.is_read=0  order by a.create_time desc limit 0,1",uid);
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
        String query =String.format("select count(1) from tb_bbs_system_message where uid='%s' and is_read=0",uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

}
