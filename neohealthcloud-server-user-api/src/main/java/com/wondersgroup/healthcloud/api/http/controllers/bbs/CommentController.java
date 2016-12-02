package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.UserHomeDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  1. 评论回复 post
 *  2. 话题详情下的评论列表
 *  3. 删除评论
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/comment")
public class CommentController {

    public static final int TOPIC_COMMENT_PAGESIZE = 20;//话题列表中显示回复的数量

    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    private static final int ADMIN = 1;

    /**
     * 评论回复
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> publish(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer topicId = reader.readInteger("topicId", false);
        String content = reader.readString("content", false);
        Integer referCommentId = reader.readDefaultInteger("referCommentId", 0);

        CommentPublishDto commentPublishDto = new CommentPublishDto();
        commentPublishDto.setTopicId(topicId);
        commentPublishDto.setReferCommentId(referCommentId);
        commentPublishDto.setContent(content);
        commentPublishDto.setUid(uid);

        Comment comment = commentService.publishComment(commentPublishDto);
        Map<String, Object> info = new HashMap<>();
        if (comment != null){
            info.put("commentId", comment.getId());
            info.put("floor", comment.getFloor());
            rt.setData(info);
            rt.setMsg("回复成功");
        }else {
            rt.setCode(2040);
            rt.setData(null);
            rt.setMsg("回复失败!");
        }
        return rt;
    }

    /**
     * 话题详情下的评论列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonListResponseEntity<CommentListDto> list(@RequestParam Integer topicId,
                                               @RequestParam(defaultValue = "0", required = false) Integer floor,
                                               @RequestParam(defaultValue = "0", required = false) Integer getOwnerReply,
                                               @RequestParam(defaultValue = "1", required = false) Integer page){
        //根据楼层请求的时候 page是计算出来的
        if (floor > 0){
            page = (floor-1)/TOPIC_COMMENT_PAGESIZE + 1;
        }
        //最后一页
        JsonListResponseEntity<CommentListDto> responseEntity = new JsonListResponseEntity();
        int totalPage = 1;
        if (getOwnerReply == 1){
            int commentCount = topicService.getOwnerCommentCount(topicId);
            totalPage =  (commentCount-1)/TOPIC_COMMENT_PAGESIZE + 1;
            page = page == -1 ? totalPage : page;
            List<CommentListDto> commentListDtos = this.commentService.getTopicOwnerCommentsList(topicId, page, TOPIC_COMMENT_PAGESIZE);
            responseEntity.setContent(commentListDtos, totalPage > page, null, String.valueOf(page+1));
        }else {
            int commentCount = topicService.getCommentCount(topicId);
            totalPage =  (commentCount-1)/TOPIC_COMMENT_PAGESIZE + 1;
            page = page == -1 ? totalPage : page;
            List<CommentListDto> commentListDtos = this.commentService.getCommentListByTopicId(topicId, page, TOPIC_COMMENT_PAGESIZE);
            responseEntity.setContent(commentListDtos, totalPage > page, null, String.valueOf(page+1));
        }

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("totalPage", totalPage);
        pageInfo.put("page", page);
        responseEntity.setExtras(pageInfo);
        return responseEntity;
    }


    /**
     * 删除评论
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResponseEntity deleteComment(@RequestParam Integer commentId, @RequestParam String uid){
        RegisterInfo account = userService.getOneNotNull(uid);
        if(account!=null){
            int isAdmin = account.getIsBBsAdmin();
            if(isAdmin != ADMIN)
                throw new CommonException(1000, "您当前没有权限删除该评论");
        }
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        List<Integer> idList = Lists.newArrayList();
        idList.add(commentId);
        commentService.delCommonByIds(idList);
        BbsMsgHandler.adminDelComment(uid, commentId);
        responseEntity.setMsg("删除成功");
        return responseEntity;
    }

}
