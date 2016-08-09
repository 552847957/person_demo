package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    public JsonResponseEntity<List<DoctorAccountDTO>> findAllDoctorList(Pageable pageable){
        JsonResponseEntity<List<DoctorAccountDTO>> response = new JsonResponseEntity<>();

        Page<DoctorAccount> doctorAccounts = doctorAccountRepository.findAll(pageable);

        List<DoctorAccountDTO> doctorAccountDTOs = new ArrayList<>();

        for (DoctorAccount doctorAccount : doctorAccounts) {
            DoctorAccountDTO doctorAccountDTO = new DoctorAccountDTO();
            doctorAccountDTO.setId(doctorAccount.getId());
            doctorAccountDTO.setName(doctorAccount.getName());
            doctorAccountDTO.setAvatar(doctorAccount.getAvatar());
            doctorAccountDTO.setNickname(doctorAccount.getNickname());
            doctorAccountDTO.setMobile(doctorAccount.getMobile());
            doctorAccountDTO.setIsAvailable(doctorAccount.getIsAvailable());
            doctorAccountDTOs.add(doctorAccountDTO);
        }
        response.setData(doctorAccountDTOs);

        return response;
    }

}
