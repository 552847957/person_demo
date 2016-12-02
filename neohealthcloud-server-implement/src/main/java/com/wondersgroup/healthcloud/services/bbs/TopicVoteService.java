package com.wondersgroup.healthcloud.services.bbs;


import com.wondersgroup.healthcloud.services.bbs.dto.VoteInfoDto;

/**
 * Created by ys on 2016/08/13.
 * <p>
 * 投票相关
 * </p>
 * @author ys
 */
public interface TopicVoteService {

    /**
     * 针对当前一个话题只能有一个投票
     * 可直接通过topicId获取投票信息
     */
    VoteInfoDto getVoteInfoByTopicId(Integer topicId);

    /**
     * 获取投票结果
     */
    VoteInfoDto getVoteInfoByVoteId(Integer voteId);

    /**
     * 用户投票
     * @return 返回投票后的结果
     */
    VoteInfoDto submitVote(String uid, Integer voteId, Integer voteItemId);

    /**
     * 判断用户是否投过票
     */
    Boolean isVotedForUser(String uid, Integer voteId);

}
