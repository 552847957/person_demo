package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.PrizeWin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
public interface PrizeWinReporistory extends JpaRepository<PrizeWin,Integer> {
    @Query("select a from PrizeWin a where a.registerid = ?1 and a.activityid = ?2 and a.delFlag = '0'")
    PrizeWin findByRegisterId(String registerId,String activityid);
}
