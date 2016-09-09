package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorAccountDTO;
import com.wondersgroup.healthcloud.api.http.dto.faq.FaqDTO;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.services.doctor.DoctorAccountService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorInforUpdateLengthException;
import com.wondersgroup.healthcloud.services.user.PatientAttentionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/1.
 */
@RestController
@RequestMapping(value = "/api")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;


    @Autowired
    private PatientAttentionService patientAttentionService;

    /**
     * 根据uid获取医生详情
     * @param doctor_id
     * @return
     */
    @RequestMapping(value = "/user/doctorInfo", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity<DoctorAccountDTO> info(@RequestParam(required = false) String uid,
                                                     @RequestParam String doctor_id) {
        DoctorAccountDTO  doctorAccountDTO = getDoctorInfo(uid,doctor_id);
        JsonResponseEntity<DoctorAccountDTO> response = new JsonResponseEntity<>();
        response.setData(doctorAccountDTO);
        return response;
    }

    /**
     * 医生关注列表
     * @param uid
     * @param flag
     * @return
     */
    @VersionRange
    @GetMapping(path = "/user/attentionDoctor/list")
    public JsonListResponseEntity getAttentionDoctorList(
            @RequestParam(required = true) String  uid,
            @RequestParam(required = false, defaultValue = "1") Integer flag){
        JsonListResponseEntity<DoctorAccountDTO> body = new JsonListResponseEntity<>();
        int pageSize = 10;
        boolean has_more = false;
        List<DoctorAccountDTO> list = Lists.newArrayList();

        List<Map<String,Object>> doctorList = patientAttentionService.findAttentionDoctorList(uid,pageSize,flag);

        for (Map<String,Object> docMap : doctorList){
            DoctorAccountDTO doctorAccountDTO = new DoctorAccountDTO(docMap);
            Boolean hasQA = doctorService.checkDoctorHasService(doctorAccountDTO.getUid(),"Q&A");
            doctorAccountDTO.setHasQA(hasQA);
            list.add(doctorAccountDTO);
        }

        if(null != doctorList && doctorList.size() == pageSize){
            List<Map<String,Object>> doctorListMore = patientAttentionService.findAttentionDoctorList(uid,pageSize,flag+1);
            if(null !=doctorListMore && doctorListMore.size()>0 ){
                has_more = true;
                flag = flag +1;
            }
        }

        if (has_more) {
            body.setContent(list, true, null, String.valueOf(flag));
        } else {
            body.setContent(list, false, null, null);
        }
        return body;
    }


    /**
     * 关注医生
     * @param request
     * @return
     */
    @PostMapping(path = "/user/attentionDoctor")
    @ResponseBody
    public JsonResponseEntity<String> doAttention(@RequestBody String request) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String uid = reader.readString("uid", false);
        String doctorid = reader.readString("doctor_id", false);

        Boolean result = patientAttentionService.doAttention(uid, doctorid);
        if (result) {
            response.setMsg("关注成功");
        } else {
            response.setCode(1606);
            response.setMsg("关注失败");
        }
        return response;
    }

    /**
     * 取消关注医生
     * @param uid
     * @param doctorid
     * @return
     */
    @DeleteMapping(path = "/user/attentionDoctor")
    @ResponseBody
    public JsonResponseEntity<String> delAttention(
            @RequestParam(value = "uid", required = true) String uid,
            @RequestParam(value = "doctor_id", required = true) String doctorid) {
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        Boolean result = patientAttentionService.delAttention(uid, doctorid);
        if (result) {
            response.setMsg("取消关注成功");
        } else {
            response.setCode(1607);
            response.setMsg("取消关注失败");
        }
        return response;
    }



    /**
     * 根据医生id获取用户信息
     * @param uid
     * @param doctorId
     * @return
     */
    public DoctorAccountDTO getDoctorInfo(String uid,String doctorId) {
        Map<String,Object> doctor = doctorService.findDoctorInfoByUidAndDoctorId(uid,doctorId);
        if(doctor == null){
            throw new ErrorDoctorAccountNoneException();
        }
        DoctorAccountDTO doctorAccountDTO = new DoctorAccountDTO(doctor);
        Boolean hasQA = doctorService.checkDoctorHasService(doctorId,"Q&A");
        doctorAccountDTO.setHasQA(hasQA);

        return doctorAccountDTO;
    }
}
