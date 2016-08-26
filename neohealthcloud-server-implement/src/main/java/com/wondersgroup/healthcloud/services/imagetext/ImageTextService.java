package com.wondersgroup.healthcloud.services.imagetext;

import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;

import java.util.List;

/**
 * Created by zhaozhenxing on 2016/6/12.
 */
public interface ImageTextService {
    ImageText saveImageText(ImageText imageText);

    int saveBatchImageText(List<ImageText> imageTextList);

    List<ImageText> findImageTextByAdcode(String mainArea, String specArea, ImageTextEnum adcode);
}
