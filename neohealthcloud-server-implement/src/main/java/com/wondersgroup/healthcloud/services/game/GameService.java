package com.wondersgroup.healthcloud.services.game;

import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import org.springframework.data.domain.Page;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
public interface GameService {
    Page<GameScore> findAll(int number, int size);

    void updatePersonScore(String registerId, Integer score);

    Float getScoreRank(String registerId, Integer score);
}
