package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 投票
 */
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    @Query("select a from Vote a where a.topicId=?1 ")
    List<Vote> findVoteInfosByTopicId(Integer topicId);
}
