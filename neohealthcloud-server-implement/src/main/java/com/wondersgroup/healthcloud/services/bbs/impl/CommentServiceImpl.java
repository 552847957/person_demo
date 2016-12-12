package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.constant.CommentConstant;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CommentRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.criteria.CommentSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.exception.BbsUserException;
import com.wondersgroup.healthcloud.services.bbs.exception.CircleException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CircleRepository circleRepository;
    @Autowired
    private BbsAdminService bbsAdminService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<CommentListDto> getTopicOwnerCommentsList(Integer topicId, Integer page, Integer pageSize) {
        List<Comment> comments = commentRepository.findTopicOwnerComments(topicId, (page-1)*pageSize, pageSize);
        if (null == comments || comments.isEmpty()){
            return null;
        }
        List<CommentListDto> listDtos = this.buildCommentListDaos(comments);
        return listDtos;
    }

    @Override
    public List<CommentListDto> getUserCommentsList(String uid, Integer page, Integer pageSize) {
        List<Comment> comments = commentRepository.findListByUid(uid, (page-1)*pageSize, pageSize+1);
        if (null == comments || comments.isEmpty()){
            return null;
        }
        Set<Integer> topicIds = new HashSet<>();
        for (Comment comment : comments){
            topicIds.add(comment.getTopicId());
        }
        List<Topic> topics = topicRepository.findAll(topicIds);
        Map<Integer, Topic> topicMap = new HashMap<>();
        for (Topic topic : topics){
            topicMap.put(topic.getId(), topic);
        }
        //集成topic信息
        List<CommentListDto> listDtos = this.buildCommentListDaos(comments);
        for (CommentListDto commentListDto : listDtos){
            commentListDto.mergeTopicInfo(topicMap.get(commentListDto.getTopicId()));
        }
        return listDtos;
    }

    @Override
    public List<CommentListDto> getCommentListByTopicId(Integer topicId, Integer page, Integer pageSize) {
        return this.getCommentListByFloor(topicId, (page-1)*pageSize+1, pageSize);
    }

    @Override
    public List<CommentListDto> getCommentListByFloor(Integer topicId, Integer floor, Integer pageSize) {
        int nowPage = (floor-1) / pageSize + 1;
        int nowPageStartFloor = (nowPage - 1) * pageSize + 1;
        Topic topic = topicRepository.findOne(topicId);
        if (topic == null || TopicConstant.Status.isDelStatus(topic.getStatus())){
            throw new TopicException(2001, "话题被删除");
        }
        List<Comment> comments = commentRepository.findListByTopicAndFloor(topicId, nowPageStartFloor, pageSize);
        if (null == comments || comments.isEmpty()){
            return null;
        }
        List<CommentListDto> listDtos = this.buildCommentListDaos(comments);
        for (CommentListDto commentListDto : listDtos){
            commentListDto.mergeTopicInfo(topic);
        }
        return listDtos;
    }

    private List<CommentListDto> buildCommentListDaos(List<Comment> comments){
        List<Integer> commentIds = new ArrayList<>();
        List<Integer> referCommentIds = new ArrayList<>();
        Map<Integer, Comment> commentsMap = new HashMap<>();
        Set<String> uids = new HashSet<>();
        for (Comment comment : comments){
            commentsMap.put(comment.getId(), comment);
            uids.add(comment.getUid());
            commentIds.add(comment.getId());
            if (comment.getReferCommentId() > 0){
                uids.add(comment.getReferUId());
                referCommentIds.add(comment.getReferCommentId());
            }
        }
        Map<String, RegisterInfo> userMap = userService.findByUids(uids);
        referCommentIds.removeAll(commentIds);
        if (!referCommentIds.isEmpty()){
            List<Comment> referComments = commentRepository.findAll(referCommentIds);
            for (Comment comment : referComments){
                commentsMap.put(comment.getId(), comment);
            }
        }
        List<CommentListDto> rtList = new ArrayList<>();
        //开始拼接数据
        for (Comment comment : comments){
            CommentListDto commentListDto = new CommentListDto();
            commentListDto.mergeCommentInfo(comment);
            commentListDto.mergeCommentUserInfo(userMap.get(comment.getUid()));
            if (comment.getReferCommentId() > 0 && commentsMap.containsKey(comment.getReferCommentId())){
                RegisterInfo referUserInfo = userMap.get(comment.getReferUId());
                commentListDto.mergeReferCommentInfo(commentsMap.get(comment.getReferCommentId()), referUserInfo);
            }
            rtList.add(commentListDto);
        }
        return rtList;
    }

    @Override
    @Transactional
    public Comment publishComment(CommentPublishDto publishDto) {
        Topic topic = topicRepository.findOne(publishDto.getTopicId());
        if (topic == null || topic.getStatus() != TopicConstant.Status.OK){
            throw new TopicException(2001, "帖子无效,不能回复");
        }
        Circle circle = circleRepository.findOne(topic.getCircleId());
        if (null == circle || circle.getDelFlag().equals("1")) {
            throw CircleException.NotExistForReply();
        }
        RegisterInfo withBabyInfo = userService.getOneNotNull(publishDto.getUid());
        if (withBabyInfo.getBanStatus().intValue() != UserConstant.BanStatus.OK){
            throw BbsUserException.UserBanForReply();
        }
        int commentCount = topic.getCommentCount();
        publishDto.setFloor(commentCount + 1);
        publishDto.setIsOwner(publishDto.getUid().equals(topic.getUid()) ? 1 : 0);
        Comment comment = this.saveComment(publishDto);
        topic.setCommentCount(commentCount+1);
        topic.setLastCommentTime(new Date());
        topicRepository.save(topic);
        //通知相关消息
        if (comment.getReferCommentId() > 0){
            //BbsMsgHandler.commentNewReply(comment.getReferUId(),topic.getId(), comment.getUid(),comment.getFloor());
        }else {
            //BbsMsgHandler.topicNewReply(topic.getUid(), topic.getId());
        }
        return comment;
    }

    private Comment saveComment(CommentPublishDto publishDto){
        Comment comment = new Comment();
        comment.setTopicId(publishDto.getTopicId());
        comment.setContent(publishDto.getContent());
        comment.setUid(publishDto.getUid());
        comment.setIsOwner(publishDto.getIsOwner());
        Date nowDate = new Date();
        comment.setUpdateTime(nowDate);
        comment.setCreateTime(nowDate);
        comment.setFloor(publishDto.getFloor());
        if (publishDto.getReferCommentId() != null && publishDto.getReferCommentId() > 0){
            Comment referComment = this.getCommentInfoByCommentId(publishDto.getReferCommentId());
            if (null != referComment){
                comment.setReferCommentId(publishDto.getReferCommentId());
                comment.setReferUId(referComment.getUid());
            }
        }
        return commentRepository.save(comment);
    }

    public Comment getCommentInfoByCommentId(int commentId){
        return commentRepository.findOne(commentId);
    }

    @Override
    public Boolean delCommonByIds(Iterable<Integer> ids) {
        commentRepository.updateStatusByIds(CommentConstant.Status.DELETE, ids);
        return true;
    }

    //--------------------------//


    @Override
    public List<Map<String, Object>> getCommentListByCriteria(CommentSearchCriteria searchCriteria) {
        return null;
    }

    @Override
    public int countCommentByCriteria(CommentSearchCriteria searchCriteria) {
        return 0;
    }

    @Override
    public Map<String, Object> getCommentInfoById(Integer id) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getCommentListByAdminAppUid(Integer topicId, String uid) {
        if (StringUtils.isEmpty(uid)){
            return null;
        }
        Set<String> uids = new HashSet<>();
        uids.add(uid);
        List<String> vestUids = bbsAdminService.getAdminVestUidsByAdminUid(uid);
        if (null != vestUids){
            uids.addAll(vestUids);
        }
        String uidsIn = "";
        for (String str : uids) {
            uidsIn += "'" + str + "',";
        }
        uidsIn = uidsIn.length() > 0 ? uidsIn.substring(0, uidsIn.length()-1) : "";
        String queryReply = "select comment.id, comment.floor, comment.content, comment.create_time, account.nickname " +
                " from tb_bbs_comment comment " +
                " LEFT JOIN tb_account_user account ON account.id = comment.uid " +
                " where comment.uid in ("+uidsIn+") and comment.topic_id="+topicId;
        List<Map<String, Object>> replys = jdbcTemplate.queryForList(queryReply);
        return replys;
    }

    @Override
    public Map<String, Object> getCommentInfoByIdWithReplys(Integer id) {
        Map<String, Object> info = this.getCommentInfoById(id);
        if (info == null){
            return null;
        }
        String queryReply = "select comment.id, comment.floor, comment.content, comment.create_time, account.nickname " +
                " from tb_bbs_comment comment " +
                " LEFT JOIN tb_account_user account ON account.id = comment.uid " +
                " where comment.refer_comment_id="+id;
        List<Map<String, Object>> replys = jdbcTemplate.queryForList(queryReply);
        if(replys != null && replys.size()>0){
            info.put("replys", replys);
        }
        return info;
    }

    @Override
    public Comment findOne(Integer id) {
        Comment comment = commentRepository.findOne(id);
        return comment;
    }

    @Override
    public Boolean delCommonByIds(String adminUid, List<Integer> ids) {
        if (null == ids || ids.isEmpty()){
            return false;
        }
        //删除回复
        commentRepository.updateStatusByIds(CommentConstant.Status.DELETE, ids);

//        BbsMsgHandler.adminDelComment(adminUid, ids);
        return true;
    }

    @Override
    public Comment saveComment(Comment comment) {
        Comment result = commentRepository.saveAndFlush(comment);
        return result;
    }
}
