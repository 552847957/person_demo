package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
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
     * 获取有效的圈子列表
     */
    List<Circle> getCircleList(Integer page, Integer pageSize);

    /**
     * 获取圈子分类下面的有效的圈子列表
     */
    List<Circle> getCircleListByCateId(Integer cateId, Integer page, Integer pageSize);


    /**
     * 获取圈子信息
     */
    Circle getCircleInfoById(Integer circleId);

    /**
     * 获取圈子 有效的分类
     */
    List<CircleCategory> getCircleCateList();

    /**
     * 获取默认加入的圈子列表
     * @return
     */
    List<Circle> defaultAttentList();

    /**
     * 获取圈子分类
     *
     * @return
     */
    List<CircleCategoryDto> getCircleCategoryDtoList();

    /**
     * 获取“我的圈”信息
     *
     * @param userId
     * @return
     */
    List<MyCircleDto> getMyCircleList(String userId);

    /**
     * 获取我的圈子中banner轮换图
     *
     * @return
     */
    List<CircleBannerDto> getCircleBannerList();

    /**
     * 根据圈子分类id获取该分类下的所有圈子,包含用户是否关注
     *
     * @return
     */
    List<CircleListDto> getCirclesByCId(Integer categoryId, String uId);

    /**
     * 根据用户id和圈子id，查询该用户是否已加入这个圈子
     *
     * @param uId
     * @param circleId
     * @return
     */
    UserCircle queryByUIdAndCircleIdAndDelFlag(String uId, Integer circleId, String delFlag);

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
     * 获取圈子的关注人数
     *
     * @param circleId
     * @param delFlag
     * @return
     */
    int getAttentCount(Integer circleId, String delFlag);

    /**
     * 加入默认圈子
     * @return
     */
    int joinInDefaultCircles(String uId);

    /**
     * 圈子关注人数+N
     * @param circleId
     */
    void increaseAttentionCount(Integer circleId, Integer num);

    /**
     * 更新真实的圈子关注人数
     * @param circleId
     * @return
     */
    int updateActuallyAttentionCount(Integer circleId);

    String getRecommendCircleNames();

    Boolean saveOrUpdateCircle(Circle newData);

    int checkCircleNameByName(int id, String circleName);

    List<AdminCircleDto> searchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag, int pageNo, int pageSize);

    int countSearchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag);


}
