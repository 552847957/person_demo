package com.wondersgroup.healthcloud.api.http.controllers.services;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.utils.security.ServiceUrlPlaceholderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private ServiceUrlPlaceholderResolver serviceUrlPlaceholderResolver;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity list(@RequestHeader(value = "main-area", required = true) String mainArea,
                                   @RequestHeader(value = "spec-area", required = false) String specArea,
                                   @RequestHeader(value = "app-version", required = true) String version,
                                   @AccessToken(required = false, guestEnabled = true) Session session) {
        JsonResponseEntity result = new JsonResponseEntity();

        List<ImageText> imageTexts = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_SERVICE_BTN.getType(), version);
        if (imageTexts != null && imageTexts.size() > 0) {
            List funcList = new ArrayList<>();
            Map map = null;
            for (ImageText imageText : imageTexts) {
                map = new HashMap<>();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", serviceUrlPlaceholderResolver.parseUrl(imageText.getHoplink(), session));
                map.put("mainTitle", imageText.getMainTitle());
                funcList.add(map);
            }
            result.setData(funcList);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置数据！");
        }

        return result;
    }
}
