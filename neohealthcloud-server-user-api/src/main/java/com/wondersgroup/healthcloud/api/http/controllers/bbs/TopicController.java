package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.api.http.dto.article.ShareH5APIDTO;
import com.wondersgroup.healthcloud.api.http.dto.bbs.TopicViewDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicDetailDto;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  1. 话题发布
 *  2. 圈子下的话题列表(包括全部，以及非精华的请他tab列表)
 *  3. 圈子下的精华tab下的列表
 *  4. 用户圈子首页下面的推荐的话题列表
 *  5. 圈子首页－热门话题列表  - add 2016-10-24
 *  6. 获取帖子详情
 * @author ys
 */
@RestController
@RequestMapping("/api/bbs/topic")
public class TopicController {

    //@Value("${bbs.topic.shareThumb}")
    private String bbsDefaultShareThumb="http://img.wdjky.com/6022087dfdd751471409614922.png?imageView2";

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicCollectService topicCollectService;

    @Autowired
    private TopicVoteService topicVoteService;

    @Autowired
    private FavorService favorService;

    @Autowired
    private UserService userService;


    private final static int pageSize = 10;

    /**
     * 话题发布
     */
    @VersionRange
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public JsonResponseEntity<Map<String, Object>> publish(@RequestBody String request){
        JsonResponseEntity<Map<String, Object>> rt = new JsonResponseEntity();

        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        Integer circleId = reader.readInteger("circleId", false);
        String title = reader.readString("title", false);
        String content = reader.readDefaultString("content", "");
        List<String> imgsList = reader.readObject("imgs", true, ArrayList.class);
        List<String> voteItems = reader.readObject("voteItems", true, ArrayList.class);

        TopicPublishDto topicPublishDto = new TopicPublishDto();
        topicPublishDto.setCircleId(circleId);
        topicPublishDto.setUid(uid);
        topicPublishDto.setTitle(title);
        topicPublishDto.addContent(content, imgsList);
        topicPublishDto.setVoteItems(voteItems);

        int topicId = topicService.publishTopic(topicPublishDto);
        Map<String, Object> info = new HashMap<>();
        if (topicId > 0){
            info.put("topicId", topicId);
            rt.setData(info);
            rt.setMsg("发布成功");
        }else {
            throw new CommonException(2040, "发布失败!");
        }
        return rt;
    }

    /**
     * 圈子下的话题列表(包括全部，以及非精华的请他tab列表)
     */
    @VersionRange
    @RequestMapping(value = "/circle/list", method = RequestMethod.GET)
    public JsonListResponseEntity<TopicListDto> list(@RequestParam Integer circleId,
                                                    @RequestParam(defaultValue="0") Integer tabId,
                                                    @RequestParam(defaultValue = "1", required = false) Integer flag){
        int pageSize = 10;
        JsonListResponseEntity<TopicListDto> rt = new JsonListResponseEntity();
        List<TopicListDto> listInfo;
        if (tabId == -1){
            listInfo = topicService.getCircleBestRecommendTopics(circleId, flag, pageSize);
        }else {
            listInfo = topicService.getCircleTopicListByTab(circleId, tabId, flag, pageSize);
        }
        Boolean hasMore = false;
        if (listInfo != null && listInfo.size() > pageSize){
            listInfo = listInfo.subList(0, pageSize);
            hasMore = true;
        }
        rt.setContent(listInfo, hasMore, null, String.valueOf(flag+1));
        return rt;
    }

    /**
     * 圈子的精华列表
     */
    @VersionRange
    @RequestMapping(value = "/circle/bestList", method = RequestMethod.GET)
    public JsonListResponseEntity<TopicListDto> bestList(@RequestParam Integer circleId,
                                                     @RequestParam(defaultValue = "1", required = false) Integer flag){
        int pageSize = 10;
        JsonListResponseEntity<TopicListDto> rt = new JsonListResponseEntity();
        List<TopicListDto> listInfo = topicService.getCircleBestRecommendTopics(circleId, flag, pageSize);
        Boolean hasMore = false;
        if (listInfo != null && listInfo.size() > pageSize){
            listInfo = listInfo.subList(0, pageSize);
            hasMore = true;
        }
        rt.setContent(listInfo, hasMore, null, String.valueOf(flag+1));
        return rt;
    }

    /**
     * 用户圈子首页下面的推荐的话题列表
     */
    @VersionRange
    @RequestMapping(value = "/user/recommendList", method = RequestMethod.GET)
    public JsonListResponseEntity<TopicListDto> bestList(@RequestParam String uid,
                                                         @RequestParam(defaultValue = "1", required = false) Integer flag){
        int pageSize = 10;
        JsonListResponseEntity<TopicListDto> rt = new JsonListResponseEntity();
        List<TopicListDto> listInfo = topicService.getBestRecommendTopicsForUser(uid, flag, pageSize);
        Boolean hasMore = false;
        if (listInfo != null && listInfo.size() > pageSize){
            listInfo = listInfo.subList(0, pageSize);
            hasMore = true;
        }
        rt.setContent(listInfo, hasMore, null, String.valueOf(flag+1));
        return rt;
    }

    /**
     * 圈子首页－热门话题列表
     */
    @VersionRange
    @RequestMapping(value = "/hotList", method = RequestMethod.GET)
    public JsonListResponseEntity<TopicListDto> hotList(@RequestParam String uid,
                                                         @RequestParam(defaultValue = "1", required = false) Integer flag){
        int pageSize = 20;
        int limit_page_num = 3;//限制热门话题只返回60条数据
        JsonListResponseEntity<TopicListDto> rt = new JsonListResponseEntity();
        List<TopicListDto> listInfo = topicService.getHotRecommendTopics(uid, flag, pageSize);
        Boolean hasMore = false;
        if (listInfo != null && listInfo.size() > pageSize){
            listInfo = listInfo.subList(0, pageSize);
            hasMore = true;
        }
        if (flag == limit_page_num){
            hasMore = false;
        }
        rt.setContent(listInfo, hasMore, null, String.valueOf(flag+1));
        return rt;
    }

    @VersionRange
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public JsonResponseEntity<TopicViewDto> topicView(@RequestParam Integer topicId,
                                                      @RequestParam(defaultValue = "", required = false) String uid){
        JsonResponseEntity<TopicViewDto> responseEntity = new JsonResponseEntity<>();
        TopicDetailDto detailInfo = topicService.getTopicDetailInfo(topicId);

        TopicViewDto viewDto = new TopicViewDto(detailInfo);
        viewDto.setShareInfo(this.getShareInfo(detailInfo));

        if (StringUtils.isNotEmpty(uid)){
            viewDto.setIsCollected(topicCollectService.isCollectedForUser(uid, topicId) ? 1 : 0);
            if (viewDto.getVoteInfo() != null){
                viewDto.setIsVoted(topicVoteService.isVotedForUser(uid, viewDto.getVoteInfo().getVoteId()) ? 1 : 0);
            }
            //用户被禁言 不能回复
            RegisterInfo account = userService.getOneNotNull(uid);
            if (account.getBanStatus().intValue() != UserConstant.BanStatus.OK){
                viewDto.setUserCommentStatus(UserConstant.UserCommentStatus.USER_BAN);
            }
            Boolean isFavor = favorService.isFavorTopic(uid, topicId);
            viewDto.setIsFavor(isFavor ? 1 : 0);
        }

        responseEntity.setData(viewDto);
        return responseEntity;
    }

    private ShareH5APIDTO getShareInfo(TopicDetailDto detailInfo){
        ShareH5APIDTO shareH5APIDTO = new ShareH5APIDTO();
        shareH5APIDTO.setId(detailInfo.getId());
        shareH5APIDTO.setTitle(detailInfo.getTitle());
        shareH5APIDTO.setThumb(this.bbsDefaultShareThumb);
        shareH5APIDTO.setDesc(detailInfo.getIntro());
        shareH5APIDTO.setUrl(appUrlH5Utils.buildBbsTopicView(detailInfo.getId()));
        return shareH5APIDTO;
    }

}
