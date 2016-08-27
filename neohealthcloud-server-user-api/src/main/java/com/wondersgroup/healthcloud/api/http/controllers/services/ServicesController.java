package com.wondersgroup.healthcloud.api.http.controllers.services;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity list(@RequestHeader(value = "main-area", required = true) String mainArea,
                                   @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();

        ImageText imgText = new ImageText();
        imgText.setAdcode(ImageTextEnum.SERVICE_BTN.getType());
        List<ImageText> imageTexts = imageTextService.findImageTextByAdcode(mainArea, specArea, imgText);
        if (imageTexts != null && imageTexts.size() > 0) {
            List funcList = new ArrayList<>();
            Map map = null;
            for (ImageText imageText : imageTexts) {
                map = new HashMap<>();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", imageText.getHoplink());
                map.put("mainTitle",imageText.getMainTitle());
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
