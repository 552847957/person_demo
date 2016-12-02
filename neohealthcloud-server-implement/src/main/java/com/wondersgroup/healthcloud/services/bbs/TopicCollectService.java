package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicCollect;

import java.util.List;

/**
 * Created by ys on 2016/08/13.
 * 圈子收藏相关
 * @author ys
 */
public interface TopicCollectService {

    /**
     * 获取用户收藏的话题列表
     * 返回数据为pageSize+1个
     */
    List<TopicCollect> getCollectTopicListByUid(String uid, Integer page, Integer pageSize);

    /**
     * 检测用户是否收藏过
     * @return true:已收藏, false:未收藏
     */
    Boolean isCollectedForUser(String uid, Integer topicId);

    TopicCollect collectTopic(String uid, Integer topicId);

}
