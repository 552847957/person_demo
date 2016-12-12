package com.wondersgroup.healthcloud.services.diabetes;

import com.wondersgroup.healthcloud.services.diabetes.dto.*;

import java.io.IOException;
import java.util.List;

/**
 * 调用web端慢病接口方法
 * Created by zhuchunliu on 2016/12/8.
 */
public interface DiabetesService {
    /**
     * 根据医生获取在管人群数
     * @param hospitalCode
     * @param doctorName
     * @return
     */
    public Integer getTubePatientNumber(String hospitalCode,String doctorName);

    /**
     * 在管人群列表
     * @param hospitalCode
     * @param doctorName
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<TubePatientDTO> getTubePatientList(String hospitalCode, String doctorName, Integer pageNo , Integer pageSize) throws IOException;

    /**
     * 根据身份证获取在管人群详情
     */
    public TubePatientDetailDTO getTubePatientDetail(String cardType,String cardNumber) throws Exception;

    /**
     * 筛查报告
     */
    public List<ReportScreeningDTO> getReportScreening(String cardType,String cardNumber) throws Exception;

    /**
     * 检查报告
     */
    public List<ReportInspectDTO> getReportInspectList(String cardType,String cardNumber) throws Exception;

    /**
     * 检查报告详情
     */
    public List<ReportInspectDetailDTO> getReportInspectDetail(String reportNum,String reportDate) throws Exception;


    /**
     * 随访报告
     */
    public List<ReportFollowDTO> getReportFollowList(String cardType,String cardNumber) throws Exception;
}

