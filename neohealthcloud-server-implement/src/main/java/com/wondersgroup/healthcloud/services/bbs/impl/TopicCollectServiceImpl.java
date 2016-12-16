package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicCollect;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicCollectRepository;
import com.wondersgroup.healthcloud.services.bbs.TopicCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("topicCollectService")
public class TopicCollectServiceImpl implements TopicCollectService {

    @Autowired
    private TopicCollectRepository topicCollectRepository;

    @Override
    public List<TopicCollect> getCollectTopicListByUid(String uid, Integer page, Integer pageSize) {
        return topicCollectRepository.findListByUid(uid, (page-1)*pageSize, pageSize+1);
    }

    @Override
    public Boolean isCollectedForUser(String uid, Integer topicId) {
        TopicCollect topicCollect = topicCollectRepository.findInfoByUidAndTopicId(uid, topicId);
        return null != topicCollect && topicCollect.getDelFlag().equals("0");
    }

    @Override
    public TopicCollect collectTopic(String uid, Integer topicId) {
        TopicCollect topicCollect = topicCollectRepository.findInfoByUidAndTopicId(uid, topicId);
        if (null == topicCollect) {
            topicCollect = new TopicCollect();
            topicCollect.setTopicId(topicId);
            topicCollect.setUid(uid);
            topicCollect.setCreateTime(new Date());
            topicCollectRepository.save(topicCollect);
        }else {
            topicCollect.setCreateTime(new Date());
            topicCollect.setDelFlag(topicCollect.getDelFlag().equals("0") ? "1" : "0");
            topicCollectRepository.save(topicCollect);
        }
        return topicCollect;
    }
}
