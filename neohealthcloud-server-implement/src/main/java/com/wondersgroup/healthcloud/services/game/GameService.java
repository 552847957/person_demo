package com.wondersgroup.healthcloud.services.game;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
public interface GameService {
    List<Map<String, Object>> findAll(int number, int size);

    void updatePersonScore(String registerId, Integer score,Integer platform);

    Float getScoreRank(String registerId, Integer score);
}
