package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.criteria.CommentSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ys on 16/8/19
 * 圈子评论
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BbsAdminService bbsAdminService;

    @Admin
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResponseEntity<Object> delete(@RequestHeader String appUid, @RequestParam List<Integer> ids){
        JsonResponseEntity<Object> entity = new JsonResponseEntity();
        Boolean delOk = commentService.delCommonByIds(appUid, ids);
        entity.setMsg("删除成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Pager list(@RequestBody Pager pager, @RequestHeader String appUid, @RequestHeader String userid){
        Map<String, Object> parms = pager.getParameter();
        CommentSearchCriteria searchCriteria = new CommentSearchCriteria(parms);
        if (searchCriteria.getIsMine()){
            List<String> adminIds = new ArrayList<>();
            List<String> vestUids = bbsAdminService.getAdminVestUidsByAdminUid(appUid);
            if (vestUids != null){
                adminIds.addAll(vestUids);
            }
            adminIds.add(appUid);
            searchCriteria.setUids(adminIds);
        }
        searchCriteria.setPage(pager.getNumber());
        searchCriteria.setPageSize(pager.getSize());
        searchCriteria.setOrderInfo("`comment`.create_time desc");
        int totalSize = commentService.countCommentByCriteria(searchCriteria);
        List<Map<String, Object>> list= commentService.getCommentListByCriteria(searchCriteria);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    /**
     * 根据id查询评论详情
     * created by : limenghua
     * @param id
     * @return
     */
    @Admin
    @RequestMapping(value="/getCommentInfoById",method = RequestMethod.GET)
    public JsonResponseEntity getCommentInfoById(@RequestParam Integer id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        Map<String, Object> info = commentService.getCommentInfoById(id);
        if (info != null) {
            jsonResponseEntity.setData(info);
        } else {
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg("未查询到话题数据");
        }
        return jsonResponseEntity;
    }

    /**
     * 根据id查询评论详情
     * created by : ys
     * @param id
     * @return
     */
    @Admin
    @RequestMapping(value="/getCommentInfoByIdWithReplys",method = RequestMethod.GET)
    public JsonResponseEntity getCommentInfoByIdWithReplys(@RequestParam Integer id) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        Map<String, Object> info = commentService.getCommentInfoByIdWithReplys(id);
        if (info != null) {
            jsonResponseEntity.setData(info);
        } else {
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg("未查询到话题数据");
        }
        return jsonResponseEntity;
    }


    /**
     * 回复评论
     * created by : ys
     */
    @Admin
    @RequestMapping(value="/replyComment",method = RequestMethod.POST)
    public JsonResponseEntity replyComment(@RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String content = reader.readString("content", false);
        String uid = reader.readString("uid", false);

        Integer topicId = reader.readInteger("topicId", false);
        Integer referCommentId = reader.readDefaultInteger("referCommentId", 0);

        Comment referComment = commentService.findOne(referCommentId);
        if (null == referComment){
            entity.setCode(1001);
            entity.setMsg(String.format("评论id[%s]不存在", referCommentId));
            return entity;
        }

        CommentPublishDto commentPublishDto = new CommentPublishDto();
        commentPublishDto.setTopicId(topicId);
        commentPublishDto.setReferCommentId(referCommentId);
        commentPublishDto.setContent(content);
        commentPublishDto.setUid(uid);

        Comment comment = commentService.publishComment(commentPublishDto);
        if (null == comment){
            throw new CommonException(2040, "回复失败!");
        }
        Map<String, Object> info = new HashMap<>();
        info.put("commentId", comment.getId());
        info.put("floor", comment.getFloor());
        entity.setData(info);
        entity.setMsg("回复成功");
        return entity;
    }


    /**
     * 修改评论：只修改内容
     * created by : limenghua
     * @param request
     * @return
     */
    @Admin
    @RequestMapping(value="/modifyComment",method = RequestMethod.POST)
    public JsonResponseEntity modifyComment(@RequestBody String request) {
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer id = reader.readInteger("id", false);
        String content = reader.readString("content", false);

        Comment exist = commentService.findOne(id);
        if (null == exist){
            throw new CommonException(2021, "评论id不存在");
        }
        exist.setContent(content);
        commentService.saveComment(exist);
        entity.setMsg("修改成功");
        return entity;
    }
}
