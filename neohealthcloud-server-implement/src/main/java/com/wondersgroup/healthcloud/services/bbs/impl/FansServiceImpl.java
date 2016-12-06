package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Fans;
import com.wondersgroup.healthcloud.jpa.repository.bbs.FansRepository;
import com.wondersgroup.healthcloud.services.bbs.FansService;
import com.wondersgroup.healthcloud.services.bbs.dto.AttentDto;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.utils.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 粉丝
 * Created by jialing.yao on 2016-8-12.
 */
@Service("fansService")
public class FansServiceImpl implements FansService {

    private static final Logger logger = LoggerFactory.getLogger("FansServiceImpl");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private FansRepository fansRepository;

    @Override
    public Page queryAttentListByUid(String uid, Page page) {
        int count = fansRepository.getAttentCount(uid);
        List<AttentDto> list = this.getAttentListByUid(uid, page.getOffset(), page.getPageSize());
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }

    /**
     * 获取uid关注了多少人
     */
    public int countAttentByUid(String uid) {
        String query = String.format("select count(1) as fansNum from tb_bbs_fans where fans_uid='%s' and del_flag='0'", uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    @Override
    public Boolean isAttentUser(String uid, String targetUid) {
        String query = String.format("select count(1) as fansNum from tb_bbs_fans where uid='%s' and fans_uid='%s' and del_flag='0'", targetUid, uid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null && num > 0;
    }

    @Override
    public List<AttentDto> getAttentListByUid(String uid, int pageNo, int pageSize) {
        String query = String.format("select c.fans_uid, " +
                "a.id as attent_uid,a.nickname as attent_nickname,a.avatar as attent_avatar,a.gender as attent_gender," +
                "b.id as baby_uid,b.baby_name,b.birthday as baby_birthday,b.gender as baby_gender," +
                //"if(month(now())-month(b.birthday)>=0, " +
                //"concat('宝宝',year(now())-year(b.birthday),'岁',month(now())-month(b.birthday),'个月')," +
                //"concat('宝宝',year(now())-year(b.birthday)-1,'岁',month(now())-month(b.birthday)+12,'个月')) as baby_age," +
                "1 as is_attention" +
                " from tb_account_user a,tb_baby_info b, tb_bbs_fans c" +
                " where a.id=b.parent_id and c.uid=a.id" +
                " and c.fans_uid='%s'" +
                " and a.del_flag=0 and b.del_flag=0 and c.del_flag=0 order by c.create_time ASC limit %s, %s", uid, pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()) {
            return null;
        }
        List<AttentDto> dtoList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String babyBirthday = map.get("baby_birthday").toString();

            AttentDto dto = new AttentDto();
            dto.setFansUid(String.valueOf(map.get("fans_uid")));
            // String.valueOf(map.get(""))
            dto.setAttentUid(String.valueOf(map.get("attent_uid")));
            dto.setAttentNickname(String.valueOf(map.get("attent_nickname")));
            dto.setAttentAvatar(String.valueOf(map.get("attent_avatar")));
            dto.setAttentGender(String.valueOf(map.get("attent_gender")));
            dto.setBabyUid(String.valueOf(map.get("baby_uid")));
            dto.setBabyName(String.valueOf(map.get("baby_name")));
            dto.setBabyBirthday(String.valueOf(map.get("baby_birthday")));
            dto.setBabyGender(String.valueOf(map.get("baby_gender")));
            dto.setIsAttention(Integer.valueOf(map.get("is_attention").toString()));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public Fans queryByUIdAndFansUid(String uId, String fansUid) {
        Fans fans = fansRepository.queryByUIdAndFansUid(uId, fansUid);
        return fans;
    }

    @Override
    public Fans queryByUIdAndFansUidAndDelFlag(String uId, String fansUid, String delFlag) {
        Fans fans = fansRepository.queryByUIdAndFansUidAndDelFlag(uId, fansUid, delFlag);
        return fans;
    }

    @Override
    public Fans saveFans(Fans fans) {
        Fans result = fansRepository.saveAndFlush(fans);
        try {
            if (fans.getDelFlag().equals("0")) {
                BbsMsgHandler.addAttent(fans.getUId(), fans.getFansUid());
            }
        } catch (Exception e) {
            logger.error("添加关注后消息通知出错");
        }
        return result;
    }

    //===========================查顶层粉丝列表========================
    @Override
    public Page queryFansListByTopUid(String topUid, Page page) {
        int fansNum = this.countFansByTopUid(topUid);
        List<Map<String, Object>> list = this.getFansListByTopUid(topUid, page.getOffset(), page.getPageSize());
        page.setTotalCount(fansNum);
        page.setResult(list);
        return page;
    }

    // 查看顶层关注列表
    @Override
    public Page queryAttentListByTopUid(String topUid, Page page) {
        int fansNum = fansRepository.getAttentCount(topUid);
        List<Map<String, Object>> list = this.getAttentListByTopUid(topUid, page.getOffset(), page.getPageSize());
        page.setTotalCount(fansNum);
        page.setResult(list);
        return page;
    }

    /**
     * 根据当前登录UID统计粉丝数
     *
     * @param topUid
     * @return
     */
    @Override
    public int countFansByTopUid(String topUid) {
        String query = String.format("select count(1) as fansNum from tb_bbs_fans where uid='%s' and del_flag='0' ", topUid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    /**
     * 根据当前登录UID查询粉丝列表，分页
     *
     * @param topUid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getFansListByTopUid(String topUid, int pageNo, int pageSize) {
        String query = String.format("select " +
                "a.id as otherId,a.nickname as fans_nickname,a.avatar as fans_avatar,a.gender as fans_gender," +
                "b.id as baby_uid,b.baby_name,b.birthday as baby_birthday,b.gender as baby_gender," +
                "if((select count(1) from tb_bbs_fans a where uid=c.fans_uid and fans_uid='%s' and del_flag=0) >0,1,0) as is_attention, " +
                " case when exists (select uid from tb_bbs_fans f1 where f1.uid = '%s' and f1.fans_uid = a.id and f1.del_flag = 0) and " +
                " exists (select uid from tb_bbs_fans f2 where f2.fans_uid = '%s' and f2.uid = a.id and f2.del_flag = 0)" +
                " then 1 else 0 end as attentStatus" + // 如果是互相关注，返回1，否则返回0
                " from tb_account_user a,tb_baby_info b, tb_bbs_fans c" +
                " where a.id=b.parent_id and c.fans_uid=a.id" +
                " and c.uid='%s'" +
                " and a.del_flag=0 and b.del_flag=0 and c.del_flag=0 " +
                " order by c.create_time desc" +
                " limit %s, %s", topUid, topUid, topUid, topUid, pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     * 登录用户的关注列表c.fans_uid as uid,c.fans_uid='%s'
     *
     * @param topUid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getAttentListByTopUid(String topUid, int pageNo, int pageSize) {
        String query = String.format("select c.uid as otherId, " +
                "a.nickname as fans_nickname,a.avatar as fans_avatar,a.gender as fans_gender," +
                "b.id as baby_uid,b.baby_name,b.birthday as baby_birthday,b.gender as baby_gender," +
                " 1 as is_attention, " +
                " case when EXISTS (select uid from tb_bbs_fans f where f.uid = '%s' and f.fans_uid = c.uid and f.del_flag = 0) then '1' else '0' end as attentStatus" + // 如果是互相关注，返回1，否则返回0
                " from tb_account_user a,tb_baby_info b, tb_bbs_fans c" +
                " where a.id=b.parent_id and c.uid=a.id" +
                " and c.fans_uid='%s'" +
                " and a.del_flag=0 and b.del_flag=0 and c.del_flag=0 " +
                " order by c.create_time desc" +
                " limit %s, %s", topUid, topUid, pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }

    //===========================查点击某个粉丝的粉丝列表========================
    @Override
    public Page queryFansListByClickUid(String topUid, String clickUid, Page page) {
        int fansNum = this.countFansByClickUid(topUid, clickUid);
        List<Map<String, Object>> list = this.getFansListByClickUid(topUid, clickUid, page.getOffset(), page.getPageSize());
        page.setTotalCount(fansNum);
        page.setResult(list);
        return page;
    }

    /**
     * 根据点击的粉丝clickUid，统计clickUid下的粉丝数
     *
     * @param topUid
     * @param clickUid
     * @return
     */
    @Override
    public int countFansByClickUid(String topUid, String clickUid) {
        String query = String.format("select count(1) " +
                "from(" +
                " select d1.uid as top_uid,d2.uid as click_uid,d2.fans_uid as click_fans_uid,d2.create_time,if(d1.uid <>'',1,0) as is_attention " +
                " from (" +
                "   select * from  tb_bbs_fans where fans_uid='%s' and del_flag=0" +
                " ) d1 right join (" +
                "   select * from  tb_bbs_fans where uid='%s' and del_flag=0" +
                " ) d2 on d1.uid=d2.fans_uid" +
                ") c,tb_account_user a,tb_baby_info b" +
                " where a.id=b.parent_id and c.click_fans_uid=a.id", topUid, clickUid);
        Integer num = jdbcTemplate.queryForObject(query, Integer.class);
        return num != null ? num : 0;
    }

    /**
     * 根据点击的粉丝clickUid，查询clickUid的粉丝列表，分页
     *
     * @param topUid
     * @param clickUid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getFansListByClickUid(String topUid, String clickUid, int pageNo, int pageSize) {
        String query = String.format("select " +
                "a.id as otherId,a.nickname as fans_nickname,a.avatar as fans_avatar,a.gender as fans_gender," +
                "b.id as baby_uid,b.baby_name,b.birthday as baby_birthday,b.gender as baby_gender," +
                "c.is_attention, " +
                " case when (c.is_attention = 0) then 0 else case when exists( select uid from tb_bbs_fans f where f.fans_uid = a.id and f.uid = '%s' and f.del_flag = 0) then 1 else 0 end end as attentStatus " +// 如果是互相关注，返回1，否则返回0
                "from(" +
                " select d1.uid as top_uid,d2.uid as click_uid,d2.fans_uid as click_fans_uid,d2.create_time, if(d1.uid <>'',1,0) as is_attention" +
                " from (" +
                "   select * from  tb_bbs_fans where fans_uid='%s' and del_flag=0" +
                " ) d1 right join (" +
                "   select * from  tb_bbs_fans where uid='%s' and del_flag=0" +
                " ) d2 on d1.uid=d2.fans_uid" +
                ") c,tb_account_user a,tb_baby_info b" +
                " where a.id=b.parent_id and c.click_fans_uid=a.id" +
                " order by c.create_time desc" +
                " limit %s, %s", topUid, topUid, clickUid, pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(query);
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }

    @Override
    public Page queryAttentListByClickUid(String topUid, String clickUid, Page page) {
        int fansNum = fansRepository.getAttentCountByUidAndClickUid(topUid, clickUid);
        List<Map<String, Object>> list = this.getAttentListByClickUid(topUid, clickUid, page.getOffset(), page.getPageSize());
        page.setTotalCount(fansNum);
        page.setResult(list);
        return page;
    }

    /**
     * 点击用户的关注列表
     *
     * @param topUid
     * @param clickUid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> getAttentListByClickUid(String topUid, String clickUid, int pageNo, int pageSize) {
        String querySql = String.format("SELECT a.id AS otherId, a.nickname AS fans_nickname, a.avatar AS fans_avatar, " +
                " a.gender AS fans_gender, b.id AS baby_uid, b.baby_name, b.birthday AS baby_birthday, b.gender AS baby_gender," +
                " c.is_attention, " +
                " case when (c.is_attention = 0) then 0 else case when exists( select uid from tb_bbs_fans f where f.fans_uid = a.id and f.uid = '%s' and f.del_flag = 0) then 1 else 0 end end as attentStatus " + // 如果是互相关注，返回1，否则返回0
                " FROM (" +
                " SELECT d1.fans_uid AS top_uid, d2.fans_uid AS click_uid, d2.uid AS click_attent_uid, d2.create_time,IF (d1.uid <> '', 1, 0) AS is_attention" +
                " FROM ( SELECT * FROM tb_bbs_fans WHERE fans_uid = '%s' AND del_flag = 0) d1" +
                " RIGHT JOIN ( SELECT * FROM tb_bbs_fans WHERE fans_uid = '%s' AND del_flag = 0) d2" +
                " ON d1.uid = d2.uid" +
                " ) c,tb_account_user a,tb_baby_info b" +
                " WHERE a.id = b.parent_id AND c.click_attent_uid = a.id" +
                " ORDER BY c.create_time DESC" +
                " limit %s, %s", topUid, topUid, clickUid, pageNo, pageSize);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql);
        if (null == list || list.isEmpty()) {
            return null;
        }
        return list;
    }


    @Override
    public boolean ifAttentEachOther(String otherId, String uId) {
        boolean ifOtherAttentMe = isAttentUser(uId, otherId);
        boolean ifIAttentOther = isAttentUser(otherId, uId);
        if (ifOtherAttentMe && ifIAttentOther) {
            return true;
        } else {
            return false;
        }
    }
}
