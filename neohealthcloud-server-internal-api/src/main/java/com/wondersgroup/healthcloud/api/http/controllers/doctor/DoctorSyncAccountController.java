package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.wondersgroup.healthcloud.api.http.dto.doctor.SyncRequestDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.SyncResponseDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.services.doctor.DoctorSyncAccountService;
import com.wondersgroup.healthcloud.services.doctor.exception.SyncDoctorAccountException;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by longshasha on 16/8/2.
 */
@RestController
@RequestMapping(value = "/doctor")
public class DoctorSyncAccountController {

    @Autowired
    private DoctorSyncAccountService doctorSyncAccountService;


    /**
     * 开通万达云账号
     * @param
     * @return
     */
    @PostMapping(path = "/account/open")
    public JsonResponseEntity<SyncResponseDTO> openAccount(@RequestBody(required = true) SyncRequestDTO syncRequest){
        JsonResponseEntity<SyncResponseDTO> response = new JsonResponseEntity<>();
        validateSyncRequest(syncRequest);

        DoctorAccount doctorAccount = doctorSyncAccountService.findDoctorByMobileWithOutDelfag(syncRequest.getMobile());

        if(doctorAccount != null && "0".equals(doctorAccount.getDelFlag())){
            SyncResponseDTO syncResponseDTO = new SyncResponseDTO(doctorAccount);
            response.setCode(1);
            response.setMsg("该手机号已经开通过万达云账号");
            response.setData(syncResponseDTO);
            return response;
        }


        if(doctorAccount==null){
            doctorAccount = new DoctorAccount();
            doctorAccount.setCreateDate(new Date());
            doctorAccount.setRegtime(new Date());
        }

        doctorAccount.setMobile(syncRequest.getMobile());
        doctorAccount.setName(syncRequest.getName());


        DoctorInfo doctorInfo = new DoctorInfo();
        doctorInfo.setDutyId(syncRequest.getDutyId());
        doctorInfo.setHospitalId(syncRequest.getHospitalId());
        doctorInfo.setIdcard(syncRequest.getIdcard());
        doctorInfo.setDutyId(syncRequest.getDutyId());
        if(StringUtils.isNotBlank(syncRequest.getIdcard())){
            doctorInfo.setGender(IdcardUtils.getGenderByIdCard(syncRequest.getIdcard()));
        }

        doctorAccount = doctorSyncAccountService.openWonderCloudAccount(doctorAccount,doctorInfo,syncRequest.getRoles());

        SyncResponseDTO syncResponseDTO = new SyncResponseDTO(doctorAccount);


        response.setData(syncResponseDTO);

        return response;
    }


    /**
     * 关闭万达云账号
     * @param
     * @return
     */
    @DeleteMapping(path = "/account/close")
    public JsonResponseEntity<String> closeAccount(@RequestParam String register_id ){
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        doctorSyncAccountService.closeWonderCloudAccount(register_id);
        response.setMsg("解除成功");
        return response;
    }

    /**
     * 验证传参
     * @param syncRequestDTO
     * //后期可以添加业务方面的验证 比如传的医疗机构代码不存在  todo
     */
    public void validateSyncRequest(SyncRequestDTO syncRequestDTO){
        if(syncRequestDTO == null){
            throw new SyncDoctorAccountException();
        }
        if(StringUtils.isBlank(syncRequestDTO.getMobile())){
            throw new SyncDoctorAccountException("医生手机号不能为空");
        }
        if(StringUtils.isBlank(syncRequestDTO.getName())){
            throw new SyncDoctorAccountException("医生姓名不能为空");
        }
        if(StringUtils.isBlank(syncRequestDTO.getHospitalId())){
            throw new SyncDoctorAccountException("医生所属医疗机构不能为空");
        }

    }

}
