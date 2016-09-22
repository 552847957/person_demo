package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.jpa.entity.game.Game;
import com.wondersgroup.healthcloud.jpa.entity.game.GameScore;
import com.wondersgroup.healthcloud.jpa.entity.game.WechatRegister;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.game.GameRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GameScoreRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.WechatRegisterRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.game.GameService;
import com.wondersgroup.healthcloud.services.user.SessionUtil;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/8/31.
 */
@RestController
@RequestMapping(value = "/game")
public class GameController {

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameScoreRepository gameScoreRepo;
    @Autowired
    private GameService gameService;
    @Autowired
    private RegisterInfoRepository registerInfoRepo;
    @Autowired
    private SessionUtil sessionUtil;
    @Autowired
    private WechatRegisterRepository wechatRegisterRepo;

    private Logger logger = LoggerFactory.getLogger(GameController.class);
    /**
     * 获取游戏分数
     * @return
     */
    @PostMapping(path = "/score/list")
    public Pager scoreList(@RequestBody Pager pager){
        List<Map<String, Object>> resultMap = gameService.findAll(pager.getNumber()-1,pager.getSize());
        for(Map<String,Object> map : resultMap){
            map.put("nickname", null == map.get("registerid") ? "" : this.getNiceName(map.get("registerid").toString()));
            map.remove("registerid");
        }
        pager.setData(resultMap);
        int totalCount = gameScoreRepo.getTotalCount();
        pager.setTotalElements(totalCount >100 ?100:totalCount);
        return pager;
    }

    /**
     * 获取分数
     * @param token
     * @return
     */
    @GetMapping(path = "/score/person")
    public JsonResponseEntity getPersonScore(
            @RequestHeader(name="access-token",required = false) String token,
            @RequestHeader(name = "openid",required = false) String openid){

        if(StringUtils.isEmpty(token) && StringUtils.isEmpty(openid)){
            return new JsonResponseEntity(0,"您已长时间未登录，请重新登录获取历史分数!",ImmutableBiMap.of("score",""));
        }

        String registerId = null;
        if(!StringUtils.isEmpty(openid)){//微信登录
            WechatRegister wechatRegister = wechatRegisterRepo.getByOpenId(openid);
            registerId = null == wechatRegister ? null : wechatRegister.getRegisterid();
        }else{//app登录
            Session session = sessionUtil.get(token);
            if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
                return new JsonResponseEntity(0,"您已长时间未登录，请重新登录获取历史分数!",ImmutableBiMap.of("score",""));
            }
            registerId = session.getUserId();
        }

        if(StringUtils.isEmpty(registerId)){
            logger.info(" method : getPersonScore  获取不到用户信息 openId : "+openid+"  token : "+token);
            return new JsonResponseEntity(0,"获取不到用户信息!",ImmutableBiMap.of("score",""));
        }
        GameScore gameScore = gameScoreRepo.getByRegisterId(registerId);
        ImmutableBiMap map;
        if(null != gameScore){
            float rate = gameService.getScoreRank(registerId,gameScore.getScore());
            map = ImmutableBiMap.of("score",gameScore.getScore(),"rate",new DecimalFormat("#").format(rate*100)+"%");
        }else{
            map = ImmutableBiMap.of("score","");
        }
        return new JsonResponseEntity(0,null,map);
    }

    /**
     * 保存用户游戏成绩
     * @param token
     * @return
     */
    @PostMapping(path = "/score/person")
    public JsonResponseEntity setPersonScore(
            @RequestHeader(name="access-token",required = false) String token,
            @RequestHeader(name="openid",required = false) String openid,
            @RequestBody String request) throws  Exception{

        JsonKeyReader reader = new JsonKeyReader(request);

        if(StringUtils.isEmpty(token) && StringUtils.isEmpty(openid)){
            return new JsonResponseEntity(1001,"您已长时间未登录，请重新登录!");
        }

        String registerId = null;
        if(!StringUtils.isEmpty(openid)){//微信登录
            WechatRegister wechatRegister = wechatRegisterRepo.getByOpenId(openid);
            registerId = null == wechatRegister ? null : wechatRegister.getRegisterid();
        }else{//app登录
            Session session = sessionUtil.get(token);
            if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
                return new JsonResponseEntity(1001,"您已长时间未登录，请重新登录!");
            }
            registerId = session.getUserId();
        }

        if(StringUtils.isEmpty(registerId)){
            logger.info("method : setPersonScore  获取不到用户信息 openId : "+openid+"  token : "+token);
            return new JsonResponseEntity(1001,"获取不到用户信息!",ImmutableBiMap.of("score",""));
        }

        String score_encode = reader.readString("c2NvcmU=", false);
        int score =0;
        if(!StringUtils.isEmpty(score_encode) && score_encode.length() > 2){
            score = Integer.parseInt(new String(new BASE64Decoder().decodeBuffer(score_encode.substring(2))));
        }

        int maxScore = 18000;
        Game game = gameRepo.getTopGame();
        if(null != game && null != game.getMaxScore()){
            maxScore = game.getMaxScore();
        }

        if(score > maxScore || score < 0 ){
            return new JsonResponseEntity(1002,"成绩无效，请停止非法攻击!");
        }

        if(StringUtils.isEmpty(token)){
            return new JsonResponseEntity(1001,"您已长时间未登录，请重新登录!");
        }

        logger.info(" registerId: "+registerId +"   score: "+score);
        gameService.updatePersonScore(registerId,score);
        Float rate = gameService.getScoreRank(registerId, score);
        return new JsonResponseEntity(0,null,ImmutableBiMap.of("rate",new DecimalFormat("#").format(rate*100)+"%"));
    }

    /**
     * 检测用户是否绑定了手机号
     * @param token
     * @return
     */
    @GetMapping(path = "/check/phone")
    public JsonResponseEntity checkPhone(@RequestHeader(name="access-token",required = false) String token){

        if(StringUtils.isEmpty(token)){
            return new JsonResponseEntity(1001,"玩游戏，请先登录哟！");
        }

        Session session = sessionUtil.get(token);
        if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
            return new JsonResponseEntity(1001,"玩游戏，请先登录哟！");
        }
        RegisterInfo register = registerInfoRepo.findOne(session.getUserId());
        boolean flag = false;
        if(null != register && StringUtils.isNotEmpty(register.getRegmobilephone())){
            flag = true;
        }
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setData(ImmutableMap.of("flag",flag));
        return entity;
    }


    /**
     * 检测token是否有效
     * @param token
     * @return
     */
    @GetMapping(path = "/check/token")
    public JsonResponseEntity checkToken(@RequestHeader(name="access-token",required = false) String token){

        if(StringUtils.isEmpty(token)){
            return new JsonResponseEntity(1001,"玩游戏，请先登录哟！");
        }

        Session session = sessionUtil.get(token);
        if(null == session || false == session.getIsValid() || StringUtils.isEmpty(session.getUserId())){
            return new JsonResponseEntity(1001,"玩游戏，请先登录哟！");
        }
        return new JsonResponseEntity();
    }

    /**
     * 累计挑战次数
     * plantform 1:app ,2:微信
     * @return
     */
    @PostMapping(path = "/click")
    public JsonResponseEntity click(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer platform = reader.readInteger("platform", false);
        if(1 == platform){
            gameRepo.updateAppClick();
        }else{
            gameRepo.updateWeixinClick();
        }
        return new JsonResponseEntity(0,"统计成功");
    }

    /**
     * 累计分享次数
     * plantform 1:app ,2:微信
     * @return
     */
    @PostMapping(path = "/share")
    public JsonResponseEntity share(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        Integer platform = reader.readInteger("platform", false);
        Game game = gameRepo.getTopGame();

        if(null != game) {
            if(1 == platform){
                game.setAppShare(game.getAppShare() == null ? 1 : game.getAppShare() + 1);
            }else{
                game.setWeixinShare(game.getWeixinShare() == null ? 1 : game.getWeixinShare()+1);
            }
            gameRepo.save(game);
        }
        return new JsonResponseEntity(0,"统计成功");
    }



    /**
     * 获取游戏规则
     * @return
     */
    @GetMapping(path = "/rule")
    public JsonResponseEntity rule(){
        Game game = gameRepo.getTopGame();
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setData(ImmutableMap.of("rule",null != game?game.getRule():""));
        return entity;
    }

    /**
     * 检测是否在游戏时间区间内
     * @return
     */
    @GetMapping(path = "/check")
    public JsonResponseEntity check(){
        Game game = gameRepo.getTopGame();

        Boolean flag = false;

        if(null != game){
            if(null != game.getStartTime() && null != game.getEndTime()
                    && new DateTime(game.getStartTime()).isBefore(DateTime.now())
                    && new DateTime(game.getEndTime()).isAfter(DateTime.now())){
                flag = true;
            }
        }
        JsonResponseEntity entity = new JsonResponseEntity();
        entity.setData(ImmutableMap.of("flag",flag));
        return entity;
    }

    private Object getNiceName(String registerid) {
        RegisterInfo register = registerInfoRepo.findOne(registerid);
        if(null == register || StringUtils.isEmpty(register.getNickname())){
            return "火星用户";
        }
        String nickName = register.getNickname();
        if(1 == nickName.length()){
            return nickName;
        }else if(2 == nickName.length()){
            return nickName.charAt(0)+"*";
        }else if(3 == nickName.length()){
            return nickName.charAt(0)+"*" + nickName.charAt(2);
        }else if(4 == nickName.length()){
            return nickName.charAt(0)+"**" + nickName.charAt(3);
        }else if(5 == nickName.length()){
            return nickName.charAt(0)+"***" + nickName.charAt(4);
        }else if(6 == nickName.length()){
            return nickName.charAt(0)+"****" + nickName.charAt(5);
        }else if(11 == nickName.length()){
            return nickName.substring(0, 3)+"****" +nickName.substring(7, 11);
        }else{
            StringBuffer sb = new StringBuffer();
            sb.append(nickName.substring(0,4));
            for(int i=0 ;i<nickName.length() - 6; i++){
                sb.append("*");
            }
            return sb.append(nickName.substring(nickName.length()-2)).toString();
        }
    }
}
