package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.entity.bbs.UserFans;
import com.wondersgroup.healthcloud.jpa.repository.bbs.UserFansRepository;
import com.wondersgroup.healthcloud.services.bbs.UserFansService;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBbsInfo;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 粉丝
 * Created by jialing.yao on 2016-8-12.
 */
@Service("fansService")
public class UserFansServiceImpl implements UserFansService {

    private static final Logger logger = LoggerFactory.getLogger("FansServiceImpl");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserFansRepository fansRepository;

    @Override
    public UserFans queryByUidAndFansUid(String uid, String fansUid) {
        return fansRepository.queryByUidAndFansUid(uid, fansUid);
    }

    @Override
    public List<UserBbsInfo> getAttentUsers(String uid, int page, int pageSize){
        String sql = "select user.registerid as uid,user.headphoto as avatar,user.nickname,user.is_bbs_admin<>0 as isBBsAdmin, " +
                " user.ban_status as banStatus, user.gender,user.birthday,user.del_flag as delFlag " +
                " from tb_bbs_fans fans " +
                " left join app_tb_register_info user on fans.uid=user.registerid " +
                " where user.del_flag='0' and fans.fans_uid=? " +
                " ORDER BY fans.update_time desc limit ?,?";
        Object[] parms = new Object[]{uid, (page-1)*pageSize, pageSize};
        List<UserBbsInfo> list = jdbcTemplate.query(sql, parms, new BeanPropertyRowMapper<>(UserBbsInfo.class));
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public List<UserBbsInfo> getFansUsers(String uid, int page, int pageSize) {
        String sql = "select user.registerid as uid,user.headphoto as avatar,user.nickname,user.is_bbs_admin<>0 as isBBsAdmin, " +
                " user.ban_status as banStatus, user.gender,user.birthday,user.del_flag as delFlag " +
                " from tb_bbs_fans fans " +
                " left join app_tb_register_info user on fans.fans_uid=user.registerid " +
                " where user.del_flag='0' and fans.uid=? " +
                " ORDER BY fans.update_time desc limit ?,?";
        Object[] parms = new Object[]{uid, (page-1)*pageSize, pageSize};
        List<UserBbsInfo> list = jdbcTemplate.query(sql, parms, new BeanPropertyRowMapper<>(UserBbsInfo.class));
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public List<String> filterMyAttentUser(String myUid, List<String> filterUids) {
        return fansRepository.filterMyAttentUser(myUid, filterUids);
    }

    @Override
    public List<String> filterMyFans(String myUid, List<String> filterUids) {
        return fansRepository.filterMyFans(myUid, filterUids);
    }

    @Override
    public Map<String, Integer> getMyAttentStatus(String myUid, List<String> targetUids) {
        List<String> myFans = this.filterMyFans(myUid, targetUids);
        List<String> myAttent = this.filterMyAttentUser(myUid, targetUids);
        Map<String, Integer> info = new HashMap<>();
        for (String uid : targetUids){
            int attentStatus = 0;
            if (myAttent.contains(uid)){
                attentStatus = myFans.contains(uid) ? 2 : 1;
            }
            info.put(uid, attentStatus);
        }
        return info;
    }

    @Override
    public Integer getMyAttentStatus(String myUid, String targetUid) {
        int attentStatus = 0;
        if (isAttent(myUid, targetUid)){
            attentStatus = isFans(myUid, targetUid) ? 2 : 1;
        }
        return attentStatus;
    }

    @Override
    public int countAttentNum(String uid) {
        return fansRepository.countAttentNumForUser(uid);
    }

    @Override
    public int countFansNum(String uid) {
        return fansRepository.countFansNumForUser(uid);
    }
    @Override
    public Boolean isAttent(String uid, String targetUid) {
        String query = String.format("select count(1) from tb_bbs_fans where uid='%s' and fans_uid='%s' and del_flag='0'", targetUid, uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null && num > 0;
    }
    @Override
    public Boolean isFans(String uid, String targetUid) {
        String query = String.format("select count(1) from tb_bbs_fans where uid='%s' and fans_uid='%s' and del_flag='0'", targetUid, uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null && num > 0;
    }

    @Override
    public boolean isAttentEachOther(String uid, String targetUid) {
        return isAttent(uid, targetUid) && isAttent(targetUid, uid);
    }


    @Override
    public UserFans saveFans(UserFans fans) {
        UserFans result = fansRepository.saveAndFlush(fans);
        if (fans.getDelFlag().equals("0")) {
//            BbsMsgHandler.addAttent(fans.getUid(), fans.getFansUid());
        }
        return result;
    }

}
