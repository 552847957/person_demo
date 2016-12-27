package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.exceptions.CommonException;
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
import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.criteria.CommentSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.exception.PublishCommentException;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.config.ConfigSwitch;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
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
    @Autowired
    private BbsMsgHandler bbsMsgHandler;
    @Autowired
    private BadWordsService badWordsService;
    @Autowired
    private ConfigSwitch configSwitch;

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
            Topic topic = topicMap.get(commentListDto.getTopicId());
            topic.setTitle(badWordsService.dealBadWords(topic.getTitle()));
            commentListDto.mergeTopicInfo(topic);
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
            topic.setTitle(badWordsService.dealBadWords(topic.getTitle()));
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
            commentListDto.dealBadWords(badWordsService);
            rtList.add(commentListDto);
        }
        return rtList;
    }

    @Override
    @Transactional
    public Comment publishComment(CommentPublishDto publishDto) {
        Topic topic = topicRepository.findOne(publishDto.getTopicId());
        this.checkTopicIsCanReply(topic);
        Circle circle = circleRepository.findOne(topic.getCircleId());
        if (null == circle || circle.getDelFlag().equals("1")) {
            throw PublishCommentException.circleDel();
        }
        RegisterInfo userInfo = userService.getOneNotNull(publishDto.getUid());
        if (userInfo.getBanStatus() != UserConstant.BanStatus.OK){
            throw PublishCommentException.userBan();
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
            bbsMsgHandler.commentNewReply(comment.getReferUId(),topic.getId(), comment.getUid(),comment.getFloor());
        }else {
            bbsMsgHandler.topicNewReply(topic.getUid(), topic.getId());
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
        comment.setStatus(getUserPublishCommentDefaultStatus(publishDto.getIsAdminPublish()));
        if (publishDto.getReferCommentId() != null && publishDto.getReferCommentId() > 0){
            Comment referComment = commentRepository.findOne(publishDto.getReferCommentId());
            if (null != referComment){
                comment.setReferCommentId(publishDto.getReferCommentId());
                comment.setReferUId(referComment.getUid());
            }
        }
        return commentRepository.save(comment);
    }

    /**
     * 获取发表评论默认状态
     */
    private int getUserPublishCommentDefaultStatus(Boolean isAdmin){
        if (isAdmin){
            return TopicConstant.Status.OK;
        }
        return configSwitch.isVerifyComment() ? CommentConstant.Status.WAIT_VERIFY : CommentConstant.Status.OK;
    }

    @Override
    public Boolean delCommonById(String uid, Integer commentId) {
        RegisterInfo account = userService.getOneNotNull(uid);
        //普通用户不能删除评论
        if(account.getIsBBsAdmin() != 1){
            throw new CommonException(1000, "您当前没有权限删除该评论");
        }
        commentRepository.updateStatusById(CommentConstant.Status.DELETE, commentId);
        bbsMsgHandler.adminDelComment(uid, commentId);
        return true;
    }

    @Override
    public Boolean delCommonByIds(String adminUid, List<Integer> commentIds) {
        if (null == commentIds || commentIds.isEmpty()){
            return false;
        }
        RegisterInfo account = userService.getOneNotNull(adminUid);
        //普通用户不能删除评论
        if(account.getIsBBsAdmin() != 1){
            throw new CommonException(1000, "您当前没有权限删除评论");
        }
        //删除回复
        commentRepository.updateStatusByIds(CommentConstant.Status.DELETE, commentIds);

        bbsMsgHandler.adminDelComment(adminUid, commentIds);
        return true;
    }

    @Override
    public List<Map<String, Object>> getCommentListByCriteria(CommentSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select `comment`.*,topic.title as title,circle.name as circle_name,user.nickname ");
        querySql.append(" from tb_bbs_comment `comment`  ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=`comment`.topic_id ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" LEFT JOIN app_tb_register_info user on user.registerid=`comment`.uid ");
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        System.out.println(querySql.toString());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Override
    public int countCommentByCriteria(CommentSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_comment `comment` ");
        querySql.append(" left join tb_bbs_topic topic on topic.id=`comment`.topic_id ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" LEFT JOIN app_tb_register_info user on user.registerid=`comment`.uid ");
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }

        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }

    @Override
    public Map<String, Object> getCommentInfoById(Integer id) {
        StringBuffer querySql = new StringBuffer("SELECT `comment`.id, topic.id as topicId, topic.title AS title, `comment`.floor," +
                " user.nickname, `comment`.create_time, circle.`name` AS circle_name, `comment`.content " +
                " FROM tb_bbs_comment `comment` " +
                " LEFT JOIN tb_bbs_topic topic ON topic.id = `comment`.topic_id " +
                " LEFT JOIN tb_bbs_circle circle ON circle.id = topic.circle_id " +
                " LEFT JOIN app_tb_register_info user on user.registerid = `comment`.uid" +
                " where `comment`.id = ");
        querySql.append(id);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString());
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
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
        String queryReply = "select c.id, c.floor, c.content, c.create_time, user.nickname " +
                " from tb_bbs_comment c " +
                " LEFT JOIN app_tb_register_info user on user.registerid = c.uid " +
                " where c.uid in ("+uidsIn+") and c.topic_id="+topicId;
        List<Map<String, Object>> replys = jdbcTemplate.queryForList(queryReply);
        return replys;
    }

    @Override
    public Map<String, Object> getCommentInfoByIdWithReplys(Integer id) {
        Map<String, Object> info = this.getCommentInfoById(id);
        if (info == null){
            return null;
        }
        String queryReply = "select c.id, c.floor, c.content, c.create_time, user.nickname " +
                " from tb_bbs_comment c " +
                " LEFT JOIN app_tb_register_info user on user.registerid = c.uid " +
                " where c.refer_comment_id="+id;
        List<Map<String, Object>> replys = jdbcTemplate.queryForList(queryReply);
        if(replys != null && replys.size()>0){
            info.put("replys", replys);
        }
        return info;
    }

    @Override
    public Comment findOne(Integer id) {
        return commentRepository.findOne(id);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.saveAndFlush(comment);
    }

    private void checkTopicIsCanReply(Topic topic){
        if(topic == null){
            throw new TopicException(2001, "帖子无效,不能回复");
        }
        if (topic.getStatus() == TopicConstant.Status.WAIT_VERIFY){
            throw PublishCommentException.topicWaitVerify();
        }
        if (topic.getStatus() != TopicConstant.Status.OK){
            throw new TopicException(2001, "帖子无效,不能回复");
        }
    }
}
