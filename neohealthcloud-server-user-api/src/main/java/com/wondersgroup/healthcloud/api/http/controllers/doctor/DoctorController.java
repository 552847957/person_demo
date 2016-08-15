package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorInforUpdateLengthException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;


    /**
     * 根据uid获取医生详情
     * @param doctor_id
     * @return
     */
    @RequestMapping(value = "/user/doctorInfo", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<DoctorAccountDTO> info(@RequestParam String doctor_id) {
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(doctor_id);
        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        response.setData(doctorAccountDTO);
        return response;
    }




    /**
     * 根据医生id获取用户信息
     * @param id
     * @return
     */
    public DoctorAccountDTO getDoctorInfo(String id) {
        Map<String,Object> doctor = doctorService.findDoctorInfoByUid(id);
        if(doctor == null){
            throw new ErrorDoctorAccountNoneException();
        }
        DoctorAccountDTO doctorAccountDTO = new DoctorAccountDTO(doctor);

        return doctorAccountDTO;
    }
}
