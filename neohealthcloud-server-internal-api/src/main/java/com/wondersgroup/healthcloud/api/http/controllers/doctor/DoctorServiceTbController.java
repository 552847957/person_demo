package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @RequestMapping(value = "/doctorService/save", method = RequestMethod.POST)
    public JsonResponseEntity saveDoctorService(@RequestParam String doctorId,
                                                @RequestParam List<String> serviceId){
        DoctorInfo doctor = doctorInfoRepository.findById(doctorId);

        if (doctor != null) {
            doctorServiceRepository.removeServiceByUid(doctorId);
            List<DoctorServiceEntity> doctorServiceEntityList = new ArrayList<>();
            for (String id : serviceId) {
                DoctorServiceEntity doctorServiceEntity = new DoctorServiceEntity();
                doctorServiceEntity.setId(IdGen.uuid());
                doctorServiceEntity.setDoctorId(doctorId);
                doctorServiceEntity.setServiceId(id);
                doctorServiceEntity.setCreateDate(new Date());
                doctorServiceEntity.setUpdateDate(new Date());
                doctorServiceEntityList.add(doctorServiceEntity);
            }
            doctorServiceRepository.save(doctorServiceEntityList);
            return new JsonResponseEntity(0, "保存成功");
        }
        throw new ErrorDoctorAccountNoneException();
    }
}
