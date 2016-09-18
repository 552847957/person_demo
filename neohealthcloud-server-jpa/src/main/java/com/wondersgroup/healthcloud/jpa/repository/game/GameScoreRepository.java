package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
public interface GameScoreRepository extends JpaRepository<GameScore, Integer> ,JpaSpecificationExecutor<GameScore> {

    @Query("select gs from GameScore gs where gs.registerid = ?1")
    GameScore getByRegisterId(String registerId);

    @Query("select count(1) from GameScore gs where gs.score <= ?1")
    Integer getUnderCount(Integer score);

    @Query("select count(1) from GameScore")
    Integer getTotalCount();
}