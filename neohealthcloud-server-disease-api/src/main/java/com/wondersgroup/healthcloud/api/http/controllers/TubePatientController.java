package com.wondersgroup.healthcloud.api.http.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.api.http.dto.TubePatientDetailEntity;
import com.wondersgroup.healthcloud.api.http.dto.TubePatientEntity;
import com.wondersgroup.healthcloud.api.utls.MapHelper;
import com.wondersgroup.healthcloud.api.utls.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.BaseInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.services.assessment.MeasureService;
import com.wondersgroup.healthcloud.services.assessment.dto.BloodGlucoseDTO;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 在管人群列表
 * Created by zhuchunliu on 2016/12/12.
 */
@RestController
@RequestMapping(value = "/api/tube/patient")
public class TubePatientController {

    private static final Logger logger = LoggerFactory.getLogger("info");

    @Autowired
    private DiabetesService diabetesService;

    @Autowired
    private DoctorAccountRepository doctorAccountRepo;

    @Autowired
    private DoctorInfoRepository doctorInfoRepo;

    @Autowired
    private BaseInfoRepository baseInfoRepo;

    @Autowired
    private MeasureService measureService;
    /**
     * 在管人群列表
     * @return
     */
    @GetMapping("/list")
    public JsonListResponseEntity list(
            @RequestParam String  doctorId,
            @RequestParam(required = false,name = "name") String  patientName,
            @RequestParam(required = false, defaultValue = "1") Integer flag){

        logger.info(" /api/tube/patient/list patientName :"+patientName);

        JsonListResponseEntity response = new JsonListResponseEntity();
        DoctorAccount doctor = doctorAccountRepo.findOne(doctorId);
        DoctorInfo doctorInfo = doctorInfoRepo.findOne(doctorId);
        if(null == doctor || null == doctorInfo){
            return response;
        }
        int pageSize = 10;
        List<TubePatientDTO> resource = diabetesService.getTubePatientList(doctorInfo.getHospitalId(),doctor.getName(),patientName,flag,pageSize);
        Integer total = diabetesService.getTubePatientNumber(doctorInfo.getHospitalId(),doctor.getName(),patientName);


        List<TubePatientEntity> list = Lists.newArrayList();
        List<String> personcareds = Lists.newArrayList();
        Map<String,Integer> map = Maps.newHashMap();
        for(int index = 0 ;index < resource.size() ; index++){
            TubePatientDTO dto = resource.get(index);
            list.add(new TubePatientEntity(dto));
            if(!StringUtils.isEmpty(dto.getCardNumber()) && !StringUtils.isEmpty(dto.getCardType())
                    && dto.getCardType().equals("01")){
                personcareds.add(dto.getCardNumber());
                map.put(dto.getCardNumber(),index);
            }
        }

        List<BloodGlucoseDTO> bloodList = measureService.getRecentAbnormalBloodGlucose(StringUtils.join(personcareds,","));
        MapHelper mapHelper =   MapHelper.builder().putObj("0","早餐前").putObj("1", "早餐后").putObj("2", "午餐前").
                putObj("3", "午餐后").putObj("4","晚餐前").putObj("5", "晚餐后").
                putObj("6", "睡前").putObj("7", "凌晨").putObj("8", "随机");
        for(BloodGlucoseDTO dto : bloodList){
            if(StringUtils.isEmpty(dto.getFlag()) || dto.getFlag().equals("0")){
                continue;
            }
            TubePatientEntity tube = list.get(map.get(dto.getPersoncard()));
            tube.setFlag(dto.getFlag());
            tube.setMeasureDate(new DateTime(dto.getDate()).toString("yyyy-MM-dd"));
            tube.setBloodGlucose(dto.getFpq());
            tube.setInterval(mapHelper.get(dto.getInterval().toString()).toString());
        }

        boolean hasMore = false;
        if(total > pageSize * flag){
            hasMore = true;
            flag++;
        }
        response.setContent(list,hasMore,null,flag.toString());
        return response;
    }

    /**
     * 高危筛查待提醒总数
     * @return
     */
    @GetMapping("/total")
    public JsonResponseEntity list(
            @RequestParam String  doctorId,
            @RequestParam(required = false,name = "name") String  patientName) {

        JsonResponseEntity response = new JsonResponseEntity();

        DoctorAccount doctor = doctorAccountRepo.findOne(doctorId);
        DoctorInfo doctorInfo = doctorInfoRepo.findOne(doctorId);
        if(null == doctor || null == doctorInfo){
            return response;
        }

        Integer total = diabetesService.getTubePatientNumber(doctorInfo.getHospitalId(),doctor.getName(),patientName);
        response.setData(ImmutableMap.of("total",total));
        return response;
    }

    @GetMapping("/detail")
    public JsonResponseEntity detail(
            @RequestParam(name="cardType") String cardType,
            @RequestParam(name="cardNumber") String cardNumber){
        TubePatientDetailDTO dto = diabetesService.getTubePatientDetail(cardType,cardNumber);
        if(null == dto){
            return new JsonResponseEntity();
        }
        TubePatientDetailEntity entity = new TubePatientDetailEntity(dto);
        if(!StringUtils.isEmpty(dto.getProfession())){
            if(!StringUtils.isEmpty(dto.getProfession())) {
                entity.setProfession(baseInfoRepo.getExplainMemo("profession", dto.getProfession()));
            }
            if(!StringUtils.isEmpty(dto.getCardType())) {
                entity.setCardTypeName(baseInfoRepo.getExplainMemo("personcardType", dto.getCardType()));
            }
        }
        return new JsonResponseEntity(0,null,entity);

    }
}
