package com.wondersgroup.healthcloud.api.http.controllers;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.TubePatientDetailEntity;
import com.wondersgroup.healthcloud.api.http.dto.TubePatientEntity;
import com.wondersgroup.healthcloud.api.utls.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.exceptions.RequestPostMissingKeyException;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.BaseInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.services.diabetes.DiabetesService;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDTO;
import com.wondersgroup.healthcloud.services.diabetes.dto.TubePatientDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 在管人群列表
 * Created by zhuchunliu on 2016/12/12.
 */
@RestController
@RequestMapping(value = "/api/tube/patient")
public class TubePatientController {

    @Autowired
    private DiabetesService diabetesService;

    @Autowired
    private DoctorAccountRepository doctorAccountRepo;

    @Autowired
    private DoctorInfoRepository doctorInfoRepo;

    @Autowired
    private BaseInfoRepository baseInfoRepo;
    /**
     * 在管人群列表
     * @return
     */
    @PostMapping("/list")
    public Pager list(@RequestBody Pager pager){
        Map<String,Object> param = pager.getParameter();
        if(!param.containsKey("doctorId") || null == param.get("doctorId")
                || StringUtils.isEmpty(param.get("doctorId").toString())){
            throw new RequestPostMissingKeyException("doctorId");
        }
        String doctorId = param.get("doctorId").toString();
        DoctorAccount doctor = doctorAccountRepo.findOne(doctorId);
        DoctorInfo doctorInfo = doctorInfoRepo.findOne(doctorId);
        if(null == doctor || null == doctorInfo){
            return pager;
        }

        List<TubePatientDTO> resource = diabetesService.getTubePatientList(doctorInfo.getHospitalId(),doctor.getName(),pager.getNumber(),pager.getSize());
        Integer total = diabetesService.getTubePatientNumber(doctorInfo.getHospitalId(),doctor.getName());
        List<TubePatientEntity> list = Lists.newArrayList();
        for(TubePatientDTO dto : resource){
            list.add(new TubePatientEntity(dto));
        }
        pager.setTotalElements(total);
        pager.setData(list);
        return pager;
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
