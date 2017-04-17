package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.ReportFollowEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportInspectDetailEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportInspectEntity;
import com.wondersgroup.healthcloud.api.http.dto.ReportScreeningEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.BaseInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportFollowDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportInspectDetailDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.ReportScreeningDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private RegisterInfoRepository registerInfoRepo;

    @Autowired
    private BaseInfoRepository baseInfoRepo;

    /**
     * 筛查报告列表
     * @return
     */
    @GetMapping("/screening")
    public JsonResponseEntity<List<ReportScreeningEntity>> screening(
            @RequestParam(name="uid",required = false) String uid,
            @RequestParam(name="cardType",required = false) String cardType,
            @RequestParam(name="cardNumber",required = false) String cardNumber){

        JsonResponseEntity jsonResponseEntity = this.getInfo(uid,cardType,cardNumber);
        if(0 != jsonResponseEntity.getCode()){
            return jsonResponseEntity;
        }
        cardNumber = jsonResponseEntity.getData().toString();
        cardType = StringUtils.isEmpty(uid)?cardType:"01";

        List<ReportScreeningDTO> source = diabetesService.getReportScreening(cardType,cardNumber);
        List<ReportScreeningEntity> list = Lists.newArrayList();
        if(null == source || 0 == source.size()){
            return new JsonResponseEntity<>(0,null ,list);
        }

        for(ReportScreeningDTO dto : source){
            ReportScreeningEntity entity = new ReportScreeningEntity(dto);
            if(null != dto.getFilterResult() && !StringUtils.isEmpty(dto.getFilterResult().getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getFilterResult().getHospitalCode()));
            }else if(null != dto.getRiskAssess() && !StringUtils.isEmpty(dto.getRiskAssess().getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getRiskAssess().getHospitalCode()));
            }

            if(null != dto.getFilterResult() && !StringUtils.isEmpty(dto.getFilterResult().getReportResult())){
                entity.setReportResult(baseInfoRepo.getExplainMemo("filter_result",dto.getFilterResult().getReportResult()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity<>(0,null ,list);
    }

    public JsonResponseEntity getInfo(String uid ,String cardType, String cardNumber){
        if(!StringUtils.isEmpty(uid)){
            RegisterInfo  registerInfo = registerInfoRepo.findOne(uid);
            if(null == registerInfo){
                return new JsonResponseEntity(3001,"用户不存在" ,null);
            }
            if(StringUtils.isEmpty(registerInfo.getPersoncard())){
                return new JsonResponseEntity(3002,"用户尚未进行实名认证" ,null);
            }
            cardNumber = registerInfo.getPersoncard();
        }else{
            if(StringUtils.isEmpty(cardType)){
                throw new RequestPostMissingKeyException("cardType");
            }
            if(StringUtils.isEmpty(cardNumber)){
                throw new RequestPostMissingKeyException("cardNumber");
            }
        }
        JsonResponseEntity response = new JsonResponseEntity();
        response.setData(cardNumber);
        return response;
    }

    /**
     * 检查报告列表
     * @return
     */
    @GetMapping("/inspect")
    public JsonResponseEntity<List<ReportInspectEntity>> inspect(
            @RequestParam(name="uid",required = false) String uid,
            @RequestParam(name="cardType",required = false) String cardType,
            @RequestParam(name="cardNumber",required = false) String cardNumber){

        JsonResponseEntity jsonResponseEntity = this.getInfo(uid,cardType,cardNumber);
        if(0 != jsonResponseEntity.getCode()){
            return jsonResponseEntity;
        }
        cardNumber = jsonResponseEntity.getData().toString();
        cardType = StringUtils.isEmpty(uid)?cardType:"01";

        List<ReportInspectDTO> source = diabetesService.getReportInspectList(cardType,cardNumber);
        List<ReportInspectEntity> list = Lists.newArrayList();
        if(null == source || 0 == source.size()){
            return new JsonResponseEntity<>(0,null ,list);
        }
        for(ReportInspectDTO dto : source){
            ReportInspectEntity entity = new ReportInspectEntity(dto);
            if(!StringUtils.isEmpty(dto.getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getHospitalCode()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity<>(0,null ,list);
    }

    /**
     * 检查报告详情
     * @return
     */
    @GetMapping("/inspect/detail")
    public JsonResponseEntity<List<ReportInspectDetailEntity>> inspectDetail(
            @RequestParam(name="reportNum") String reportNum,
            @RequestParam(name="reportDate")@DateTimeFormat(pattern = "yyyy-MM-dd") Date reportDate){

        List<ReportInspectDetailDTO> source = diabetesService.getReportInspectDetail(reportNum,reportDate);
        List<ReportInspectDetailEntity> list = Lists.newArrayList();
        if(null == source || 0 == source.size()){
            return new JsonResponseEntity<>(0,null ,list);
        }
        for(ReportInspectDetailDTO dto : source){
            list.add(new ReportInspectDetailEntity(dto));
        }
        return new JsonResponseEntity<>(0,null ,list);
    }

    /**
     * 随访报告
     * @return
     */
    @GetMapping("/follow")
    public JsonResponseEntity<List<ReportFollowEntity>> follow(
            @RequestParam(name="uid",required = false) String uid,
            @RequestParam(name="cardType",required = false) String cardType,
            @RequestParam(name="cardNumber",required = false) String cardNumber){

        JsonResponseEntity jsonResponseEntity = this.getInfo(uid,cardType,cardNumber);
        if(0 != jsonResponseEntity.getCode()){
            return jsonResponseEntity;
        }
        cardNumber = jsonResponseEntity.getData().toString();
        cardType = StringUtils.isEmpty(uid)?cardType:"01";

        List<ReportFollowDTO> source = diabetesService.getReportFollowList(cardType,cardNumber);
        List<ReportFollowEntity> list = Lists.newArrayList();
        if(null == source || 0 == source.size()){
            return new JsonResponseEntity<>(0,null ,list);
        }
        for(ReportFollowDTO dto : source){
            ReportFollowEntity entity = new ReportFollowEntity(dto);
            if(!StringUtils.isEmpty(dto.getHospitalCode())){
                entity.setHospitalName(dictCache.queryHospitalName(dto.getHospitalCode()));
            }
            list.add(entity);
        }
        return new JsonResponseEntity<>(0,null ,list);
    }

    /**
     * 随访报告
     * @return
     */
    @GetMapping("/num")
    public JsonResponseEntity<Map<String,Object>> num(
            @RequestParam(name="uid",required = true) String uid){

        RegisterInfo  registerInfo = registerInfoRepo.findOne(uid);
        if(null == registerInfo){
            return new JsonResponseEntity(3001,"用户不存在" ,null);
        }
        if(StringUtils.isEmpty(registerInfo.getPersoncard())){
            return new JsonResponseEntity(3002,"用户尚未进行实名认证" ,null);
        }

        Map<String,Object> map = diabetesService.getReportCount("01",registerInfo.getPersoncard());

        return new JsonResponseEntity<>(0,null ,map);
    }
}
