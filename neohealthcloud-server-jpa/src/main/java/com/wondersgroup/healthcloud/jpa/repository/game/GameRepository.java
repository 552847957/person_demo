package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
public interface GameRepository extends JpaRepository<Game,Integer> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update app_tb_game set app_click = IFNULL(app_click,0) +1")
    void updateAppClick();

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update app_tb_game set weixin_click = IFNULL(weixin_click,0) +1")
    void updateWeixinClick();

    @Query(nativeQuery = true,value = "select * from app_tb_game limit 1")
    Game getTopGame();
}
