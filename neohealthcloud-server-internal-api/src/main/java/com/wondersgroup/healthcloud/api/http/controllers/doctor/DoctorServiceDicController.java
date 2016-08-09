package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorServiceDicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shenbin on 16/8/9.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorServiceDicController {

    @Autowired
    private DoctorServiceDicRepository doctorServiceDicRepository;

    /**
     * 查询服务字典表
     * @return
     */
    @RequestMapping(value = "/doctorService/dic", method = RequestMethod.GET)
    public Object findAllDoctorServiceDic(){
        return doctorServiceDicRepository.findAllDoctorService();
    }
}
