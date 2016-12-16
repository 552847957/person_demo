package com.wondersgroup.healthcloud.services.home.dto.advertisements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import lombok.Data;

/**
 * 中央区域广告
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CenterAdDTO {
    private String imgUrl;
    private String hoplink;
    public CenterAdDTO(){}
    public CenterAdDTO(String imgUrl,String hoplink){
        this.imgUrl = imgUrl;
        this.hoplink = hoplink;
    }

    public CenterAdDTO(ImageText imageText) {
        this.imgUrl = imageText.getImgUrl();
        this.hoplink = imageText.getHoplink();
    }

}
