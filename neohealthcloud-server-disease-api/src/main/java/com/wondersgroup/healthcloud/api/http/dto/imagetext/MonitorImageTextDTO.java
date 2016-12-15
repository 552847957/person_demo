package com.wondersgroup.healthcloud.api.http.dto.imagetext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import lombok.Data;

/**
 * Created by zhaozhenxing on 2016/12/12.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorImageTextDTO {
    public String monitorId;
    public String mainTitle;
    public String subTitle;
    public String imgUrl;
    public boolean checked;

    public MonitorImageTextDTO() {}

    public MonitorImageTextDTO(ImageText imageText) {
        this.monitorId = imageText.getId();
        this.mainTitle = imageText.getMainTitle();
        this.subTitle = imageText.getSubTitle();
        this.imgUrl = imageText.getImgUrl();
    }
}
