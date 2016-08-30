package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceDicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value = "doctorServiceDic/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String findDoctorServiceDicDetail(@RequestParam String id) throws JsonProcessingException {
        DoctorServiceDic doctorServiceDic = doctorServiceDicRepository.findById(id);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(DoctorServiceDic.class, new String[]{"create_by", "create_date", "update_by", "update_date"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", doctorServiceDic);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 修改服务
     * @param para
     * @return
     */
    @RequestMapping(value = "updateDoctorServiceDic", method = RequestMethod.POST)
    public JsonResponseEntity updateDoctorServiceDic(@RequestBody Map para) {
        DoctorServiceDic doctorServiceDic = MapToBeanUtil.fromMapToBean(DoctorServiceDic.class, para);
        doctorServiceDicRepository.save(doctorServiceDic);

        return new JsonResponseEntity(0, "修改成功");
    }

}

