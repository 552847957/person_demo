package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.game.GamePrize;
import com.wondersgroup.healthcloud.jpa.enums.GameType;
import com.wondersgroup.healthcloud.jpa.repository.game.GamePrizeRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GameRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.PrizeWinReporistory;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuchunliu on 2016/10/21.
 */
@RestController
@RequestMapping
public class PrizeController {
    @Autowired
    private GamePrizeRepository gamePrizeRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameService gameService;
    @Autowired
    private PrizeWinReporistory prizeWinRepo;

    @PostMapping(path = "/prize/update")
    public JsonResponseEntity update(@RequestBody GamePrize gamePrize){
        gamePrize.setDelFlag("0");
        gamePrize.setGameId(gameRepo.getTopGame(GameType.turntable.toString()).getId());
        gamePrize.setUpdateDate(new Date());
        gamePrizeRepo.save(gamePrize);
        return new JsonResponseEntity(0,"保存成功");
    }

    @DeleteMapping(path = "/prize/delete")
    public JsonResponseEntity update(@RequestParam(name = "id",required = true) Integer id){
        gamePrizeRepo.delete(id);
        return new JsonResponseEntity(0,"删除成功");
    }

    @GetMapping(path = "/prize/list")
    public JsonResponseEntity list(){
        List<Map<String,Object>> list = gameService.getGamePrize(GameType.turntable.toString());
        return new JsonResponseEntity(0,null,list);
    }


    @PostMapping(path = "/prize/win")
    public JsonResponseEntity win(@RequestBody Pager pager){
        List<Map<String,Object>> list = gameService.getPrizeWin(pager.getNumber(), pager.getSize(), GameType.turntable.toString());
        for(Map<String,Object> map : list){
            map.put("date",map.get("date").toString().substring(0,19));
        }
        pager.setData(list);
        pager.setTotalElements(gameService.getPrizeTotal(GameType.turntable.toString()));
        return new JsonResponseEntity(0,null,list);
    }

    @GetMapping(path = "/prize/export")
    public JsonResponseEntity export(){
        List<Map<String,Object>> list = gameService.getGamePrize(GameType.turntable.toString());
        return new JsonResponseEntity(0,null,list);
    }



}
