package com.wondersgroup.healthcloud.api.http.controllers.imagetext;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/17.
 */
@RestController
@RequestMapping("/api/imagetext")
public class ImageTextController {

    @Autowired
    private ImageTextService imageTextService;

    @Admin
    @RequestMapping(value = "/findImageTextByAdcode", method = RequestMethod.GET)
    public JsonResponseEntity findImageTextByAdcode(@RequestHeader(name = "main-area", required = true) String mainArea,
                                                    @RequestHeader(name = "spec-area", required = false) String specArea,
                                                    @RequestParam ImageTextEnum imageTextEnum) {
        JsonResponseEntity result = new JsonResponseEntity();
        List<ImageText> imageTextList = imageTextService.findImageTextByAdcode(mainArea, specArea, imageTextEnum);
        if (imageTextList != null && imageTextList.size() > 0) {
            result.setData(imageTextList);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置数据");
        }
        return result;
    }

    @Admin
    @RequestMapping(value = "/saveImageText", method = RequestMethod.POST)
    public JsonResponseEntity saveImageText(@RequestBody ImageText imageText) {
        JsonResponseEntity result = new JsonResponseEntity();
        ImageText advertisement = imageTextService.saveImageText(imageText);
        if (advertisement != null) {
            result.setMsg("广告信息保存成功！");
        } else {
            result.setCode(1001);
            result.setMsg("广告信息保存失败！");
        }
        return result;
    }

}
