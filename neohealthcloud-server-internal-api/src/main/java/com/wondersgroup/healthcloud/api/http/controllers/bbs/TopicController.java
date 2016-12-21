package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.http.dto.bbs.TopicViewDto;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.BeanUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.*;
import com.wondersgroup.healthcloud.jpa.repository.bbs.AdminVestUserRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.TopicService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.*;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
@Admin
@RestController
@RequestMapping("/api/bbs/topic")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger("TopicController");

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public Pager list(@RequestBody Pager pager, @RequestHeader String appUid){
        Map<String, Object> parms = pager.getParameter();
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria(parms);
        if (searchCriteria.getIsMine()){
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
        searchCriteria.setStatusNotIn(new Integer[]{TopicConstant.Status.USER_DELETE, TopicConstant.Status.WAIT_VERIFY});
        int totalSize = topicService.countTopicByCriteria(searchCriteria);

        List<AdminVestUser> adminAppUsers = adminVestUserRepository.getVestUsersByAdminUid(appUid);
        List<String> vestUids = new ArrayList<>();
        vestUids.add(appUid);
        if (adminAppUsers != null && !adminAppUsers.isEmpty()) {
            for (AdminVestUser appUser : adminAppUsers) {
                vestUids.add(appUser.getVest_uid());
            }
        }
        List<Map<String, Object>> list= this.getTopicListByCriteria(searchCriteria);
        if (null != list && !list.isEmpty()){
            for (Map<String, Object> map : list){
                map.put("is_mine", vestUids.contains(map.get("uid").toString()) ? 1 :0);
            }
        }
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    private List<Map<String, Object>> getTopicListByCriteria(TopicSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select topic.*,circle.name as circle_name, user.nickname from tb_bbs_topic topic ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=topic.uid ");
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" where " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Admin
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> publish(@RequestHeader String appUid, @RequestBody TopicPublishDto topicPublishDto){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();
        Integer circleId = topicPublishDto.getCircleId();
        if (null == circleId || circleId == 0){
            throw new RuntimeException("圈子无效");
        }
        topicPublishDto.setIsAdminPublish(true);
        Topic topic = topicService.publishTopic(topicPublishDto);
        Map<String, Object> info = new HashMap<>();
        if (topic.getId() > 0){
            info.put("topicId", topic.getId());
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
        entity.setMsg(topic.getStatus() == TopicConstant.Status.ADMIN_DELETE ? "删除成功" : "恢复话题成功");
        return entity;
    }

    @Admin
    @RequestMapping(value = "/verifyUnPass", method = RequestMethod.POST)
    public JsonResponseEntity<Object> verifyUnPass(@RequestHeader String appUid, @RequestParam List<Integer> ids){
        JsonResponseEntity<Object> entity = new JsonResponseEntity();
        topicService.verifyUnPass(ids);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/verifyPass", method = RequestMethod.POST)
    public JsonResponseEntity<Object> verifyPass(@RequestHeader String appUid, @RequestParam List<Integer> ids){
        JsonResponseEntity<Object> entity = new JsonResponseEntity();
        topicService.verifyPass(ids);
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
        TopicDetailDto view = topicService.getTopicDetailInfo(id);
        Map<String, Object> info = BeanUtils.beanToMap(view);
        StringBuffer contents = new StringBuffer();
        if (view.getTopicContents() != null && !view.getTopicContents().isEmpty()){
            for(TopicDetailDto.TopicContentInfo contentInfo : view.getTopicContents()){
                if (StringUtils.isNotEmpty(contentInfo.getContent())){
                    contents.append(contentInfo.getContent() + "\n");
                }
            }
        }
        info.put("contents", contents);
        List<Map<String, Object>> comments = commentService.getCommentListByAdminAppUid(id, appUid);
        info.put("comments", comments);
        entity.setData(info);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public JsonResponseEntity<TopicViewDto> view(@RequestHeader String appUid, @RequestParam Integer id){
        JsonResponseEntity<TopicViewDto> entity = new JsonResponseEntity();

        TopicDetailDto detailInfo = topicService.getTopicDetailInfo(id);
        TopicViewDto viewDto = new TopicViewDto(detailInfo);
        List<String> adminAppUids = new ArrayList<>();
        adminAppUids.add(appUid);
        List<AdminVestUser> adminAppUsers = adminVestUserRepository.getVestUsersByAdminUid(appUid);
        if (adminAppUsers != null && !adminAppUsers.isEmpty()){
            for (AdminVestUser appUser : adminAppUsers){
                adminAppUids.add(appUser.getVest_uid());
            }
            if (!adminAppUids.contains(viewDto.getUid())){
                entity.setCode(1200);
                entity.setMsg("只能编辑自己以及小号发的话题");
                return entity;
            }
        }
        entity.setCode(0);
        entity.setData(viewDto);
        return entity;
    }

    @Admin
    @RequestMapping(value = "/getTopicTitleById", method = RequestMethod.GET)
    public JsonResponseEntity getTopicTitleById(@RequestParam Integer topicId) {
        JsonResponseEntity entity = new JsonResponseEntity();
        Topic topic = topicService.infoTopic(topicId);
        if (null == topic || StringUtils.isEmpty(topic.getTitle())){
            throw new CommonException(2021, "未查询到话题标题");
        }
        entity.setData(topic.getTitle());
        return entity;
    }
}
