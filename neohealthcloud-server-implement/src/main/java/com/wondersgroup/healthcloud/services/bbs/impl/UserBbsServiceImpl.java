package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserBanLog;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.UserBanLogRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.bbs.UserBbsService;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;
import com.wondersgroup.healthcloud.services.bbs.exception.BbsUserException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("userBbsService")
public class UserBbsServiceImpl implements UserBbsService {

    @Autowired
    private CircleRepository circleRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserBanLogRepository userBanLogRepository;

    @Override
    public boolean delTopic(String uid, Integer topicId) {
        RegisterInfo account = userService.getOneNotNull(uid);
        Topic topic = topicRepository.findOne(topicId);
        if (topic == null){
            throw new TopicException(2021, "话题不存在");
        }
        if (account.getIsBBsAdmin() != 1 && !account.getRegisterid().equals(topic.getUid())){
            throw new TopicException(2022, "您不是管理员,不能该删除话题");
        }
        if (account.getRegisterid().equals(topic.getUid())){
            topic.setStatus(TopicConstant.Status.USER_DELETE);
        }else {
            topic.setStatus(TopicConstant.Status.ADMIN_DELETE);
        }
        topicRepository.save(topic);
        if (account.getIsBBsAdmin() == 1){
            //BbsMsgHandler.adminDelTopic(topic.getUid(), topicId);
        }
        return true;
    }

    @Override
    public List<Circle> getUserJoinedCircles(String uid) {
        return circleRepository.findUserJoinedCircles(uid);
    }

    @Override
    public List<CircleListDto> getUserJoinedCirclesDto(String uid) {
        List<Circle> cList = circleRepository.findUserJoinedCircles(uid);
        List<CircleListDto> dtoList = new ArrayList<>();
        if (cList != null && cList.size() > 0) {
            for (Circle circle : cList) {
                CircleListDto dto = new CircleListDto();
                dto.setId(circle.getId());
                dto.setName(circle.getName());
                dto.setIcon(circle.getIcon());
                dto.setForbidden(circle.getDelflag().equals("0")? false : true);
                // 我的圈子，都是已关注，无需展示，json会忽略
                dto.setIfAttent(null);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Transactional
    @Override
    public boolean setUserBan(String loginUid, String uid, Integer banStatus, String reason) {
        RegisterInfo account = userService.getOneNotNull(uid);
        if(account.getIsBBsAdmin() == 1){
            throw new BbsUserException("不能操作管理员");
        }
        RegisterInfo loginInfo = userService.getOneNotNull(loginUid);
        if (null == loginInfo || loginInfo.getIsBBsAdmin() != 1){
            throw new BbsUserException("非管理员,没有相关权限");
        }

        UserBanLog banLog = new UserBanLog();
        banLog.setBanStatus(banStatus);
        banLog.setAdminUid(loginUid);
        banLog.setReason(reason);
        banLog.setUId(uid);
        banLog.setCreateTime(new Date());
        banLog = userBanLogRepository.save(banLog);
        if (account.getBanStatus().intValue() != banStatus){
            account.setBanStatus(banStatus);
            registerInfoRepository.save(account);
        }
        //通知LTS
        //BbsMsgHandler.userBan(uid, loginUid, banStatus, banLog.getId());
        return true;
    }

    /**
     * 根据UID，获取用户禁言信息
     * @param uid 用户ID
     * @return 如果不存在用户禁言数据，返回空对象
     */
    @Override
    public Map<String, Object> getUserBanInfoByUid(String uid) {
        RegisterInfo account = userService.getOneNotNull(uid);
        if (account.getBanStatus().intValue() == UserConstant.BanStatus.OK){
            return null;
        }
        UserBanLog userBanLog = userBanLogRepository.findLastLogForUser(uid);
        Map<String, Object> dataWrap=new HashMap<>();
        if(null != userBanLog){
            dataWrap.put("expire", userBanLog.getBanStatus());
            dataWrap.put("reason", userBanLog.getReason());
        }
        return dataWrap;
    }
}
