package com.wondersgroup.healthcloud.api.http.controllers.services;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
@RestController
@RequestMapping("/api/services")
public class ServicesController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity list() {
        JsonResponseEntity result = new JsonResponseEntity();
        // todo
        List funcList = new ArrayList<>();
        Map map = new HashMap<>();
        map.put("imgUrl","http://www.wondersgroup.com/");
        map.put("hoplink","http://www.wondersgroup.com/");
        map.put("mainTitle","体征测量");
        funcList.add(map);
        map = new HashMap<>();
        map.put("imgUrl","http://www.wondersgroup.com/");
        map.put("hoplink","http://www.wondersgroup.com/");
        map.put("mainTitle","食物库");
        funcList.add(map);

        result.setData(funcList);
        return result;
    }
}
