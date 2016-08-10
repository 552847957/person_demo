package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceEntity;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceRepository;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by shenbin on 16/8/9.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorServiceTbController {

    @Autowired
    private DoctorServiceRepository doctorServiceRepository;

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    /**
     * 保存医生服务
     * @param doctorId
     * @param serviceId
     * @return
     */
    @RequestMapping(value = "/doctorService/save", method = RequestMethod.GET)
    public JsonResponseEntity<String> saveDoctorService(@RequestParam String doctorId,
                                                        @RequestParam String serviceId){
        DoctorInfo doctor = doctorInfoRepository.findById(doctorId);

        if (doctor != null) {
            JsonResponseEntity<String> response = new JsonResponseEntity<>();
            DoctorServiceEntity doctorServiceEntity = new DoctorServiceEntity();
            doctorServiceEntity.setDoctorId(doctorId);
            doctorServiceEntity.setServiceId(serviceId);
            doctorServiceEntity.setCreateDate(new Date());
            doctorServiceEntity.setUpdateDate(new Date());
            doctorServiceRepository.saveAndFlush(doctorServiceEntity);
            response.setMsg("保存成功");
            return response;
        }
        throw new ErrorDoctorAccountNoneException();
    }
}
