package com.wondersgroup.healthcloud.api.http.controllers.imagetext;

import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaozhenxing on 2016/8/17.
 */
@RestController
@RequestMapping("/api/imagetext")
public class ImageTextController {

    @Autowired
    private ImageTextService imageTextService;

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
