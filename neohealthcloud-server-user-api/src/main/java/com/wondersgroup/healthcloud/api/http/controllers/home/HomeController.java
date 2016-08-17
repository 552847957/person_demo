package com.wondersgroup.healthcloud.api.http.controllers.home;

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
@RequestMapping("/api/home")
public class HomeController {

    @RequestMapping(value = "/bannerFunctionAds", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity bannerFunctionAds() {
        // todo zzx
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();
        List banners = new ArrayList();
        Map map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        banners.add(map);
        map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        banners.add(map);
        data.put("banners", banners);

        List functionIcons = new ArrayList();
        map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        map.put("mainTitle", "体征测量");
        map.put("subTitle", "记录您的体征数据");
        functionIcons.add(map);
        map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        map.put("mainTitle", "在线咨询");
        map.put("subTitle", "三甲医院,随时解惑");
        functionIcons.add(map);
        data.put("functionIcons", functionIcons);

        Map advertisements = new HashMap();
        advertisements.put("allowClose", true);
        List adImages = new ArrayList();
        map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        adImages.add(map);
        map = new HashMap();
        map.put("imgUrl", "http://www.wondersgroup.com/");
        map.put("hoplink", "http://www.wondersgroup.com/");
        adImages.add(map);
        advertisements.put("adImages", adImages);
        data.put("advertisements", advertisements);
        result.setData(data);
        return result;
    }

    @RequestMapping(value = "/appTips", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity appTips() {
        // todo zzx
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();
        data.put("content","热烈庆祝万达信息成立20周年，期待万达信息的明天越来越好！");
        data.put("hoplink", "https://www.wondersgroup.com");
        result.setData(data);
        return result;
    }

    @RequestMapping(value = "/newsAndQuestions", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity newsAndQuestions() {
        // todo zzx
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();
        List news = new ArrayList<>();
        data.put("news", news);
        List questions = new ArrayList<>();
        data.put("questions", questions);
        result.setData(data);
        return result;
    }
}
