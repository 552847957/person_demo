package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.enums.GameType;
import com.wondersgroup.healthcloud.jpa.repository.game.PrizeWinReporistory;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/10/24.
 */
@RestController
@RequestMapping("/game/prize")
public class GamePrizeController {

    @Autowired
    private PrizeWinReporistory prizeWinRepo;

    @Autowired
    private GameService gameService;
    /**
     * 中奖信息
     * @return
     */
    @GetMapping(path = "/win")
    public JsonResponseEntity prize(){
        List<Map<String,Object>> list = gameService.getGamePrize(GameType.turntable.toString());
        return new JsonResponseEntity(0,null,list);
    }

    /**
     * 中奖信息
     * @return
     */
    @GetMapping(path = "/info")
    public JsonResponseEntity pirzeList(){
        List<Map<String,Object>> list = gameService.getGamePrize(GameType.turntable.toString());
        return new JsonResponseEntity(0,null,list);
    }
}
