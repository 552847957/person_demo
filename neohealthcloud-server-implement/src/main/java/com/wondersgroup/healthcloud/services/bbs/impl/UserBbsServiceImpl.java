package com.wondersgroup.healthcloud.services.bbs.impl;

import java.util.*;

import com.wondersgroup.healthcloud.common.utils.StringsUtils;
import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import com.wondersgroup.healthcloud.jpa.repository.bbs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserBanLog;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.bbs.UserBbsService;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminAccountDto;
import com.wondersgroup.healthcloud.services.bbs.dto.UserBanInfo;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;
import com.wondersgroup.healthcloud.services.bbs.exception.BbsUserException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.user.UserService;


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
    private CommentRepository commentRepository;

    @Autowired
    private UserBanLogRepository userBanLogRepository;
    @Autowired
    private BbsMsgHandler bbsMsgHandler;

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
            bbsMsgHandler.adminDelTopic(topic.getUid(), topicId);
        }
        return true;
    }

    @Override
    public List<Circle> getUserJoinedCircles(String uid) {
        List<Circle> defaultAttents = circleRepository.findAllDefaultAttents();
        List<Circle> notJoinedDefaultAttents = new ArrayList<>();
        if (null != defaultAttents && !defaultAttents.isEmpty()){
            List<Integer> allJoinedCircleId = circleRepository.getAllUserJoinedCircleId(uid);
            for (Circle circle : defaultAttents){
                if (!allJoinedCircleId.contains(circle.getId())){
                    notJoinedDefaultAttents.add(circle);
                }
            }
        }
        List<Circle> joinedList = circleRepository.findUserJoinedCircles(uid);
        if (!notJoinedDefaultAttents.isEmpty()){
            joinedList.addAll(notJoinedDefaultAttents);
        }
        return joinedList;
    }

    @Override
    public List<CircleListDto> getUserJoinedCirclesDto(String uid) {
        List<Circle> cList = this.getUserJoinedCircles(uid);
        List<CircleListDto> dtoList = new ArrayList<>();
        if (cList != null && cList.size() > 0) {
            for (Circle circle : cList) {
                CircleListDto dto = new CircleListDto();
                dto.setId(circle.getId());
                dto.setName(circle.getName());
                dto.setIcon(circle.getIcon());
                dto.setDescription(circle.getDescription());
                dto.setForbidden(!circle.getDelFlag().equals("0"));
                // 我的圈子，都是已关注
                dto.setIfAttent(1);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Transactional
    @Override
    public boolean setUserBan(String adminUid, String uid, Integer banStatus, String reason) {
        RegisterInfo account = userService.getOneNotNull(uid);
        if(account.getIsBBsAdmin() == 1){
            throw new BbsUserException("不能操作管理员");
        }
        RegisterInfo loginInfo = userService.getOneNotNull(adminUid);
        if (null == loginInfo || loginInfo.getIsBBsAdmin() != 1){
            throw new BbsUserException("非管理员,没有相关权限");
        }

        UserBanLog banLog = new UserBanLog();
        banLog.setBanStatus(banStatus);
        banLog.setAdminUid(adminUid);
        banLog.setReason(reason);
        banLog.setUId(uid);
        banLog.setCreateTime(new Date());
        banLog = userBanLogRepository.save(banLog);
        if (account.getBanStatus().intValue() != banStatus){
            account.setBanStatus(banStatus);
            registerInfoRepository.save(account);
        }
        //通知LTS
        bbsMsgHandler.userBan(uid, adminUid, banStatus, banLog.getId());
        return true;
    }

    @Override
    public UserBanLog getUserBanInfoByUid(String uid) {
        RegisterInfo account = userService.getOneNotNull(uid);
        if (account.getBanStatus() == UserConstant.BanStatus.OK){
            return null;
        }
        return userBanLogRepository.findLastLogForUser(uid);
    }

    @Override
    public UserBanInfo getUserBanInfoByBanLogId(Integer banLogId) {
        UserBanLog userBanLog = userBanLogRepository.findOne(banLogId);
        UserBanInfo userBanInfo = new UserBanInfo(userBanLog);
        RegisterInfo registerInfo = registerInfoRepository.findOne(userBanLog.getAdminUid());
        userBanInfo.setAvatar(registerInfo.getHeadphoto());
        userBanInfo.setNickname(registerInfo.getNickname());
        return userBanInfo;
    }

    @Override
    public List<AdminAccountDto> queryBBSAdminList() {
        List<RegisterInfo> adminAccountList = registerInfoRepository.queryAllBBsAdmins();
        List<AdminAccountDto> dtoList = new ArrayList<>();
        if (adminAccountList != null && adminAccountList.size() > 0) {
            for (RegisterInfo account : adminAccountList) {
                AdminAccountDto dto = new AdminAccountDto();
                dto.setAvatar(account.getHeadphoto());
                dto.setId(account.getRegisterid());
                dto.setNickname(StringsUtils.subString(account.getNickname(), 5, "…"));
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public int countTopicByUid(String uid, Boolean isMine) {
        List<Integer> status = new ArrayList<>();
        status.add(TopicConstant.Status.OK);
        status.add(TopicConstant.Status.FORBID_REPLY);
        if (isMine){
            status.add(TopicConstant.Status.WAIT_VERIFY);
        }
        return topicRepository.countByPublishUidAndStatus(uid, status);
    }

    @Override
    public int countCommentByUid(String uid, Boolean isMine) {
        List<Integer> status = new ArrayList<>();
        status.add(CommentConstant.Status.OK);
        if (isMine){
            status.add(CommentConstant.Status.WAIT_VERIFY);
        }
        return commentRepository.countByReplyUidAndStatus(uid, status);
    }
}
