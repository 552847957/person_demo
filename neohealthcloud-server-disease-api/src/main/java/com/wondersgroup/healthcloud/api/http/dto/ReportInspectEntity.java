package com.wondersgroup.healthcloud.api.http.dto;

import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Administrator on 2016/12/13.
 */
@Data
public class ReportInspectEntity {
    private String reportNum;//报告单号
    private String hospitalName;//医疗机构代码
    private String reportDate;//报告日期
    private String reportType;//报告类型

    public ReportInspectEntity(ReportInspectDTO dto){
        this.reportNum = dto.getReportNum();
        this.reportDate = StringUtils.isEmpty(dto.getReportDate())?
                null:DateTime.parse(dto.getReportDate(), DateTimeFormat.forPattern("yyyyMMdd")).toString("yyyy-MM-dd");
        this.reportType = dto.getReportType();
    }
}
