package com.wondersgroup.healthcloud.jpa.repository.bbs;

import com.wondersgroup.healthcloud.jpa.entity.bbs.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Created by ys on 2016/08/11
 * 投票子项
 */
public interface VoteItemRepository extends JpaRepository<VoteItem, Integer> {

    @Query(nativeQuery = true, value = "select * from tb_bbs_vote_item a where a.vote_id=?1")
    List<VoteItem> findVoteItemsByVoteId(Integer voteId);
}
