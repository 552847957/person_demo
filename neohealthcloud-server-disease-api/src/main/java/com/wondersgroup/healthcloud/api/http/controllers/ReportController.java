package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.ReportFollowEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportInspectDetailEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportInspectEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportScreeningEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportFollowDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDetailDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportScreeningDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuchunliu on 2016/12/13.
 */
@RestController
@RequestMapping(value = "/api/report")
public class ReportController {

    @Autowired
    private DiabetesService diabetesService;

    @Autowired
    private DictCache dictCache;

    /**
     * 筛查报告列表
     * @return
     */
    @GetMapping("/screening")
    public JsonResponseEntity screening(
            @RequestParam(name="cardType") String cardType,
            @RequestParam(name="cardNumber") String cardNumber){

        List<ReportScreeningDTO> source = diabetesService.getReportScreening(cardType,cardNumber);
        List<ReportScreeningEntity> list = Lists.newArrayList();
        for(ReportScreeningDTO dto : source){
            ReportScreeningEntity entity = new ReportScreeningEntity(dto);
            if(null != dto.getFilterResult() && !StringUtils.isEmpty(dto.getFilterResult().getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getFilterResult().getHospitalCode()));
            }else if(null != dto.getRiskAssess() && !StringUtils.isEmpty(dto.getRiskAssess().getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getRiskAssess().getHospitalCode()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity(0,null ,list);
    }

    /**
     * 检查报告列表
     * @return
     */
    @GetMapping("/inspect")
    public JsonResponseEntity inspect(
            @RequestParam(name="cardType") String cardType,
            @RequestParam(name="cardNumber") String cardNumber){

        List<ReportInspectDTO> source = diabetesService.getReportInspectList(cardType,cardNumber);
        List<ReportInspectEntity> list = Lists.newArrayList();
        for(ReportInspectDTO dto : source){
            ReportInspectEntity entity = new ReportInspectEntity(dto);
            if(!StringUtils.isEmpty(dto.getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getHospitalCode()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity(0,null ,list);
    }

    /**
     * 检查报告详情
     * @return
     */
    @GetMapping("/inspect/detail")
    public JsonResponseEntity inspectDetail(
            @RequestParam(name="reportNum") String reportNum,
            @RequestParam(name="reportDate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date reportDate){

        List<ReportInspectDetailDTO> resoure = diabetesService.getReportInspectDetail(reportNum,reportDate);
        List<ReportInspectDetailEntity> list = Lists.newArrayList();
        for(ReportInspectDetailDTO dto : resoure){
            list.add(new ReportInspectDetailEntity(dto));
        }
        return new JsonResponseEntity(0,null ,list);
    }

    /**
     * 随访报告
     * @return
     */
    @GetMapping("/follow")
    public JsonResponseEntity follow(
            @RequestParam(name="cardType") String cardType,
            @RequestParam(name="cardNumber") String cardNumber){

        List<ReportFollowDTO> source = diabetesService.getReportFollowList(cardType,cardNumber);
        List<ReportFollowEntity> list = Lists.newArrayList();
        for(ReportFollowDTO dto : source){
            ReportFollowEntity entity = new ReportFollowEntity(dto);
            if(!StringUtils.isEmpty(dto.getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getHospitalCode()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity(0,null ,list);
    }
}
