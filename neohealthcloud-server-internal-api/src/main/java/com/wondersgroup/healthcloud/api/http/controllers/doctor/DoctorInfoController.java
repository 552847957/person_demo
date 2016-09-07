package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenbin on 16/8/5.
 */

@RestController
@RequestMapping(value = "/api")
public class DoctorInfoController {

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    @Autowired
    private DoctorService doctorService;

    /**
     * 保存医生信息
     * @param
     * @return
     */
    @PostMapping(path = "/doctorInfo/save")
    public JsonResponseEntity saveDoctorInfo(@RequestBody Doctor doctor){

        if(doctor!=null && StringUtils.isNotBlank(doctor.getUid())){
            DoctorInfo doctorInfo = doctorInfoRepository.findOne(doctor.getUid());
            if(doctorInfo!=null){
                doctorInfo.setExpertin(doctor.getExpertin());
                doctorInfo.setIntroduction(doctor.getIntroduction());
                doctorInfo.setDepartStandard(doctor.getDepartStandard());
                doctorInfoRepository.saveAndFlush(doctorInfo);
            }
            DoctorAccount doctorAccount = doctorAccountRepository.findOne(doctor.getUid());
            if(doctorAccount!=null){
                doctorAccount.setIsAvailable(doctor.getIsAvailable());
                doctorAccountRepository.saveAndFlush(doctorAccount);
            }

            return new JsonResponseEntity(0, "保存成功");
        }else{
            return new JsonResponseEntity(3101, "保存失败,参数有误");
        }


    }

    /**
     * 查询医生信息
     * @return
     */
    @GetMapping(path = "/doctorInfo/find")
    public JsonResponseEntity<Doctor> findDoctorInfo(@RequestParam String uid) throws JsonProcessingException {

        JsonResponseEntity<Doctor> response = new JsonResponseEntity<>();

        Doctor doctor = doctorService.findDoctorByUid(uid);

        if(doctor==null){
            response.setCode(3101);
            response.setMsg("不存在的医生");
            return response;
        }
        response.setData(doctor);
        return response;
    }


    /**
     * 用于H5页面 -- 医生详情
     * @param doctorId
     * @return
     * @throws JsonProcessingException
     */
    @GetMapping(path = "/doctor/detail")
    public JsonResponseEntity<Doctor> detail(@RequestParam String doctorId) throws JsonProcessingException {

        JsonResponseEntity<Doctor> response = new JsonResponseEntity<>();

        Doctor doctor = doctorService.findDoctorByUid(doctorId);

        if(doctor==null){
            response.setCode(3101);
            response.setMsg("不存在的医生");
            return response;
        }
        response.setData(doctor);
        return response;
    }

}
