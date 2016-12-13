package com.wondersgroup.healthcloud.api.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportScreeningDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by zhuchunliu on 2016/12/13.
 */
@Data
public class ReportScreeningEntity {

    private String reportDate; // 筛查日期
    private String hospitalName;//医疗机构代码
    private BigDecimal peripheralBloodSugar;//空腹末梢血糖
    private BigDecimal venousBloodSugar;//空腹静脉血糖
    private BigDecimal dgtt;//dgtt2h静脉血糖
    private String reportResult;//筛查结果  1:糖尿病、2：糖尿病前期、3：血糖正常

    public ReportScreeningEntity(ReportScreeningDTO dto){
        this.reportDate = null == dto.getReportDate() ?null : new DateTime(dto.getReportDate()).toString("yyyy-MM-dd");
        this.peripheralBloodSugar = dto.getPeripheralBloodSugar();
        this.venousBloodSugar = dto.getVenousBloodSugar();
        this.dgtt = dto.getDgtt();
        if(!StringUtils.isEmpty(dto.getReportResult())){
            if("1".equals(dto.getReportResult())){
                this.reportResult = "糖尿病";
            }else if("2".equals(dto.getReportResult())){
                this.reportResult = "糖尿病前期";
            }else if("3".equals(dto.getReportResult())){
                this.reportResult = "血糖正常";
            }
        }
    }
}
