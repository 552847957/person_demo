package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBbsInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by ys on 2016/08/11.
 * 用户 - 圈子 的相关
 */
public interface UserBbsService {

    boolean delTopic(String uid, Integer topicId);

    /**
     * 获取用户加入的圈子
     */
    List<Circle> getUserJoinedCircles(String uid);

    /**
     * 获取用户加入的圈子-格式优化
     */
    List<CircleListDto> getUserJoinedCirclesDto(String uid);

    /**
     * 设置用户禁言状态
     */
    boolean setUserBan(String loginUid, String uid, Integer banStatus, String reason);

    Map<String, Object> getUserBanInfoByUid(String uid);

}
