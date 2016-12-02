package com.wondersgroup.healthcloud.services.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;

import java.util.List;

/**
 * Created by ys on 2016/6/13.
 * 话题评论信息
 */
public interface CommentService {

    /**
     * 获取话题下面的评论列表
     */
    List<CommentListDto> getCommentListByTopicId(Integer topicId, Integer page, Integer pageSize);

    /**
     * 通过楼层获取 当前楼层所在的评论列表
     */
    List<CommentListDto> getCommentListByFloor(Integer topicId, Integer floor, Integer pageSize);

    /**
     * 获取用户发表的评论列表
     * 返回数量 = pageSize+1
     */
    List<CommentListDto> getUserCommentsList(String uid, Integer page, Integer pageSize);

    /**
     * 获取当前话题中 楼主的回复列表
     */
    List<CommentListDto> getTopicOwnerCommentsList(Integer topicId, Integer page, Integer pageSize);

    Comment publishComment(CommentPublishDto publishDto);

    Boolean delCommonByIds(Iterable<Integer> ids);

}
