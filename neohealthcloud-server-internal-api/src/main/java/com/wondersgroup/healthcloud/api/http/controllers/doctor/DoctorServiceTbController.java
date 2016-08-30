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
            iteratorDoctorService(serviceId, doctorId, doctorServiceEntityList);
            doctorServiceRepository.save(doctorServiceEntityList);
            return new JsonResponseEntity(0, "保存成功");
        }
        throw new ErrorDoctorAccountNoneException();
    }

    /**
     * 批量保存医生服务
     * @param doctorId
     * @param serviceId
     * @return
     */
    @RequestMapping(value = "/doctorService/batchSave", method = RequestMethod.POST)
    public JsonResponseEntity batchSaveDoctorService(@RequestParam List<String> doctorId,
                                                     @RequestParam List<String> serviceId) {
        for (String id : doctorId) {
            DoctorInfo doctor = doctorInfoRepository.findById(id);

            if (doctor != null) {
                doctorServiceRepository.removeServiceByUid(id);
                List<DoctorServiceEntity> doctorServiceEntityList = new ArrayList<>();
                iteratorDoctorService(serviceId, id, doctorServiceEntityList);
                doctorServiceRepository.save(doctorServiceEntityList);
            } else {
                throw new ErrorDoctorAccountNoneException();
            }
        }
        return new JsonResponseEntity(0, "保存成功");
    }

    private void iteratorDoctorService(List<String> serviceId,
                                       String id,
                                       List<DoctorServiceEntity> doctorServiceEntityList) {
        for (String service : serviceId) {
            DoctorServiceEntity doctorServiceEntity = new DoctorServiceEntity();
            doctorServiceEntity.setId(IdGen.uuid());
            doctorServiceEntity.setDoctorId(id);
            doctorServiceEntity.setServiceId(service);
            doctorServiceEntity.setCreateDate(new Date());
            doctorServiceEntity.setUpdateDate(new Date());
            doctorServiceEntityList.add(doctorServiceEntity);
        }
    }
}
