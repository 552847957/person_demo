package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.BeanUtils;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.AdminVestUser;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.repository.bbs.AdminVestUserRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.TopicService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicSettingDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicViewDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ys on 16/8/18.
 * 话题标签
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/topic")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger("TopicController");

    @Autowired
    private TopicService topicService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BbsAdminService bbsAdminService;

    @Autowired
    private AdminVestUserRepository adminVestUserRepository;

    @Admin
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Pager list(@RequestBody Pager pager, @RequestHeader String appUid, @RequestHeader String userid){
        Map<String, Object> parms = pager.getParameter();
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria(parms);
        if (searchCriteria.getIs_mine()){
            List<String> adminIds = new ArrayList<>();
            adminIds.add(appUid);
            List<String> vestUids = bbsAdminService.getAdminVestUidsByAdminUid(appUid);
            if (vestUids != null){
                adminIds.addAll(vestUids);
            }
            searchCriteria.setUids(adminIds);
        }
        searchCriteria.setPage(pager.getNumber());
        searchCriteria.setPageSize(pager.getSize());
        searchCriteria.setOrderInfo("topic.create_time desc");
        int totalSize = topicService.countTopicByCriteria(searchCriteria);

        List<AdminVestUser> adminAppUsers = adminVestUserRepository.getVestUsersByAdminUid(appUid);
        List<String> vestUids = new ArrayList<>();
        vestUids.add(appUid);
        if (adminAppUsers != null && !adminAppUsers.isEmpty()) {
            for (AdminVestUser appUser : adminAppUsers) {
                vestUids.add(appUser.getVest_uid());
            }
        }
        List<Map<String, Object>> list= topicService.getTopicListByCriteria(searchCriteria);
        if (null != list && !list.isEmpty()){
            for (Map<String, Object> map : list){
                map.put("is_mine", vestUids.contains(map.get("uid").toString()) ? 1 :0);
            }
        }
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }


    @Admin
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> publish(@RequestBody TopicPublishDto topicPublishDto){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();
        Integer circleId = topicPublishDto.getCircleId();
        if (null == circleId || circleId == 0){
            throw new RuntimeException("圈子无效");
        }
        int topicId = topicService.publishTopic(topicPublishDto);
        Map<String, Object> info = new HashMap<>();
        if (topicId > 0){
            info.put("topicId", topicId);
            rt.setData(info);
            rt.setMsg("发布成功");
        }else {
            rt.setCode(2040);
            rt.setData(null);
            rt.setMsg("发布失败!");
        }
        return rt;
    }

    @Admin
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public JsonResponseEntity<Object> delete(@RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<Object> entity = new JsonResponseEntity();
        Topic topic = topicService.delTopic(appUid, id);
        entity.setMsg(topic.getStatus().intValue() == TopicConstant.Status.ADMIN_DELETE ? "删除成功" : "恢复话题成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> setting(@RequestHeader String appUid, @RequestBody TopicSettingDto settingDto){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();
        Integer circleId = settingDto.getCircleId();
        if (null == circleId || circleId == 0){
            throw new RuntimeException("圈子无效");
        }
        int topicId = topicService.settingTopic(settingDto);
        Map<String, Object> info = new HashMap<>();
        info.put("topicId", topicId);
        rt.setData(info);
        rt.setMsg("设置成功");
        return rt;
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

        CommentPublishDto commentPublishDto = new CommentPublishDto();
        commentPublishDto.setTopicId(topicId);
        commentPublishDto.setReferCommentId(referCommentId);
        commentPublishDto.setContent(content);
        commentPublishDto.setUid(uid);

        Comment comment = commentService.publishComment(commentPublishDto);
        Map<String, Object> info = new HashMap<>();
        if (comment == null){
            entity.setCode(2040);
            entity.setData(null);
            entity.setMsg("回复失败!");
            return entity;
        }
        info.put("commentId", comment.getId());
        info.put("floor", comment.getFloor());
        entity.setData(info);
        entity.setMsg("回复成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/settingView", method = RequestMethod.GET)
    public JsonResponseEntity<Map<String, Object>> settingView(@RequestHeader String userid, @RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<Map<String, Object>> entity = new JsonResponseEntity();
        TopicViewDto view = topicService.getTopicView(id);
        Map<String, Object> info = BeanUtils.beanToMap(view);
        StringBuffer contents = new StringBuffer();
        if (view.getTopicContents() != null && !view.getTopicContents().isEmpty()){
            for(TopicViewDto.TopicContentInfo contentInfo : view.getTopicContents()){
                if (StringUtils.isNotEmpty(contentInfo.getContent())){
                    contents.append(contentInfo.getContent() + "\n");
                }
            }
        }
        info.put("contents", contents);
        List<Map<String, Object>> comments = commentService.getCommentListByAdminAppUid(id, appUid, userid);
        info.put("comments", comments);
        entity.setData(info);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public JsonResponseEntity<TopicViewDto> view(@RequestHeader String appUid, @RequestHeader String userid, @RequestParam Integer id){
        JsonResponseEntity<TopicViewDto> entity = new JsonResponseEntity();

        Topic topic = topicService.infoTopic(id);
        if (topic == null){
            throw new RuntimeException("话题无效");
        }

        List<String> adminAppUids = new ArrayList<>();
        adminAppUids.add(appUid);
        List<AdminVestUser> adminAppUsers = adminVestUserRepository.getVestUsersByAdminUid(appUid);
        if (adminAppUsers != null && !adminAppUsers.isEmpty()){
            for (AdminVestUser appUser : adminAppUsers){
                adminAppUids.add(appUser.getVest_uid());
            }
            if (!adminAppUids.contains(topic.getUid())){
                entity.setCode(1200);
                entity.setMsg("只能编辑自己以及小号发的话题");
                return entity;
            }
        }

        TopicViewDto view = topicService.getTopicView(id);
        entity.setCode(0);
        entity.setData(view);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/getTopicTitleById", method = RequestMethod.GET)
    public JsonResponseEntity getTopicTitleById(@RequestParam String topicId) {
        JsonResponseEntity jsonResponseEntity = new JsonResponseEntity();
        try {
            Integer topicIdInt = Integer.parseInt(topicId);
            if (topicId == null) {
                // 输入为空，不进行提示
                jsonResponseEntity.setData("");
                return jsonResponseEntity;
            }
            Topic topic = topicService.infoTopic(topicIdInt);
            if (topic != null && StringUtils.isNotBlank(topic.getTitle())) {
                jsonResponseEntity.setData(topic.getTitle());
                return jsonResponseEntity;
            } else {
                jsonResponseEntity.setCode(1001);
                jsonResponseEntity.setMsg("未查询到话题标题");
            }
        } catch (Exception e) {
            logger.error("根据话题id查询话题标题出错", e);
            jsonResponseEntity.setCode(1001);
            jsonResponseEntity.setMsg("您的输入有误");
        }
        return jsonResponseEntity;
    }
}
