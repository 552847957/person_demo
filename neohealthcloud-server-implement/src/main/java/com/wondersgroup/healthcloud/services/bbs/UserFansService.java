package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.UserFans;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBbsInfo;

import java.util.List;
import java.util.Map;

/**
 * </p>
 * Created by ys on 2016-12-09.
 */
public interface UserFansService {

    UserFans queryByUidAndFansUid(String uid, String fansUid);

    /**
     * 获取uid关注的用户列表
     */
    List<UserBbsInfo> getAttentUsers(String uid, int page, int pageSize);

    /**
     * 获取uid的粉丝列表
     */
    List<UserBbsInfo> getFansUsers(String uid, int page, int pageSize);

    /**
     * 从filterUids筛选出我关注的用户
     */
    List<String> filterMyAttentUser(String myUid, List<String> filterUids);

    /**
     * 从filterUids筛选出我的粉丝
     */
    List<String> filterMyFans(String myUid, List<String> filterUids);

    /**
     * 获取我和uids的关注状态
     * @return Map [key:uid, value:我关注的状态(0:未关注,1:已关注,2:已相互关注)]
     */
    Map<String, Integer> getMyAttentStatus(String myUid, List<String> targetUids);

    /**
     * 获取我和uids的关注状态
     * @return 我关注的状态(0:未关注,1:已关注,2:已相互关注)]
     */
    Integer getMyAttentStatus(String myUid, String targetUid);
    /**
     * 获取关注数
     */
    int countAttentNum(String uid);

    /**
     * 获取我的粉丝数
     */
    int countFansNum(String uid);

    Boolean isAttent(String uid, String targetUid);

    Boolean isFans(String uid, String targetUid);

    UserFans saveFans(UserFans fans);

    //是否相互关注
    boolean isAttentEachOther(String otherId, String uId);
}
