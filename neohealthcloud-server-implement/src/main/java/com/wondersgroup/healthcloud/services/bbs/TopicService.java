package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.*;

import java.util.List;

/**
 * Created by ys on 2016/08/13.
 * <p>
 * 注：有些方法 返回数据为pageSize+1个(用于判断是否还有更多),
 *    使用时需要判断并截取返回的列表(为了提高效率，减少为了判断是否更多再次连接db获取数据)
 * 具体方法见备注
 * </p>
 * @author ys
 */
public interface TopicService {

    /**
     * 获取圈子分类下面的话题列表(不包含置顶贴)
     * tabId=0表示全部分类
     * 返回数据为pageSize+1个
     */
    List<TopicListDto> getCircleTopicListByTab(Integer circleId, Integer tabId, Integer page, Integer pageSize);

    /**
     * 获取圈子的置顶推荐
     */
    List<TopicTopListDto> getCircleTopRecommendTopics(Integer circleId, Integer getNum);

    /**
     * 获取圈子的精华推荐列表
     * （最新回复时间排序）
     * 返回数据为pageSize+1个
     */
    List<TopicListDto> getCircleBestRecommendTopics(Integer circleId, Integer page, Integer pageSize);

    /**
     * 根据用户关注的圈子推荐 精华列表
     * 如果没有关注则随机推荐精华帖
     * （帖子的update_time desc排序）
     * 返回数据为pageSize+1个
     */
    List<TopicListDto> getBestRecommendTopicsForUser(String uid, Integer page, Integer pageSize);

    /**
     * 获取热门话题推荐
     * 返回数据为pageSize+1个
     */
    List<TopicListDto> getHotRecommendTopics(String uid, Integer page, Integer pageSize);

    /**
     * 根据帖子ids 获取帖子列表信息
     * 用于用户收藏
     */
    List<TopicListDto> getTopicsByIds(Iterable<Integer> topicIds);

    /**
     * 获取用户发表的帖子列表
     * 返回数据为pageSize+1个
     */
    List<TopicListDto> getTopicsByUid(String uid, Integer page, Integer pageSize);

    TopicDetailDto getTopicDetailInfo(Integer topicId);

    //给话题增加pv
    void incTopicPv(Integer topicId);

    /**
     * 发布话题
     * @return 成功返回topic_id,失败返回0
     */
    int publishTopic(TopicPublishDto publishInfo);

    int getCommentCount(Integer topicId);

    int getOwnerCommentCount(Integer topicId);

    //------------------------------//
    Topic infoTopic(Integer topicId);

    Topic delTopic(String uid, Integer topicId);

    //审核通过
    int verifyPass(Iterable<Integer> topicIds);
    //审核不通过
    int verifyUnPass(Iterable<Integer> topicIds);

    int settingTopic(TopicSettingDto topicSettingDto);

    int countTopicByCriteria(TopicSearchCriteria searchCriteria);
}
