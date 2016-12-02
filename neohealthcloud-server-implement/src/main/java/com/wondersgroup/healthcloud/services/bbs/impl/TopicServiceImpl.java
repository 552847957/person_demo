package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.utils.ArraysUtil;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.*;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.CircleService;
import com.wondersgroup.healthcloud.services.bbs.TopicService;
import com.wondersgroup.healthcloud.services.bbs.TopicVoteService;
import com.wondersgroup.healthcloud.services.bbs.UserBbsService;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.VoteInfoDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicPublishDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicTopListDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.TopicViewDto;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("topicService")
public class TopicServiceImpl implements TopicService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TopicContentRepository topicContentRepository;

    @Autowired
    private TopicVoteService voteService;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteItemRepository voteItemRepository;

    @Autowired
    private CircleRepository circleRepository;

    @Autowired
    private UserBbsService userBbsService;

    @Autowired
    private UserService userService;

    @Autowired
    private CircleService circleService;

    @Override
    public List<TopicTopListDto> getCircleTopRecommendTopics(Integer circleId, Integer getNum) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setIsTop(1);
        searchCriteria.setCircleId(circleId);
        searchCriteria.setPageSize(getNum);
        searchCriteria.setOrderInfo("topic.top_rank desc, topic.update_time desc");
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        List<TopicTopListDto> rtList = new ArrayList<>();
        if (topics != null){
            for (Topic topic : topics){
                rtList.add(new TopicTopListDto(topic));
            }
        }
        return rtList;
    }

    @Override
    public List<TopicListDto> getCircleTopicListByTab(Integer circleId, Integer tabId, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setPage(page);
        searchCriteria.setPageSize(pageSize);
        searchCriteria.setOrderInfo("topic.last_comment_time desc");
        searchCriteria.setCircleId(circleId);
        searchCriteria.setGetMoreOne(true);
        if (tabId == -1){
            searchCriteria.setIsBest(1);
        }else {
            searchCriteria.setTabId(tabId);
        }
        if (tabId == 0){
            searchCriteria.setIsTop(0);//全部标签下 不显示置顶的
        }
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getCircleBestRecommendTopics(Integer circleId, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setPage(page);
        searchCriteria.setPageSize(pageSize);
        searchCriteria.setOrderInfo("topic.last_comment_time desc");
        searchCriteria.setIsBest(1);
        searchCriteria.setGetMoreOne(true);
        searchCriteria.setCircleId(circleId);
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getBestRecommendTopicsForUser(String uid, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setPage(page);
        searchCriteria.setPageSize(pageSize);
        searchCriteria.setOrderInfo("topic.last_comment_time desc");
        searchCriteria.setIsBest(1);
        searchCriteria.setGetMoreOne(true);
        List<Circle> userCircles = userBbsService.getUserJoinedCircles(uid);
        if (userCircles != null && !userCircles.isEmpty()){
            List<Integer> joinCircleIds = new ArrayList<>();
            for (Circle circle : userCircles){
                joinCircleIds.add(circle.getId());
            }
            searchCriteria.setCircleIds(joinCircleIds);
        }
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getHotRecommendTopics(String uid, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setPage(page);
        searchCriteria.setPageSize(pageSize);
        searchCriteria.setOrderInfo("topic.score desc, topic.last_comment_time desc");
        searchCriteria.setGetMoreOne(true);
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getTopicsByUid(String uid, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setUid(uid);
        searchCriteria.setOrderInfo("topic.create_time desc");
        searchCriteria.setPage(page);
        searchCriteria.setGetMoreOne(true);
        searchCriteria.setPageSize(pageSize);
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getTopicsByIds(Iterable<Integer> topicIds) {
        List<Topic> topics = topicRepository.findAll(topicIds);
        return this.buildListDto(topics);
    }

    /**
     * 拼装话题列表需要的字段
     */
    private List<TopicListDto> buildListDto(List<Topic> topics){
        if (topics == null || topics.isEmpty()){
            return null;
        }
        Set<String> uids = new HashSet<>();
        Set<Integer> circleIds = new HashSet<>();
        for (Topic topic : topics) {
            uids.add(topic.getUid());
            circleIds.add(topic.getCircleId());
        }

        //圈子信息
        List<Circle> circles = circleRepository.findAll(circleIds);
        Map<Integer, Circle> circleMap = new HashMap<>();
        for (Circle circle : circles){
            circleMap.put(circle.getId(), circle);
        }
        //用户以及小孩信息
        Map<String, RegisterInfo> userInfos = userService.findByUids(uids);

        List<TopicListDto> listDtos = new ArrayList<>();
        for (Topic topic : topics) {
            TopicListDto listDto = new TopicListDto(topic);
            if (userInfos.containsKey(topic.getUid())){
                RegisterInfo userInfo = userInfos.get(topic.getUid());
                listDto.mergeUserInfo(userInfo);
            }
            if (circleMap.containsKey(topic.getCircleId())){
                listDto.setCircleName(circleMap.get(topic.getCircleId()).getName());
            }
            listDtos.add(listDto);
        }

        return listDtos;
    }

    @Override
    public TopicViewDto getTopicView(Integer topicId) {
        Topic topic = topicRepository.findOne(topicId);
        if (null == topic || TopicConstant.Status.isDelStatus(topic.getStatus())){
            throw TopicException.notExist();
        }
        RegisterInfo userInfo = userService.getOneNotNull(topic.getUid());
        if (userInfo.getBanStatus().intValue() == UserConstant.BanStatus.FOREVER){
            throw TopicException.notExist();
        }
        Circle circle = circleRepository.findOne(topic.getCircleId());
        if (circle == null){
            throw TopicException.notExist();
        }
        List<TopicContent> topicContents = topicContentRepository.findContentsByTopicId(topicId);
        TopicViewDto topicViewDto = new TopicViewDto();
        topicViewDto.mergeTopicInfo(topic, topicContents);
        topicViewDto.mergeCircleInfo(circle);
        if (1 == topic.getIsVote()){
            VoteInfoDto voteInfoDto = voteService.getVoteInfoByTopicId(topicId);
            topicViewDto.setVoteInfo(voteInfoDto);
        }
        topicViewDto.mergeUserInfo(userInfo);
        //pv+1
        topicRepository.incTopicPv(topicId);
        String pvKey = "bbs_topic_pv_"+topicId;
        try(Jedis jedis = jedisPool.getResource()){
            String day = DateUtils.sdf_day.format(new Date());
            if (jedis.hexists(pvKey, day)){
                jedis.hincrBy(pvKey, day, 1);
            }else {
                jedis.hset(pvKey, day, "1");
            }
        }
        return topicViewDto;
    }

    @Override
    @Transactional
    public int publishTopic(TopicPublishDto publishInfo) {
        if (StringUtils.isEmpty(publishInfo.getUid())) {
            throw new TopicException(2001, "uid非空");
        }
        List<TopicPublishDto.TopicContent> contents = publishInfo.getTopicContents();
        if (contents == null || contents.isEmpty()) {
            throw new TopicException(2002, "帖子无效");
        }
        Circle circle = circleRepository.findOne(publishInfo.getCirclrId());
        if (null == circle || circle.getDelflag().equals("1")) {
            throw new TopicException(2003, "圈子无效");
        }
        UserCircle userCircle = circleService.queryByUIdAndCircleIdAndDelFlag(publishInfo.getUid(), publishInfo.getCirclrId(), "0");
        if (null == userCircle){
            throw new TopicException(2013, "需要加入该圈子才能发布话题哦");
        }
        RegisterInfo account = userService.getOneNotNull(publishInfo.getUid());
        if (account.getBanStatus() != UserConstant.BanStatus.OK){
            throw new CommonException(2014, "禁言状态无法发表话题哦");
        }
        int contentCount = contents.size();
        Topic topic = this.saveTopic(publishInfo);
        //保存详情
        List<String> allImgs = new ArrayList<>();
        List<TopicContent> topicContents = new ArrayList<>();
        for (TopicPublishDto.TopicContent contentTmp : contents) {
            List<String> imgs = contentTmp.getImgs();
            String imgsStr = "";
            if (imgs != null && !imgs.isEmpty()) {
                allImgs.addAll(imgs);
                imgsStr = ArraysUtil.split2Sting(imgs, ",");
            }
            topicContents.add(new TopicContent(topic.getId(), contentTmp.getContent(), imgsStr));
        }
        topicContentRepository.save(topicContents);

        //保存投票信息
        Boolean isVote = this.saveTopicVote(topic.getId(), publishInfo.getVoteItems());

        //同步一些信息到主表topic里面,为了列表显示
        if (contentCount > 1 || isVote) {
            if (contentCount > 1) {
                Integer imgCount = allImgs.size();
                allImgs = allImgs.size() > 3 ? allImgs.subList(0, 3) : allImgs;
                String listImgsStr = ArraysUtil.split2Sting(allImgs, ",");
                topic.setImgs(listImgsStr);
                topic.setImgCount(imgCount);
            }
            topic.setIsVote(isVote ? 1 : 0);
            topicRepository.save(topic);
        }
        circle.setTopicCount(circle.getTopicCount() + 1);
        circleRepository.save(circle);
        //lts
        //BbsMsgHandler.publishTopic(topic.getUid(), topic.getId());
        return topic.getId();
    }

    @Override
    public int getCommentCount(Integer topicId) {
        Topic topic = topicRepository.findOne(topicId);
        return topic == null ? 0 : topic.getCommentCount();
    }

    @Override
    public int getOwnerCommentCount(Integer topicId) {
        Integer count = commentRepository.getTopicOwnerReplyCount(topicId);
        return count == null ? 0 : count;
    }

    private Topic saveTopic(TopicPublishDto publishInfo) {
        TopicPublishDto.TopicContent firstContent = publishInfo.getTopicContents().get(0);
        String intro;
        if (firstContent.getContent().length() > 50) {
            intro = firstContent.getContent().substring(0, 50);
        } else {
            intro = firstContent.getContent();
        }
        String topicImgs = "";
        if (null != firstContent.getImgs() && !firstContent.getImgs().isEmpty()){
            List<String> listImgs = firstContent.getImgs().size() > 3 ? firstContent.getImgs().subList(0, 3) : firstContent.getImgs();
            topicImgs = ArraysUtil.split2Sting(listImgs, ",");
        }
        Integer topicImgsCount = null == firstContent.getImgs() ? 0 : firstContent.getImgs().size();
        Topic topic = new Topic();
        topic.setTitle(publishInfo.getTitle());
        topic.setUid(publishInfo.getUid());
        topic.setCircleId(publishInfo.getCirclrId());
        topic.setStatus(TopicConstant.Status.OK);
        topic.setIntro(intro);
        topic.setImgs(topicImgs);
        topic.setImgCount(topicImgsCount);
        topic.setIsBest(0);
        topic.setIsVote(0);
        Date nowTime = new Date();
        topic.setCreateTime(nowTime);
        topic.setUpdateTime(nowTime);
        topic.setLastCommentTime(nowTime);
        topic = topicRepository.save(topic);
        if (topic.getId() == null) {
            throw new TopicException(2001, "保存失败");
        }
        return topic;
    }

    private Boolean saveTopicVote(Integer topicId, List<String> voteItems) {
        List<VoteItem> voteItemModels = new ArrayList<>();
        Boolean isVote = false;
        if (null != voteItems && !voteItems.isEmpty()) {
            Vote vote = new Vote(topicId);
            vote = voteRepository.save(vote);
            if (vote.getId() != null) {
                for (String voteItem : voteItems) {
                    voteItemModels.add(new VoteItem(vote.getId(), voteItem));
                }
            }
            voteItemRepository.save(voteItemModels);
            isVote = true;
        }
        return isVote;
    }

    /**
     * 使用jdbc查询
     * 对topic查询的简单封装
     * 支持TopicSearchCriteria条件的各种组合查询
     */
    private List<Topic> searchTopicByCriteria(TopicSearchCriteria searchCriteria){
        StringBuffer querySql = new StringBuffer("select topic.* from tb_bbs_topic topic ");
        querySql.append(" left join tb_account_user user on user.id = topic.uid ");
        if (searchCriteria.getTabId() > 0){
            querySql.append(" left join tb_bbs_topic_tab_map tab on tab.topic_id = topic.id ");
        }
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        if (StringUtils.isEmpty(searchCriteria.getOrderInfo())){
            querySql.append(" order by topic.create_time desc ");
        }else {
            querySql.append(" order by " + searchCriteria.getOrderInfo());
        }
        if (searchCriteria.getPageSize() > 0){
            querySql.append(" limit ?,? ");
            elelmentType.add((searchCriteria.getPage()-1)*searchCriteria.getPageSize());
            if (searchCriteria.getGetMoreOne()){
                elelmentType.add(searchCriteria.getPageSize()+1);
            }else {
                elelmentType.add(searchCriteria.getPageSize());
            }
        }
        System.out.println(querySql.toString());
        List<Topic> list = jdbcTemplate.query(querySql.toString(), elelmentType.toArray(), new BeanPropertyRowMapper(Topic.class));
        return list;
    }

}
