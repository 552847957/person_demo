package com.wondersgroup.healthcloud.services.diabetes;

import com.wondersgroup.healthcloud.services.diabetes.dto.*;

import lombok.Data;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public Integer getTubePatientNumber(String hospitalCode,String doctorName,String patientName);

    /**
     * 在管人群列表
     * @param hospitalCode
     * @param doctorName
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<TubePatientDTO> getTubePatientList(String hospitalCode, String doctorName, String patientName,Integer pageNo , Integer pageSize);

    /**
     * 根据身份证获取在管人群详情
     */
    public TubePatientDetailDTO getTubePatientDetail(String cardType,String cardNumber);

    /**
     * 筛查报告
     */
    public List<ReportScreeningDTO> getReportScreening(String cardType,String cardNumber);

    /**
     * 检查报告
     */
    public List<ReportInspectDTO> getReportInspectList(String cardType,String cardNumber);

    /**
     * 检查报告详情
     */
    public List<ReportInspectDetailDTO> getReportInspectDetail(String reportNum, Date reportDate);


    /**
     * 随访报告
     */
    public List<ReportFollowDTO> getReportFollowList(String cardType,String cardNumber);

    /**
     * 获取用户报告数
     * @param cardType
     * @param cardNumber
     * @return
     */
    Map<String,Object> getReportCount(String cardType, String cardNumber);
    
    /**
     * 随访计划频度展示
     * @param cardType
     * @param cardNumber
     * @return
     */
    public FollowPlanDTO getFollowPlanList(String cardType, String cardNumber);
    
    /**
     * 糖料病用户一周没血糖记录，进行提醒
     * @param registerId
     * @return FollowPlanDTO
     */
    public Boolean addDiabetesRemindMessage(String registerId);
}

