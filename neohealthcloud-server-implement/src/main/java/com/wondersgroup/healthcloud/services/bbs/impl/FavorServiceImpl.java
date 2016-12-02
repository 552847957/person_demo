package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Topic;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicFavor;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicFavorRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicRepository;
import com.wondersgroup.healthcloud.services.bbs.FavorService;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ys on 2016/10/24.
 * @author ys
 */
@Service("favorService")
public class FavorServiceImpl implements FavorService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicFavorRepository topicFavorRepository;

    @Transactional
    @Override
    public int favorTopic(String uid, Integer topicId) {
        Topic topic = topicRepository.findOne(topicId);
        if (topic == null){
            throw TopicException.notExist();
        }
        int favorCount = topic.getFavorCount();
        if (!this.isFavorTopic(uid, topicId)){
            TopicFavor topicFavor = new TopicFavor(uid, topicId);
            topicFavorRepository.saveAndFlush(topicFavor);
            topic.setFavorCount(++favorCount);
            topicRepository.saveAndFlush(topic);
        }
        return favorCount;
    }

    @Override
    public Boolean isFavorTopic(String uid, Integer topicId) {
        TopicFavor topicFavor = topicFavorRepository.findByUidAndTopicId(uid, topicId);
        return null != topicFavor;
    }

}
