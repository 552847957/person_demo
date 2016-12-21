package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.utils.ArraysUtil;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.common.utils.StringsUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.TopicConstant;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.*;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.criteria.TopicSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.VoteInfoDto;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.*;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import com.wondersgroup.healthcloud.services.bbs.util.BbsMsgHandler;
import com.wondersgroup.healthcloud.services.config.ConfigSwitch;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
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
    private BbsMsgHandler bbsMsgHandler;
    @Autowired
    private BadWordsService badWordsService;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ConfigSwitch configSwitch;

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
    @Autowired
    private TopicTabService topicTabService;

    @Override
    public List<TopicTopListDto> getCircleTopRecommendTopics(Integer circleId, Integer getNum) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setIsTop(true);
        searchCriteria.setCircleId(circleId);
        searchCriteria.setStatus(TopicConstant.Status.OK);
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
        searchCriteria.setStatus(TopicConstant.Status.OK);
        searchCriteria.setGetMoreOne(true);
        if (tabId == TopicConstant.DefaultTab.BASE_RECOMMEND){
            searchCriteria.setOrderInfo("topic.top_rank desc, topic.update_time desc");
            searchCriteria.setIsBest(true);
        }else if (tabId == TopicConstant.DefaultTab.NEW_PUBLISH){
            searchCriteria.setOrderInfo("topic.create_time desc");
        }else if (tabId == TopicConstant.DefaultTab.All){
            searchCriteria.setIsTop(false);//全部标签下 不显示置顶的
        }else {
            searchCriteria.setTabId(tabId);
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
        searchCriteria.setIsBest(true);
        searchCriteria.setStatus(TopicConstant.Status.OK);
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
        searchCriteria.setStatus(TopicConstant.Status.OK);
        searchCriteria.setOrderInfo("topic.last_comment_time desc");
        searchCriteria.setIsBest(true);
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
        searchCriteria.setStatus(TopicConstant.Status.OK);
        searchCriteria.setPageSize(pageSize);
        searchCriteria.setOrderInfo("topic.score desc, topic.last_comment_time desc");
        searchCriteria.setGetMoreOne(true);
        List<Topic> topics = this.searchTopicByCriteria(searchCriteria);
        return this.buildListDto(topics);
    }

    @Override
    public List<TopicListDto> getTopicsByUid(String uid, Boolean isMine, Integer page, Integer pageSize) {
        TopicSearchCriteria searchCriteria = new TopicSearchCriteria();
        searchCriteria.setUid(uid);
        if (!isMine){
            searchCriteria.setStatus(TopicConstant.Status.OK);
        }else {
            searchCriteria.setStatusIn(new Integer[]{TopicConstant.Status.WAIT_VERIFY, TopicConstant.Status.OK});
        }
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

    @Override
    public TopicDetailDto getTopicDetailInfo(Integer topicId) {
        Topic topic = topicRepository.findOne(topicId);
        if (null == topic){
            throw TopicException.notExist();
        }
        TopicDetailDto topicDetailDto = new TopicDetailDto(topic);

        List<TopicContent> topicContents = topicContentRepository.findContentsByTopicId(topicId);
        topicDetailDto.mergeTopicContents(topicContents);

        RegisterInfo userInfo = userService.getOneNotNull(topic.getUid());
        topicDetailDto.mergeUserInfo(userInfo);

        Circle circle = circleRepository.findOne(topic.getCircleId());
        if (circle == null){
            throw TopicException.notExist();
        }
        topicDetailDto.mergeCircleInfo(circle);

        if (1 == topic.getIsVote()){
            VoteInfoDto voteInfoDto = voteService.getVoteInfoByTopicId(topicId);
            topicDetailDto.setVoteInfo(voteInfoDto);
        }

        return topicDetailDto;
    }

    @Override
    public void incTopicPv(Integer topicId){
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
    }

    @Override
    @Transactional
    public Topic publishTopic(TopicPublishDto publishInfo) {
        Circle circle = circleRepository.findOne(publishInfo.getCircleId());
        if (null == circle || circle.getDelFlag().equals("1")) {
            throw new TopicException(2003, "圈子无效");
        }
        if (publishInfo.getIsTop() == 1){
            this.checkIsCanTopTopic(publishInfo.getCircleId(), publishInfo.getId());
        }
        this.isCanPublishForUser(publishInfo);

        //该话题是否发布过(话题只能发布一次，发布会推送相关消息)
        Boolean isPublished = false;
        Topic oldTopic = null;
        if (null != publishInfo.getId() && publishInfo.getId() > 0){
            oldTopic = topicRepository.findOne(publishInfo.getId());
            if (oldTopic == null){
                throw new RuntimeException("编辑的话题无效");
            }
            int oldStatus = oldTopic.getStatus();
            isPublished = oldStatus != TopicConstant.Status.WAIT_PUBLISH && oldStatus != TopicConstant.Status.WAIT_VERIFY;
            //已发布的 就不能在设置为待发布状态了
            if (isPublished){
                publishInfo.setIsPublish(1);
            }
        }
        //保存话题基本信息
        Topic topic = initTopicBaseInfo(publishInfo, oldTopic);
        topic = topicRepository.save(topic);
        if (topic.getId() == null) {
            throw new TopicException(2001, "保存话题失败");
        }
        publishInfo.setId(topic.getId());

        int nowStatus = topic.getStatus();
        Boolean isPublish = nowStatus != TopicConstant.Status.WAIT_PUBLISH && nowStatus != TopicConstant.Status.WAIT_VERIFY;

        //只有第一次发布的时候才会通知用户消息
        Boolean isFirstPublish = !isPublished && isPublish;
        //保存详情
        this.saveTopicContent(publishInfo);

        if (!isPublished){
            //发布以后 投票信息不可修改
            this.saveTopicVote(topic.getId(), publishInfo.getVoteItems());
        }

        topicTabService.updateTopicTabMapInfo(topic.getId(), publishInfo.getTags());

        //只有第一次发布才通知用户, 且圈子的话题数+1
        if (isFirstPublish){
            circle.setTopicCount(circle.getTopicCount() + 1);
            circleRepository.save(circle);
            //lts
            bbsMsgHandler.publishTopic(topic.getUid(), topic.getId());
        }
        return topic;
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

    private void saveTopicContent(TopicPublishDto publishInfo) {
        if (null == publishInfo.getId() || publishInfo.getId() == 0){
            throw new CommonException(2021, "话题不存在");
        }
        List<TopicContent> topicContents = new ArrayList<>();
        for (TopicPublishDto.TopicContent contentTmp : publishInfo.getTopicContents()) {
            List<String> imgs = contentTmp.getImgs();
            String imgsStr = "";
            if (imgs != null && !imgs.isEmpty()) {
                imgsStr = ArraysUtil.split2Sting(imgs, ",");
            }
            TopicContent topicContentTmp = new TopicContent(publishInfo.getId(), contentTmp.getContent(), imgsStr);
            if (null != contentTmp.getId() && contentTmp.getId()>0){
                topicContentTmp.setId(contentTmp.getId());
            }
            topicContents.add(topicContentTmp);
        }
        topicContentRepository.save(topicContents);
    }

    private Topic initTopicBaseInfo(TopicPublishDto publishInfo, Topic oldTopic){
        TopicPublishDto.TopicContent firstContent = publishInfo.getTopicContents().get(0);
        String intro = StringsUtils.subString(firstContent.getContent(), 80);
        List<String> allImgs = getAllImgsFromPublish(publishInfo);
        String topicImgs = "";//列表只显示3张图片
        int imgCount = allImgs.size();
        if (!allImgs.isEmpty()){
            List<String> listImgs = imgCount > 3 ? allImgs.subList(0, 3) : allImgs;
            topicImgs = ArraysUtil.split2Sting(listImgs, ",");
        }
        Date nowTime = new Date();
        Topic topic = oldTopic == null ? new Topic() : oldTopic;
        //新增
        Integer status;
        if (topic.getId() == null){
            topic.setUid(publishInfo.getUid());
            topic.setLastCommentTime(nowTime);
            topic.setCreateTime(nowTime);
            status = this.getStatusFromPublishInfo(publishInfo);
        }else {
            //编辑
            //发布以后就不能在修改归属人
            if (topic.getStatus() == TopicConstant.Status.WAIT_PUBLISH){
                topic.setUid(publishInfo.getUid());
            }
            if (topic.getStatus() != TopicConstant.Status.WAIT_PUBLISH){
                status = topic.getStatus();
            }else {
                status = this.getStatusFromPublishInfo(publishInfo);
            }
        }
        topic.setStatus(status);
        topic.setTitle(publishInfo.getTitle());
        topic.setCircleId(publishInfo.getCircleId());
        topic.setIntro(intro);
        topic.setImgs(topicImgs);
        topic.setImgCount(imgCount);
        topic.setIsBest(publishInfo.getIsBest());
        topic.setIsTop(publishInfo.getIsTop());
        topic.setTopRank(publishInfo.getTopRank());
        topic.setIsVote(null != publishInfo.getVoteItems() && !publishInfo.getVoteItems().isEmpty() ? 1 : 0);
        topic.setUpdateTime(nowTime);
        return topic;
    }

    private Integer getStatusFromPublishInfo(TopicPublishDto publishInfo){
        Integer status;
        if (publishInfo.getIsAdminPublish()){
            status = publishInfo.getIsPublish() == 0 ? TopicConstant.Status.WAIT_PUBLISH : TopicConstant.Status.OK;
        }else {
            status = configSwitch.isVerifyTopic() ? TopicConstant.Status.WAIT_VERIFY : TopicConstant.Status.OK;
        }
        return status;
    }

    private void saveTopicVote(Integer topicId, List<String> voteItems) {
        List<VoteItem> voteItemModels = new ArrayList<>();
        List<Vote> oldVotes = voteRepository.findVoteInfosByTopicId(topicId);
        //投票暂时不可以编辑
        if (oldVotes != null && !oldVotes.isEmpty()){
            return;
        }
        if (null != voteItems && !voteItems.isEmpty()) {
            Vote vote = new Vote(topicId);
            vote = voteRepository.save(vote);
            if (vote.getId() != null) {
                for (String voteItem : voteItems) {
                    voteItemModels.add(new VoteItem(vote.getId(), voteItem));
                }
            }
            voteItemRepository.save(voteItemModels);
        }
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
            listDto.setTitle(badWordsService.dealBadWords(listDto.getTitle()));
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

    private void isCanPublishForUser(TopicPublishDto publishInfo){
        if (StringUtils.isEmpty(publishInfo.getUid())) {
            throw new TopicException(2001, "uid非空");
        }
        RegisterInfo account = userService.getOneNotNull(publishInfo.getUid());
        if (account.getBanStatus() != UserConstant.BanStatus.OK){
            throw new CommonException(2014, "禁言状态无法发表话题哦");
        }
        if (null == publishInfo.getTopicContents() ||  publishInfo.getTopicContents().isEmpty()) {
            throw new TopicException(2002, "帖子无效");
        }
        //普通用户发表验证
        if (!publishInfo.getIsAdminPublish()){
            UserCircle userCircle = circleService.getAndCheckIsDefaultJoin(publishInfo.getCircleId(), publishInfo.getUid());
            if (null == userCircle){
                throw new TopicException(2013, "需要加入该圈子才能发布话题哦");
            }
        }
    }

    private List<String> getAllImgsFromPublish(TopicPublishDto publishInfo){
        //保存详情
        List<String> allImgs = new ArrayList<>();
        for (TopicPublishDto.TopicContent contentTmp : publishInfo.getTopicContents()) {
            List<String> imgs = contentTmp.getImgs();
            if (imgs != null && !imgs.isEmpty()) {
                allImgs.addAll(imgs);
            }
        }
        return allImgs;
    }

    @Override
    public Topic infoTopic(Integer topicId) {
        return topicRepository.findOne(topicId);
    }

    @Override
    public Topic delTopic(String uid, Integer topicId) {
        RegisterInfo account = userService.getOneNotNull(uid);
        if (account.getIsBBsAdmin() != 1){
            throw new RuntimeException("您不是管理员,不能操作该话题");
        }
        Topic topic = topicRepository.findOne(topicId);
        if (topic == null){
            throw new RuntimeException("话题不存在");
        }
        if (topic.getStatus() == TopicConstant.Status.USER_DELETE){
            throw new RuntimeException("该话题用户已经删除");
        }
        if (topic.getStatus() == TopicConstant.Status.ADMIN_DELETE){
            topic.setStatus(TopicConstant.Status.OK);
        }else {
            topic.setIsTop(0);
            topic.setTopRank(0);
            topic.setStatus(TopicConstant.Status.ADMIN_DELETE);
        }
        topicRepository.save(topic);
        if (topic.getStatus() == TopicConstant.Status.ADMIN_DELETE){
            bbsMsgHandler.adminDelTopic(topic.getUid(), topicId);
        }
        return topic;
    }

    @Transactional
    @Override
    public int verifyPass(Iterable<Integer> topicIds) {
        topicRepository.multSettingStatus(TopicConstant.Status.OK, topicIds);
        //lts
        bbsMsgHandler.publishMultTopics(topicIds);
        return 0;
    }

    @Transactional
    @Override
    public int verifyUnPass(Iterable<Integer> topicIds) {
        topicRepository.multSettingStatus(TopicConstant.Status.ADMIN_DELETE, topicIds);
        //lts
        bbsMsgHandler.publishMultTopics(topicIds);
        return 0;
    }

    @Override
    public int settingTopic(TopicSettingDto settingDto) {
        Circle circle = circleRepository.findOne(settingDto.getCircleId());
        if (null == circle || circle.getDelFlag().equals("1")) {
            throw new RuntimeException("圈子无效");
        }
        Topic topic = topicRepository.findOne(settingDto.getId());
        if (null == topic){
            throw new RuntimeException("话题不存在");
        }
        if (topic.getStatus() == TopicConstant.Status.USER_DELETE){
            throw new RuntimeException("该话题用户已经删除");
        }
        Boolean isToBest = false;
        if (1 == settingDto.getIsBest() && 1 != topic.getIsBest()){
            //加精
            isToBest = true;
        }
        topic.setIsBest(settingDto.getIsBest());
        if (settingDto.getIsTop() == 1 && topic.getIsTop() != 1){
            this.checkIsCanTopTopic(settingDto.getCircleId(), topic.getId());
        }
        topic.setIsTop(settingDto.getIsTop());
        topic.setTopRank(settingDto.getTopRank());
        topic.setCircleId(settingDto.getCircleId());

        topicTabService.updateTopicTabMapInfo(topic.getId(), settingDto.getTags());
        //lts
        if (isToBest){
            bbsMsgHandler.adminSetTopicBest(topic.getUid(), topic.getId());
        }
        return topic.getId();
    }

    /**
     * 使用jdbc查询
     * 对topic查询的简单封装
     * 支持TopicSearchCriteria条件的各种组合查询
     */
    private List<Topic> searchTopicByCriteria(TopicSearchCriteria searchCriteria){
        StringBuffer querySql = new StringBuffer("select topic.* from tb_bbs_topic topic ");
        querySql.append(" left join app_tb_register_info user on user.registerid = topic.uid ");
        if (searchCriteria.getTabId() > 0){
            querySql.append(" left join tb_bbs_topic_tab_map tab on tab.topic_id = topic.id ");
        }
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Topic> list = jdbcTemplate.query(querySql.toString(), elelmentType.toArray(), new BeanPropertyRowMapper(Topic.class));
        return list;
    }

    @Override
    public int countTopicByCriteria(TopicSearchCriteria searchCriteria) {
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_topic topic ");
        querySql.append(" left join tb_bbs_circle circle on circle.id=topic.circle_id ");
        querySql.append(" left join app_tb_register_info user on user.registerid=topic.uid ");
        if (StringUtils.isNotEmpty(queryParams.getQueryString())){
            querySql.append(" where " + queryParams.getQueryString());
        }
        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }

    /**
     * 置顶的时候 判断已经置顶的个数
     * 返回已经置顶话题的ids
     */
    private List<Integer> checkIsCanTopTopic(Integer circleId, Integer topicId){
        String querySql = "select id from tb_bbs_topic topic where topic.circle_id=? and topic.is_top=1 ";
        Object[] params = new Object[]{circleId};
        List<Integer> topTopicIds = jdbcTemplate.queryForList(querySql, params, Integer.class);
        topicId = topicId == null ? 0 : topicId;
        if (null != topTopicIds && !topTopicIds.contains(topicId) && topTopicIds.size() >= 5){
            throw new RuntimeException("已经有5个置顶的话题,无法置顶了,请先去取消其他置顶");
        }
        return topTopicIds;
    }

}
