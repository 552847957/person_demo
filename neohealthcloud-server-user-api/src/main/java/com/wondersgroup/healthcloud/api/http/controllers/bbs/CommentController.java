package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    private BadWordsService badWordsService;

    /**
     * 评论回复
     */
    @VersionRange
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
    @VersionRange
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
        List<CommentListDto> commentListDtos;
        if (getOwnerReply == 1){
            int commentCount = topicService.getOwnerCommentCount(topicId);
            totalPage =  (commentCount-1)/TOPIC_COMMENT_PAGESIZE + 1;
            page = page == -1 ? totalPage : page;
            commentListDtos = this.commentService.getTopicOwnerCommentsList(topicId, page, TOPIC_COMMENT_PAGESIZE);
            responseEntity.setContent(commentListDtos, totalPage > page, null, String.valueOf(page+1));
        }else {
            int commentCount = topicService.getCommentCount(topicId);
            totalPage =  (commentCount-1)/TOPIC_COMMENT_PAGESIZE + 1;
            page = page == -1 ? totalPage : page;
            commentListDtos = this.commentService.getCommentListByTopicId(topicId, page, TOPIC_COMMENT_PAGESIZE);
        }
        if (badWordsService.isDealBadWords() && commentListDtos != null){
            //违禁词屏蔽
            for (CommentListDto commentListDto : commentListDtos){
                commentListDto.setContent(badWordsService.dealBadWords(commentListDto.getContent()));
                if (commentListDto.getReferCommentInfo() != null){
                    commentListDto.getReferCommentInfo().setContent(badWordsService.dealBadWords(commentListDto.getReferCommentInfo().getContent()));
                }
            }
        }
        responseEntity.setContent(commentListDtos, totalPage > page, null, String.valueOf(page+1));

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("totalPage", totalPage);
        pageInfo.put("page", page);
        responseEntity.setExtras(pageInfo);
        return responseEntity;
    }


    /**
     * 删除评论
     */
    @VersionRange
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResponseEntity deleteComment(@RequestParam Integer commentId, @RequestParam String uid){
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        commentService.delCommonById(uid, commentId);
        responseEntity.setMsg("删除成功");
        return responseEntity;
    }

}
