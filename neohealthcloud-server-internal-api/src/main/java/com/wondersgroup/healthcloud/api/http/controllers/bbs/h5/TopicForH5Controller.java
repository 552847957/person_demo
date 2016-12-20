package com.wondersgroup.healthcloud.api.http.controllers.bbs.h5;

import com.wondersgroup.healthcloud.api.http.dto.bbs.TopicH5ViewDto;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.utils.BeanUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.AdminVestUser;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Comment;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.repository.bbs.AdminVestUserRepository;
import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.CommentService;
import com.wondersgroup.healthcloud.services.bbs.TopicService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.CommentPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicDetailDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicSettingDto;
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
 * 话题h5页面
 * @author ys 2016-12-09
 */
@RestController
@RequestMapping("/api/bbs/topic")
public class TopicForH5Controller {

    @Autowired
    private TopicService topicService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BadWordsService badWordsService;

    @RequestMapping(value = "/viewForH5", method = RequestMethod.GET)
    public JsonResponseEntity<TopicH5ViewDto> viewForH5(@RequestParam Integer topicId,
                                                        @RequestParam(defaultValue = "", required = false) String uid){
        JsonResponseEntity<TopicH5ViewDto> responseEntity = new JsonResponseEntity<>();
        TopicDetailDto detailInfo = topicService.getTopicDetailInfo(topicId);
        TopicH5ViewDto viewDto = new TopicH5ViewDto(detailInfo);
        //默认头像
        if (StringUtils.isEmpty(viewDto.getAvatar())){
            viewDto.setAvatar("http://img.wdjky.com/1482215959386?imageView2/1/w/300/h/300");
        }
        viewDto.dealBadWords(badWordsService);
        //pv+1
        topicService.incTopicPv(topicId);
        responseEntity.setData(viewDto);
        return responseEntity;
    }

    @RequestMapping(value = "/commentsForH5", method = RequestMethod.GET)
    public JsonListResponseEntity<CommentListDto> commentsForH5(@RequestParam Integer topicId,
                                                                @RequestParam(defaultValue = "1", required = false) Integer page){
        JsonListResponseEntity<CommentListDto> responseEntity = new JsonListResponseEntity();
        List<CommentListDto> commentListDtos = this.commentService.getCommentListByTopicId(topicId, page, 3);
        if (badWordsService.isDealBadWords() && commentListDtos != null){
            //违禁词屏蔽
            for (CommentListDto commentListDto : commentListDtos){
                commentListDto.setContent(badWordsService.dealBadWords(commentListDto.getContent()));
                if (commentListDto.getReferCommentInfo() != null){
                    commentListDto.getReferCommentInfo().setContent(badWordsService.dealBadWords(commentListDto.getReferCommentInfo().getContent()));
                }
            }
        }
        responseEntity.setContent(commentListDtos);
        return responseEntity;
    }
}
