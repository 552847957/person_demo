package com.wondersgroup.healthcloud.jpa.repository.game;

import com.wondersgroup.healthcloud.jpa.entity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
public interface GameRepository extends JpaRepository<Game,Integer> {
}
