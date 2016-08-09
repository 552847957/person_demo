package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorInfoDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInfoRepository;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
     * @param doctorInfoDTO
     * @return
     */
    @PostMapping(path = "/doctorInfo/save")
    public JsonResponseEntity<String> saveDoctorInfo(@RequestBody DoctorInfoDTO doctorInfoDTO){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        DoctorInfo doctorInfo = new DoctorInfo();
        doctorInfo.setId(doctorInfoDTO.getId());
        doctorInfo.setHospitalId(doctorInfoDTO.getHospitalId());
        doctorInfo.setNo(doctorInfoDTO.getNo());
        doctorInfo.setDepartStandard(doctorInfoDTO.getDepartStandard());
        doctorInfo.setIdcard(doctorInfoDTO.getIdcard());
        if(StringUtils.isNotBlank(doctorInfoDTO.getIdcard())){
            doctorInfo.setGender(IdcardUtils.getGenderByIdCard(doctorInfoDTO.getIdcard()));
        }
        doctorInfo.setDutyId(doctorInfoDTO.getDutyId());
        doctorInfo.setExpertin(doctorInfoDTO.getExpertin());
        doctorInfo.setIntroduction(doctorInfoDTO.getIntroduction());
        doctorInfo.setActcode(doctorInfoDTO.getActcode());
        doctorInfo.setDelFlag(doctorInfoDTO.getDelFlag());
        doctorInfo.setSourceId(doctorInfoDTO.getSourceId());
        doctorInfo.setCreateDate(new Date());
        doctorInfo.setUpdateDate(new Date());
        doctorInfoRepository.saveAndFlush(doctorInfo);

        response.setMsg("保存成功");

        return response;
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
