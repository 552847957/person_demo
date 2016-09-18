package com.wondersgroup.healthcloud.services.game.impl;

import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import com.wondersgroup.healthcloud.jpa.repository.game.GameScoreRepository;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
@Service
public class GameServiceImpl implements GameService{
    @Autowired
    private GameScoreRepository gameScoreRepo;

    @Autowired
    private JdbcTemplate jt;

    @Override
    public List<Map<String, Object>> findAll(int number, int size) {
        String sql = "select registerid,score,rownum_finish as rank from ( " +
        " select id,registerid,score,rownum_1,rownum_add,if(@score=score and @rn_add!=rownum_add,@n3:=rownum_1+@rn_add,@n3:=rownum_1+rownum_add) rownum_finish ,@rn_add:=rownum_add,@score:=score " +
                " from (" +
                " select id,registerid,score,rownum_1,if(@rn=rownum_1,@nn:=@nn+1,@nn:=@nn) rownum_add,@rn:=rownum_1 from ( " +
                " select id,registerid,score,if(@s!=score,@n:=@n+1,@n) rownum_1,@s:=score from " +
                " (select * from app_tb_game_score order by score desc) a,(select @n:=0) b,(select @s:=0) c) aa,(select @nn:=0) bb,(select @rn:=0) cc " +
                " ) aaa ,(select @rn_add:=0) bbb,(select @n3:=0) ccc,(select @score:=0) ddd " +
                " ) f limit "+number*size+" , "+size;
        return jt.queryForList(sql);
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

        float rate = (float)underCount / (float)totalCount;
        return Float.parseFloat(new DecimalFormat("#.##").format(rate).toString());
    }


}
