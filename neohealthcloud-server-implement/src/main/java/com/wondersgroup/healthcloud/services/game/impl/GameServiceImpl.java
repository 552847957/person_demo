package com.wondersgroup.healthcloud.services.game.impl;

import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import com.wondersgroup.healthcloud.jpa.repository.game.GameRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GameScoreRepository;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
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

    @Autowired
    private GameRepository gameRepo;

    @Override
    public List<Map<String, Object>> findAll(int number, int size) {
        String sql = "select registerid,score,rownum_finish as rank from ( " +
        " select id,registerid,score,rownum_1,rownum_add,if(@score=score and @rn_add!=rownum_add,@n3:=rownum_1+@rn_add,@n3:=rownum_1+rownum_add) rownum_finish ,@rn_add:=rownum_add,@score:=score " +
                " from (" +
                " select id,registerid,score,rownum_1,if(@rn=rownum_1,@nn:=@nn+(rownum_1-@rn),@nn:=@nn) rownum_add,@rn:=rownum_1 from ( " +
                " select id,registerid,score,if(@s!=score,@n:=@n+1,@n) rownum_1,@s:=score from " +
                " (select * from app_tb_game_score order by score desc,update_time desc) a,(select @n:=0) b,(select @s:=0) c) aa,(select @nn:=0) bb,(select @rn:=0) cc " +
                " ) aaa ,(select @rn_add:=0) bbb,(select @n3:=0) ccc,(select @score:=0) ddd " +
                " ) f limit "+number*size+" , "+size;
        return jt.queryForList(sql);
    }

    /**
     *
     * @param registerId
     * @param score
     * @param platform plantform 1:app ,2:微信
     */
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

        if(0 == score){
            return 0f;
        }
        int underCount = gameScoreRepo.getUnderCount(score);
        int totalCount = gameScoreRepo.getTotalCount();

        float rate = (float)underCount / (float)totalCount;
        DecimalFormat format=new DecimalFormat(".00");
        format.setRoundingMode(RoundingMode.FLOOR);
        return Float.parseFloat(format.format(rate));
    }

    @Override
    public List<Map<String, Object>> getGamePrize(String gameType) {
        String sql = "select prize.id, game.name as gameName, prize.amount,prize.name,prize.level \n" +
                " from app_tb_game_prize prize join app_tb_game game  on prize.game_id = game.id \n" +
                " where game.type = '"+gameType+"' and prize.del_flag = '0'" +
                " order by prize.update_date desc ";
        return jt.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getPrizeWin(int number, int size, String activityid ,String gameType) {
        String sql = "select win.registerid , win.create_date as date ,prize.name\n" +
                " from app_tb_prize_win win \n" +
                " join app_tb_game_prize prize on win.prizeid = prize.id \n" +
                " join app_tb_game game on prize.game_id = game.id\n" +
                " where game.type = '"+gameType+"' and prize.del_flag = '0' and win.del_flag = '0' and win.activityid = '"+activityid+"'" +
                " limit "+(number-1) * size+","+size;
        return jt.queryForList(sql);
    }

    @Override
    public Integer getPrizeTotal(String activityid ,String gameType) {
        String sql = "select count(1) as total" +
                " from app_tb_prize_win win \n" +
                " join app_tb_game_prize prize on win.prizeid = prize.id \n" +
                " join app_tb_game game on prize.game_id = game.id\n" +
                " where game.type = '"+gameType+"' and prize.del_flag = '0' and win.activityid = '"+activityid+"'";
        return Integer.parseInt(jt.queryForMap(sql).get("total").toString());
    }


}
