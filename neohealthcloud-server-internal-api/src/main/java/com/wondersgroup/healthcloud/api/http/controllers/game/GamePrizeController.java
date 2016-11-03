package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.activity.HealthActivityInfo;
import com.wondersgroup.healthcloud.jpa.entity.game.Game;
import com.wondersgroup.healthcloud.jpa.entity.game.GamePrize;
import com.wondersgroup.healthcloud.jpa.entity.game.PrizeWin;
import com.wondersgroup.healthcloud.jpa.enums.GameType;
import com.wondersgroup.healthcloud.jpa.repository.activity.HealthActivityInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GamePrizeRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GameRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.PrizeWinReporistory;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/10/24.
 */
@RestController
@RequestMapping("/game/prize")
public class GamePrizeController {

    private Logger logger = LoggerFactory.getLogger(GamePrizeController.class);
    @Autowired
    private PrizeWinReporistory prizeWinRepo;
    @Autowired
    private GamePrizeRepository gamePrizeRepo;
    @Autowired
    private HealthActivityInfoRepository activityInfoRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private SessionUtil sessionUtil;
    /**
     * 抽奖
     * @return
     */
    @GetMapping(path = "/draw")
    public JsonResponseEntity draw(
            @RequestHeader(name="access-token",required = true) String token,
            @RequestParam(name = "activityid",required = true) String activityid){
        Session session = sessionUtil.get(token);
        if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
            logger.info(" token : "+token+"  返回code : 1001 ");
            return new JsonResponseEntity(1001,"登录已过期，请重新登录");
        }
        String registerId = session.getUserId();
        PrizeWin prizeWin = prizeWinRepo.findByRegisterId(registerId,activityid);
        if(null != prizeWin){
            logger.info(" token : "+token+"  返回code : 1002");
            return new JsonResponseEntity(1002,"您已经中奖了，不能重复抽奖哦");
        }
        Game game = gameRepo.getTopGame(GameType.TURNTABLE.type);
        GamePrize gamePrize = GamePrizeController.drawPrize(game.getId(),gamePrizeRepo);
        if(null == gamePrize){
            logger.info(" token : "+token+"  返回code : 1003");
            return new JsonResponseEntity(1003,"很遗憾，奖品库已空，欢迎下次参与");
        }
        PrizeWin win = new PrizeWin();
        win.setRegisterid(registerId);
        win.setActivityid(activityid);
        win.setPrizeid(gamePrize.getId());
        win.setCreateDate(new Date());
        win.setDelFlag("0");
        prizeWinRepo.save(win);



//        int total = gamePrizeRepo.getTotalByGameId(game.getId());
        int rank = gamePrizeRepo.getLessThenLevelTotal(game.getId(),gamePrize.getLevel());

        Map map = Maps.newHashMap();
        HealthActivityInfo activity = activityInfoRepo.findOne(win.getActivityid());

        if(null != activity){
            map.put("location",activity.getLocate());
            map.put("startDate", new DateTime(activity.getOfflineEndTime()).getDayOfMonth());
            map.put("startTime", new DateTime(activity.getOfflineStartTime()).getHourOfDay());
            map.put("endTime", new DateTime(activity.getOfflineEndTime()).getHourOfDay());
        }
        map.put("prizeName",gamePrize.getName());
//        map.put("total",total);
        map.put("rank",rank);
        logger.info(" token : "+token+"  返回code : "+0+" 抽中奖品 ："+gamePrize.getName());
        return new JsonResponseEntity(0,null, map);
    }

    /**
     * 中奖信息
     * @return
     */
    @GetMapping(path = "/info")
    public JsonResponseEntity info(
            @RequestHeader(name="access-token",required = true) String token,
            @RequestParam(name = "activityid",required = true) String activityid
            ){
        Session session = sessionUtil.get(token);
        if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
            return new JsonResponseEntity(1001,"登录已过期，请重新登录");
        }
        String registerId = session.getUserId();
        PrizeWin prizeWin = prizeWinRepo.findByRegisterId(registerId,activityid);
        if(null != prizeWin){//中奖
            GamePrize gamePrize = gamePrizeRepo.findOne(prizeWin.getPrizeid());
            HealthActivityInfo activity = activityInfoRepo.findOne(prizeWin.getActivityid());
            Map map = Maps.newHashMap();
            map.put("rank",gamePrizeRepo.getLessThenLevelTotal(gamePrize.getGameId(),gamePrize.getLevel()));
            if(null != gamePrize){
                map.put("prizeName",gamePrize.getName());
            }
            if(null != activity){
                map.put("location",activity.getLocate());
                map.put("startDate", new DateTime(activity.getOfflineEndTime()).getDayOfMonth());
                map.put("startTime", new DateTime(activity.getOfflineStartTime()).getHourOfDay());
                map.put("endTime", new DateTime(activity.getOfflineEndTime()).getHourOfDay());
            }
            return new JsonResponseEntity(0,null,map);
        }
        return new JsonResponseEntity(0,null,null);
    }

    /**
     * 随机抽奖
     * @return
     */
    private static synchronized GamePrize drawPrize(Integer gameId,GamePrizeRepository gamePrizeRepo){
        int amount = gamePrizeRepo.getAmoutByGameId(gameId);
        if(0 == amount){//奖池没有奖了，则返回null值
            return null;
        }
        int num = (int)(1+Math.random()*amount);
        int child = 0;
        List<GamePrize> list = gamePrizeRepo.findByGameId(gameId);
        for(GamePrize gamePrize : list){
            child += gamePrize.getAmount();
            if(child < num){
                continue;
            }
            gamePrize.setAmount(gamePrize.getAmount()-1);
            gamePrize.setUpdateDate(new Date());
            gamePrizeRepo.save(gamePrize);
            return gamePrize;
        }
        return null;
    }

}
