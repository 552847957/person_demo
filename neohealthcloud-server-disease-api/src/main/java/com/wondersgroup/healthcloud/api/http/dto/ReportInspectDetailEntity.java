package com.wondersgroup.healthcloud.api.http.dto;

import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDetailDTO;
import lombok.Data;

/**
 * Created by Administrator on 2016/12/13.
 */
@Data
public class ReportInspectDetailEntity {
    private String itemName;//项目名称
    private String itemResult;//项目结果
    private String itemUnit;//项目单位
    private String itemReference;//项目参考值

    public ReportInspectDetailEntity(ReportInspectDetailDTO dto){
        this.itemName = dto.getItemName();
        this.itemResult = dto.getItemResult();
        this.itemUnit = dto.getItemUnit();
        this.itemReference = dto.getItemReference();
    }
}
