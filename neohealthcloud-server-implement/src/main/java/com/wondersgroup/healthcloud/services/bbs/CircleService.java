package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.*;

import java.util.List;

/**
 * Created by ys on 2016/08/11.
 * 话题圈子信息
 */
public interface CircleService {

    /**
     * 获取圈子信息
     */
    Circle getCircleInfoById(Integer circleId);


    /**
     * 获取圈子分类
     *
     * @return
     */
    List<CircleCategoryDto> getCircleCategoryDtoList();


    /**
     * 获取我的圈子中banner轮换图
     *
     * @return
     */
    List<CircleBannerDto> getCircleBannerList();

    /**
     * 根据圈子分类id获取该分类下的所有圈子,包含用户是否关注
     * @return
     */
    List<CircleListDto> getCirclesByCId(Integer categoryId, String uId,Integer flag,Integer pageSize);

    /**
     * 根据用户uid 和 圈子id
     * 查询关注状态(未关注的时候，会检查这个圈子是否被设置为默认关注，如果是，则会给用户添加关注)
     */
    UserCircle getAndCheckIsDefaultJoin(Integer circleId, String uid);

    UserCircle queryByUIdAndCircleId(String uId, Integer circleId);

    /**
     * 加入圈子
     *
     * @return
     */
    UserCircle saveUserCircle(UserCircle userCircle);

    UserCircle updateUserCircle(UserCircle userCircle);

    List<TopicTab> getCircleTopicTabs(Integer circleId);

    /**
     * 查询今天圈子发布的话题数量
     */
    int getTodayPublishTopicCount(Integer circleId);

    /**
     * 查询圈子发布的话题数量
     */
    int getTodayPublishTopicCommentCount(Integer circleId);

    /**
     * 获取圈子资料
     *
     * @param circleId
     * @return
     */
    CircleInfoDto getCircleInfo(Integer circleId);

    /**
     * 更新真实的圈子关注人数
     * @param circleId
     * @return
     */
    int updateActuallyAttentionCount(Integer circleId);

    String getRecommendCircleNames();

    Boolean saveOrUpdateCircle(Circle newData);

    Circle getCircleByName(String circleName);

    List<AdminCircleDto> searchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag, int pageNo, int pageSize);

    int countSearchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag);

    List<CircleListDto> findGuessLikeCircles(String uid);
}
