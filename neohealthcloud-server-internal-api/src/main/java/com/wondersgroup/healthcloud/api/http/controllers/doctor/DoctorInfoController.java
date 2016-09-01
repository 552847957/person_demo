package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
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
    private DoctorService doctorService;

    /**
     * 保存医生信息
     * @param para
     * @return
     */
    @PostMapping(path = "/doctorInfo/save")
    public JsonResponseEntity saveDoctorInfo(@RequestBody Map para){
        DoctorInfo doctorInfo = MapToBeanUtil.fromMapToBean(DoctorInfo.class, para);
        doctorInfo.setCreateDate(new Date());
        doctorInfo.setUpdateDate(new Date());
        doctorInfoRepository.saveAndFlush(doctorInfo);

        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 查询医生信息
     * @return
     */
    @GetMapping(path = "/doctorInfo/find")
    public String findDoctorInfo(@RequestParam String id) throws JsonProcessingException {
        DoctorInfo doctorInfo = doctorInfoRepository.findById(id);

        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorInfo.class, new String[]{"create_by", "create_date", "update_by", "update_date"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", doctorInfo);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
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
