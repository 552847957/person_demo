package com.wondersgroup.healthcloud.services.bbs;


/**
 * 点赞
 * @author ys
 */
public interface FavorService {

    /**
     * 话题点赞
     * @return 返回点赞总数
     */
    int favorTopic(String uid, Integer topicId);

    /**
     * 检测用户对该话题点过赞
     * @return true:已点赞, false:未点赞
     */
    Boolean isFavorTopic(String uid, Integer topicId);

}
