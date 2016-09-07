package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceDicRepository;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shenbin on 16/8/9.
 */
@RestController
@RequestMapping(value = "api")
public class DoctorServiceDicController {

    @Autowired
    private DoctorServiceDicRepository doctorServiceDicRepository;

    /**
     * 查询服务详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "doctorServiceDic/detail", method = RequestMethod.GET)
    public JsonResponseEntity<DoctorServiceDic> findDoctorServiceDicDetail(@RequestParam String id){
        JsonResponseEntity<DoctorServiceDic> response = new JsonResponseEntity<>();
        DoctorServiceDic doctorServiceDic = doctorServiceDicRepository.findById(id);
        if(doctorServiceDic!=null){
            response.setData(doctorServiceDic);
        }else{
            response.setCode(3101);
            response.setMsg("无对应信息");
        }
        return response;
    }

    /**
     * 修改服务字典
     * @param
     * @return
     */
    @RequestMapping(value = "updateDoctorServiceDic", method = RequestMethod.POST)
    public JsonResponseEntity updateDoctorServiceDic(@RequestBody DoctorServiceDic doctorServiceDic) {
        if(doctorServiceDic!=null){
            if(StringUtils.isBlank(doctorServiceDic.getId())){
                doctorServiceDic.setId(IdGen.uuid());
                doctorServiceDic.setCreateDate(new Date());
            }else{
                DoctorServiceDic serviceDic = doctorServiceDicRepository.findOne(doctorServiceDic.getId());
                doctorServiceDic.setDelFlag(serviceDic.getDelFlag());
            }
            doctorServiceDic.setUpdateDate(new Date());
            doctorServiceDicRepository.save(doctorServiceDic);
        }
        return new JsonResponseEntity(0, "修改成功");
    }


    /**
     * 设置上下架
     * @param doctorServiceDic
     * @return
     */
    @RequestMapping(value = "serviceDic/setAvailable", method = RequestMethod.POST)
    public JsonResponseEntity setAvailable(@RequestBody DoctorServiceDic doctorServiceDic) {
        if(doctorServiceDic!=null&& StringUtils.isNotBlank(doctorServiceDic.getId())
                && StringUtils.isNotBlank(doctorServiceDic.getIsAvailable())) {
            DoctorServiceDic serviceDic = doctorServiceDicRepository.findOne(doctorServiceDic.getId());
            if(serviceDic!=null){
                serviceDic.setIsAvailable(doctorServiceDic.getIsAvailable());
                serviceDic.setUpdateDate(new Date());
                doctorServiceDicRepository.save(serviceDic);
                return new JsonResponseEntity(0, "修改成功");
            }

        }
        return new JsonResponseEntity(3101, "修改失败");
    }

}

