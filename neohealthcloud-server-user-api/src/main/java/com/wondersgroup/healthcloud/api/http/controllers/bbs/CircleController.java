package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.NumberUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminAccountDto;
import com.wondersgroup.healthcloud.services.bbs.dto.JoinedAndGuessLikeCirclesDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.*;
import com.wondersgroup.healthcloud.services.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 1. 圈子首页
 * 2. 我的圈
 * 3. 圈子banner图查询
 * 4. 圈子分类
 * 5. 根据分类id，查询该分类下的所有圈子
 * 6. 加入圈子
 * 7. 离开圈子
 * 8. 圈子资料
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/circle")
public class CircleController {

    private static final Logger logger = LoggerFactory.getLogger("CircleController");

    @Autowired
    private TopicService topicService;

    @Autowired
    private CircleService circleService;
    @Autowired
    private UserBbsService userBbsService;
    @Autowired
    private UserService userService;

    /**
     * 圈子首页
     */
    @VersionRange
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public JsonResponseEntity<CircleHomeDto> home(@RequestParam String uid, @RequestParam Integer circleId) {
        JsonResponseEntity<CircleHomeDto> entity = new JsonResponseEntity();

        RegisterInfo account = userService.getOneNotNull(uid);
        Circle circle = circleService.getCircleInfoById(circleId);
        if (circle == null) {
            throw new CommonException(2001, "圈子无效");
        }
        CircleHomeDto circleHomeDto = new CircleHomeDto();
        if (account.getBanStatus() != UserConstant.BanStatus.OK){
            circleHomeDto.setUserPublishStatus(UserConstant.UserCommentStatus.USER_BAN);
        }
        circleHomeDto.mergeCircleInfo(circle);
        List<TopicTab> topicTabs = circleService.getCircleTopicTabs(circleId);
        circleHomeDto.mergeCircleTopicTab(topicTabs);
        circleHomeDto.setTopTopics(topicService.getCircleTopRecommendTopics(circleId, 5));
        //是否关注
        UserCircle userCircle = circleService.getAndCheckIsDefaultJoin(circleId, uid);
        circleHomeDto.setIfAttent(null == userCircle ? 0 : 1);
        //当日活跃数=当日发帖数+当日回复数量
        int publishTopicCount = circleService.getTodayPublishTopicCount(circleId);
        int publishTopicCommentCount = circleService.getTodayPublishTopicCommentCount(circleId);
        circleHomeDto.setTodayActiveCount(NumberUtils.formatCustom1(publishTopicCount + publishTopicCommentCount));
        entity.setData(circleHomeDto);
        return entity;
    }

    /**
     * 我的圈(我的圈子+[我的喜欢]推荐圈子下的未加入圈子) modify zhongshuqing
     *
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/myCircleList", method = RequestMethod.GET)
    public JsonResponseEntity myCircleList(@RequestParam String uid) {
        JsonResponseEntity entity = new JsonResponseEntity();
        try {
            JoinedAndGuessLikeCirclesDto dto = new JoinedAndGuessLikeCirclesDto();
            List<CircleListDto> myCircleList = userBbsService.getUserJoinedCirclesDto(uid);
            dto.setJoinedList(myCircleList);
            List<CircleListDto> guessLikeList = circleService.findGuessLikeCircles(uid);
            dto.setGuessLikeList(guessLikeList);
            entity.setData(dto);
            return entity;
        } catch (Exception e) {
            String errorMsg = "查询我关注的圈子&猜你喜欢出错";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }

    /**
     * 圈子banner图查询
     *
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/circleBannerList", method = RequestMethod.GET)
    public JsonResponseEntity circleBannerList() {
        JsonResponseEntity entity = new JsonResponseEntity();
        try {
            List<CircleBannerDto> bannerList = circleService.getCircleBannerList();
            entity.setData(bannerList);
            return entity;
        } catch (Exception e) {
            String errorMsg = "查询圈子banner出错";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }

    /**
     * 圈子分类
     */
    @VersionRange
    @RequestMapping(value = "/getCircleCategory", method = RequestMethod.GET)
    public JsonResponseEntity<List<CircleCategoryDto>> getCircleCategory() {
        JsonResponseEntity<List<CircleCategoryDto>> entity = new JsonResponseEntity();
        List<CircleCategoryDto> cateList = circleService.getCircleCategoryDtoList();
        if (null == cateList){
            throw new CommonException(1001, "查询圈子分类出错");
        }
        entity.setData(cateList);
        return entity;
    }

    /**
     * 根据分类id，查询该分类下的所有圈子
     * 改为下拉分页   modify zhongshuqing
     * @param categoryId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/getCirclesByCategoryId", method = RequestMethod.GET)
    public JsonListResponseEntity getCirclesByCategoryId(@RequestParam int categoryId, @RequestParam String uid,
                                                         @RequestParam(defaultValue = "1", required = false) Integer flag) {
        JsonListResponseEntity jsonListResponseEntity = new JsonListResponseEntity();
        int pageSize = 10;
        try {
            List<CircleListDto> cList = circleService.getCirclesByCId(categoryId, uid, flag, pageSize);
            
            Boolean hasMore = false;
            if (cList != null && cList.size() > pageSize){
                cList = cList.subList(0, pageSize);
                hasMore = true;
            }
            jsonListResponseEntity.setContent(cList, hasMore, null, String.valueOf(flag+1));
            return jsonListResponseEntity;
        } catch (Exception e) {
            String errorMsg = "查询分类圈子出错";
            logger.error(errorMsg, e);
            jsonListResponseEntity.setCode(1001);
            jsonListResponseEntity.setMsg(errorMsg);
        }
        return jsonListResponseEntity;
    }

    /**
     * 加入圈子
     *
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/joinLeaveCircle", method = RequestMethod.POST)
    public JsonResponseEntity joinLeaveCircle(@RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer circleId = reader.readInteger("circleId", false);
        String uid = reader.readString("uid", false);
        Circle circle = circleService.getCircleInfoById(circleId);
        if (null == circle || circle.getDelFlag().equals("1")){
            entity.setCode(1004);
            entity.setMsg("该圈子已禁用");
            return entity;
        }
        try {
            UserCircle exist = circleService.queryByUIdAndCircleId(uid, circleId);
            if (exist != null) {
                String delFlag = exist.getDelFlag();
                // 未删除
                if ("0".equals(delFlag)) {
                    entity.setMsg("您已加入该圈子");
                    return entity;
                }
                // 已删除
                if ("1".equals(delFlag)) {
                    exist.setDelFlag("0");
                    UserCircle updateResult = circleService.updateUserCircle(exist);
                    logger.info(String.format("[%s]重新加入[%s]圈子成功", uid, circleId));
                    // 更新关注人数
                    circleService.updateActuallyAttentionCount(circleId);
                    entity.setMsg("加入成功");
                    entity.setData(updateResult);
                    return entity;
                }
            } else {
                UserCircle userCircle = new UserCircle();
                userCircle.setCircleId(circleId);
                userCircle.setUId(uid);
                UserCircle result = circleService.saveUserCircle(userCircle);
                if (result != null) {
                    entity.setData(result);
                    entity.setMsg("加入成功");
                    logger.info(String.format("[%s]加入[%s]圈子成功", uid, circleId));
                    // 更新关注人数
                    circleService.updateActuallyAttentionCount(circleId);
                    return entity;
                } else {
                    entity.setCode(1003);
                    entity.setMsg("操作失败");
                    return entity;
                }
            }
        } catch (Exception e) {
            String errorMsg = "加入圈子出错";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }

    /**
     * 离开圈子，DELETE
     *
     * @param circleId
     * @param uid
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/joinLeaveCircle", method = RequestMethod.DELETE)
    public JsonResponseEntity leaveCircle(@RequestParam Integer circleId, @RequestParam String uid) {
        JsonResponseEntity entity = new JsonResponseEntity();
        try {
            UserCircle exist = circleService.getAndCheckIsDefaultJoin(circleId, uid);
            if (exist != null) {
                exist.setDelFlag("1");
                circleService.updateUserCircle(exist);
                //circleService.deleteUserCircle(circleId, uid);
                entity.setMsg("成功退出圈子");
                logger.info(String.format("[%s]退出[%s]圈子成功", uid, circleId));
                // 更新关注人数
                circleService.updateActuallyAttentionCount(circleId);
                return entity;
            } else {
                entity.setMsg("您未加入该圈子");
                return entity;
            }
        } catch (Exception e) {
            String errorMsg = "退出圈子出错";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }


    /**
     * 圈子资料
     *
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/getCircleInfo", method = RequestMethod.GET)
    public JsonResponseEntity getCircleInfo(@RequestParam Integer circleId, @RequestParam String uid) {
        JsonResponseEntity entity = new JsonResponseEntity();
        try {
            CircleFullInfoDto fullInfoDto = new CircleFullInfoDto();
            // 圈子资料
            CircleInfoDto dto = circleService.getCircleInfo(circleId);
            BeanUtils.copyProperties(dto, fullInfoDto);
            // 是否关注
            UserCircle userCircle = circleService.getAndCheckIsDefaultJoin(circleId, uid);
            if (userCircle != null) {
                fullInfoDto.setIfAttent(1);
            }
            // 话题数
            int publishTopic = dto.getTopicCount();
            String topicCountStr = NumberUtils.formatCustom1(publishTopic);
            fullInfoDto.setTopicCount(topicCountStr);

            // 圈子人数
            int manCount = dto.getAttentionCount();
            // 添加虚拟关注人数
            manCount += dto.getFakeAttentionCount();
            String manCountStr = NumberUtils.formatCustom1(manCount);
            fullInfoDto.setCircleManCount(manCountStr);

            // 管理员列表
            List<AdminAccountDto> dtoList = userBbsService.queryBBSAdminList();
            fullInfoDto.setAdminList(dtoList);

            entity.setData(fullInfoDto);
            return entity;
        } catch (BeansException e) {
            String errorMsg = "查询圈子资料出错";
            logger.error(errorMsg, e);
            entity.setCode(1001);
            entity.setMsg(errorMsg);
        }
        return entity;
    }
}
