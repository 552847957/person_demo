package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.VoteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Created by ys on 2016/08/11
 * 投票用户
 */
public interface VoteUserRepository extends JpaRepository<VoteUser, Integer> {

    @Query(nativeQuery = true,
            value = "select * from tb_bbs_vote_user a where a.uid=?1 and a.vote_id=?2 order by id desc limit 1")
    VoteUser findUserLastVoteInfo(String uid, Integer voteId);
}
