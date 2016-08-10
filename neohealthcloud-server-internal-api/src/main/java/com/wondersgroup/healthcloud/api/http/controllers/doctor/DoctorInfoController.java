package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * Created by shenbin on 16/8/5.
 */

@RestController
@RequestMapping(value = "/api")
public class DoctorInfoController {

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    /**
     * 保存医生信息
     * @param para
     * @return
     */
    @PostMapping(path = "/doctorInfo/save")
    public JsonResponseEntity saveDoctorInfo(@RequestBody Map para){
        DoctorInfo doctorInfo = new MapToBeanUtil<DoctorInfo>().fromMapToBean(DoctorInfo.class, para);
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
    public JsonResponseEntity<DoctorInfo> findDoctorInfo(@RequestParam String id){
        JsonResponseEntity<DoctorInfo> response = new JsonResponseEntity<>();

        DoctorInfo doctorInfo = doctorInfoRepository.findById(id);

        response.setData(doctorInfo);

        return response;
    }
}
