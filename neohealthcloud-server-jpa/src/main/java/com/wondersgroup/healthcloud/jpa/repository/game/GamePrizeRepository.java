package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.GamePrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
public interface GamePrizeRepository extends JpaRepository<GamePrize,Integer> {
    @Query("select a from GamePrize a where a.gameId = ?1 and a.delFlag = '0' order by a.level asc")
    List<GamePrize> findByGameId(Integer gameId);

    @Query("select sum(a.amount) from GamePrize a where a.gameId = ?1 and a.delFlag = '0'")
    Integer getAmoutByGameId(Integer gameId);

    @Query("select count(a) from GamePrize a where a.gameId = ?1 and a.delFlag = '0'")
    Integer getTotalByGameId(Integer id);

    @Query("select count(a) from GamePrize a where a.gameId = ?1 and a.level <= ?2  and a.delFlag = '0'")
    Integer getLessThenLevelTotal(Integer id, Integer level);

}
