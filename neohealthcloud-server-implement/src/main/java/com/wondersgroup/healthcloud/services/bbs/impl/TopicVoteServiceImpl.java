package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Vote;
import com.wondersgroup.healthcloud.jpa.entity.bbs.VoteItem;
import com.wondersgroup.healthcloud.jpa.entity.bbs.VoteUser;
import com.wondersgroup.healthcloud.jpa.repository.bbs.VoteItemRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.VoteRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.VoteUserRepository;
import com.wondersgroup.healthcloud.services.bbs.TopicVoteService;
import com.wondersgroup.healthcloud.services.bbs.dto.topic.VoteInfoDto;
import com.wondersgroup.healthcloud.services.bbs.exception.TopicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("topicVoteService")
public class TopicVoteServiceImpl implements TopicVoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteItemRepository voteItemRepository;

    @Autowired
    private VoteUserRepository voteUserRepository;

    @Override
    public VoteInfoDto getVoteInfoByTopicId(Integer topicId) {
        List<Vote> votes = voteRepository.findVoteInfosByTopicId(topicId);
        if (votes == null || votes.isEmpty()){
            return null;
        }
        Vote vote = votes.get(0);
        List<VoteItem> items = voteItemRepository.findVoteItemsByVoteId(vote.getId());
        VoteInfoDto voteInfoDto = new VoteInfoDto();
        voteInfoDto.setVoteId(vote.getId());
        voteInfoDto.setVoteTotalCount(vote.getVoteTotalCount());
        voteInfoDto.setVoteItems(items);
        return voteInfoDto;
    }

    @Override
    public VoteInfoDto getVoteInfoByVoteId(Integer voteId) {
        Vote vote = voteRepository.findOne(voteId);
        if (vote == null){
            return null;
        }
        List<VoteItem> items = voteItemRepository.findVoteItemsByVoteId(vote.getId());
        VoteInfoDto voteInfoDto = new VoteInfoDto();
        voteInfoDto.setVoteId(vote.getId());
        voteInfoDto.setVoteTotalCount(vote.getVoteTotalCount());
        voteInfoDto.setVoteItems(items);
        return voteInfoDto;
    }

    @Override
    @Transactional
    public VoteInfoDto submitVote(String uid, Integer voteId, Integer voteItemId) {
        VoteUser voteUser = new VoteUser();
        voteUser.setCreateTime(new Date());
        voteUser.setUid(uid);
        voteUser.setVoteId(voteId);
        voteUser.setVoteItemId(voteItemId);
        voteUserRepository.save(voteUser);

        List<VoteItem> items = voteItemRepository.findVoteItemsByVoteId(voteId);
        Boolean isOk = false;
        for (VoteItem voteItem : items){
            if (voteItem.getId().intValue() == voteItemId.intValue()){
                voteItem.setVoteCount(voteItem.getVoteCount()+1);
                voteItemRepository.save(voteItem);
                isOk = true;
            }
        }
        if (!isOk){
            throw new TopicException(2041, "投票失败,投票项目不存在");
        }

        Vote vote = voteRepository.findOne(voteId);
        vote.setVoteTotalCount(vote.getVoteTotalCount()+1);
        voteRepository.save(vote);

        VoteInfoDto voteInfoDto = new VoteInfoDto();
        voteInfoDto.setVoteId(vote.getId());
        voteInfoDto.setVoteTotalCount(vote.getVoteTotalCount());
        voteInfoDto.setVoteItems(items);
        return voteInfoDto;
    }

    @Override
    public Boolean isVotedForUser(String uid, Integer voteId) {
        VoteUser voteUser = voteUserRepository.findUserLastVoteInfo(uid, voteId);
        return null != voteUser;
    }
}
