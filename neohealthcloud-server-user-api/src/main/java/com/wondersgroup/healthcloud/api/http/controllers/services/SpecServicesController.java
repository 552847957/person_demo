package com.wondersgroup.healthcloud.api.http.controllers.services;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
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
 * Created by zhaozhenxing on 2016/8/30.
 */
@RestController
@RequestMapping("/api/spec/services")
public class SpecServicesController {
    @Autowired
    private ImageTextService imageTextService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<Map<String, Object>> list(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                        @RequestHeader(value = "spec-area", required = false) String specArea,
                                                        @RequestHeader(value = "app-version", required = true) String version) {
        JsonResponseEntity<Map<String, Object>> result = new JsonResponseEntity<>();
        Map<String, Object> data = new HashMap<>();
        List<ImageText> imageTexts = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_SERVICE_BTN.getType(), version);
        if (imageTexts != null && imageTexts.size() > 0) {
            List funcList = new ArrayList<>();
            Map map = null;
            for (ImageText imageText : imageTexts) {
                map = new HashMap<>();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", imageText.getHoplink());
                map.put("mainTitle", imageText.getMainTitle());
                map.put("subTitle", imageText.getSubTitle());
                funcList.add(map);
            }
            data.put("services", funcList);
        }
        data.put("activities", new ArrayList<>());
        data.put("signMeasurements", new HashMap<>());
        result.setData(data);
        return result;
    }
}
