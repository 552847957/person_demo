package com.wondersgroup.healthcloud.api.http.controllers.bbs;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.services.bbs.BadWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 违禁词
 * Created by ys on 2016-12-14.
 */
@RestController
@RequestMapping("/api/bbs/badword")
public class BadWordsController {

    @Autowired
    private BadWordsService badWordsService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public JsonResponseEntity info(){
        JsonResponseEntity<Map<String,String>> entity = new JsonResponseEntity();
        Map<String,String> info = new HashMap<>();
        info.put("badWords", badWordsService.getBadWords());
        entity.setData(info);
        return entity;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public JsonResponseEntity save(@RequestBody String request){
        JsonResponseEntity entity = new JsonResponseEntity();
        JsonKeyReader reader = new JsonKeyReader(request);
        String badWords = reader.readString("badWords", false);

        badWordsService.setBadWords(badWords);
        entity.setMsg("设置成功");
        return entity;
    }
}
