package com.wondersgroup.healthcloud.services.game.impl;

import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import com.wondersgroup.healthcloud.jpa.repository.game.GameScoreRepository;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
@Service
public class GameServiceImpl implements GameService{
    @Autowired
    private GameScoreRepository gameScoreRepo;

    @Override
    public Page<GameScore> findAll(int number, int size) {

        Specification<GameScore> specification = new Specification<GameScore>() {
            @Override
            public Predicate toPredicate(Root<GameScore> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("score").as(Integer.class)));
                return criteriaQuery.getRestriction();
            }
        };
        Pageable pageable = new PageRequest(number,size);
        return gameScoreRepo.findAll(specification,pageable);
    }

    @Override
    public void updatePersonScore(String registerId, Integer score) {
        GameScore gameScore = gameScoreRepo.getByRegisterId(registerId);
        if(null == gameScore){
            gameScore = new GameScore();
            gameScore.setCreateTime(new Date());
            gameScore.setCount(1);
            gameScore.setRegisterid(registerId);
            gameScore.setScore(score);
        }else{
            gameScore.setCount(gameScore.getCount()+1);
            if(gameScore.getScore()<score){
                gameScore.setScore(score);
            }
        }
        gameScore.setUpdateTime(new Date());

        gameScoreRepo.save(gameScore);
    }

    /**
     * 获取用户的分数排名
     * @param score
     * @return
     */
    @Override
    public Float getScoreRank(String registerid,Integer score) {


        int underCount = gameScoreRepo.getUnderCount(score);
        int totalCount = gameScoreRepo.getTotalCount();
        if(1 == totalCount && 1 == gameScoreRepo.getTotalCount(registerid,score)){
            return 100f;
        }
        float rate = (float)underCount / (float)totalCount;
        return Float.parseFloat(new DecimalFormat("#.##").format(rate).toString());
    }
}
