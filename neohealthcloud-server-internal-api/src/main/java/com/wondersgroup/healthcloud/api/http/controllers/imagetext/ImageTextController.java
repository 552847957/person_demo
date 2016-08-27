package com.wondersgroup.healthcloud.api.http.controllers.imagetext;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/8/17.
 */
@RestController
@RequestMapping("/api/imagetext")
public class ImageTextController {

    private static final Logger log = Logger.getLogger(ImageTextController.class);

    @Autowired
    private ImageTextService imageTextService;

    @Admin
    @RequestMapping(value = "/findImageTextByAdcode", method = RequestMethod.POST)
    public JsonResponseEntity findImageTextByAdcode(@RequestHeader(name = "main-area", required = true) String mainArea,
                                                    @RequestHeader(name = "spec-area", required = false) String specArea,
                                                    @RequestBody(required = true) ImageText imageText) {
        JsonResponseEntity result = new JsonResponseEntity();
        if (imageText == null || imageText.getAdcode() == null) {
            result.setCode(1000);
            result.setMsg("[adcode]字段不能为空");
        }
        ImageTextEnum imageTextEnum = ImageTextEnum.fromValue(imageText.getAdcode());
        if (imageTextEnum != null) {
            imageText.setMainArea(mainArea);
            imageText.setSpecArea(specArea);
            List<ImageText> imageTextList = imageTextService.findImageTextByAdcode(mainArea, specArea, imageText);
            if (imageTextList != null && imageTextList.size() > 0) {
                result.setData(imageTextList);
            } else {
                result.setCode(1000);
                result.setMsg("未查询到相关配置数据");
            }
        } else {
            result.setCode(1000);
            result.setMsg("[adcode]属性值异常");
        }
        return result;
    }

    @Admin
    @PostMapping(value = "saveBatchImageText")
    public JsonResponseEntity saveBatchImageText(@RequestBody String imageTexts) {
        JsonResponseEntity result = new JsonResponseEntity();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, ImageText.class);
            List<ImageText> imageTextList =  objectMapper.readValue(imageTexts, javaType);
            if (imageTextList != null && imageTextList.size() > 0) {
                int flag = imageTextService.saveBatchImageText(imageTextList);
                if (flag == imageTextList.size()) {
                    result.setMsg("数据保存成功！");
                    return result;
                }
            }
        } catch (Exception ex) {
            log.error("ImageTextController.saveBatchImageText error --> " + ex.getLocalizedMessage());
        }
        result.setCode(1000);
        result.setMsg("数据保存失败！");
        return result;
    }

    @Admin
    @RequestMapping(value = "/saveImageText", method = RequestMethod.POST)
    public JsonResponseEntity saveImageText(@RequestBody ImageText imageText) {
        JsonResponseEntity result = new JsonResponseEntity();
        ImageTextEnum imageTextEnum = ImageTextEnum.fromValue(imageText.getAdcode());
        if (imageTextEnum != null) {
            ImageText advertisement = imageTextService.saveImageText(imageText);
            if (advertisement != null) {
                result.setMsg("广告信息保存成功！");
            } else {
                result.setCode(1001);
                result.setMsg("广告信息保存失败！");
            }
        } else {
            result.setCode(1000);
            result.setMsg("广告类型参数异常");
        }
        return result;
    }

}
