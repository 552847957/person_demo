package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shenbin on 16/8/5.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorAccountController {

    @Autowired
    private DoctorAccountRepository doctorAccountRepository;

    /**
     * 查询医生列表
     * @param pageable
     * @return
     */
    @GetMapping(path = "/doctor/list")
    public JsonResponseEntity<ObjectNode> findAllDoctorList(Pageable pageable){
        return null;
    }
}
