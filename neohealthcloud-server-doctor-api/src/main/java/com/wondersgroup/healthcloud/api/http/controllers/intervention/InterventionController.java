package com.wondersgroup.healthcloud.api.http.controllers.intervention;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.OutlierDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.PersonDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by longshasha on 17/5/18.
 */
@RestController
@RequestMapping("/api/doctor/intervention")
public class InterventionController {

    @Autowired
    private DoctorIntervenService doctorInterventionService;

    /**
     *
     * @param uid 医生registerId
     * @param flag
     * @param sign 0-非签约 1-签约
     * @param interven_type 异常类型
     * @return
     */
    @RequestMapping(value = "/todoList", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<PersonDTO> toDoList(@RequestParam String uid,
                                                      @RequestParam(defaultValue = "") String sign,
                                                      @RequestParam(defaultValue = "") String interven_type,
                                                      @RequestParam(defaultValue = "", required = false) String flag) {

        JsonListResponseEntity<PersonDTO> response = new JsonListResponseEntity<>();
        List<PersonDTO> toDoList = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<IntervenEntity> interventionList = doctorInterventionService.findTodoInterveneList(uid, sign, interven_type, pageNo, pageSize+1);

        for (IntervenEntity intervenEntity : interventionList){
            if(toDoList.size()<pageSize){
                PersonDTO  personDTO = new PersonDTO(intervenEntity);
                toDoList.add(personDTO);
            }
        }

        if(interventionList.size()>pageSize){
            more = true;
            flag = String.valueOf(pageNo + 1);
        }
        response.setContent(toDoList, more, null, flag);

        return response;
    }

    /**
     * 未干预的血糖异常
     * @param registerId
     * @param is_all
     * @param flag
     * @param size
     * @return
     */
    @RequestMapping(value = "/bloodGlucose", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<OutlierDTO> bloodGlucose(@RequestParam String registerId,
                                                      @RequestParam(defaultValue = "false") Boolean is_all,
                                                      @RequestParam(defaultValue = "0") String flag,
                                                      @RequestParam(defaultValue = "4") String size) {

        JsonListResponseEntity<OutlierDTO> response = new JsonListResponseEntity<>();
        List<OutlierDTO> outlierDTOs = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<NeoFamIntervention> outlierList = doctorInterventionService.findBloodGlucoseOutlierListByRegisterId(registerId, is_all, pageNo, pageSize+1, Integer.valueOf(size)+1);

        for (NeoFamIntervention neoFamIntervention : outlierList){
            if(outlierDTOs.size()<pageSize){
                OutlierDTO  outlierDTO = new OutlierDTO(neoFamIntervention);
                outlierDTO.setFlag(outlierDTO.getFlag());
                outlierDTOs.add(outlierDTO);
            }
        }
        if(is_all && outlierList.size()>pageSize){
            more = true;
            flag = String.valueOf(pageNo + 1);
        }else if(!is_all && outlierList.size()> Integer.valueOf(size)){
            more = true;
            flag = String.valueOf(pageNo + 1);
        }

        response.setContent(outlierDTOs, more, null, flag);

        return response;
    }

    /**
     * 未干预的血压
     * @param registerId
     * @param is_all
     * @param flag
     * @param size
     * @return
     */
    @RequestMapping(value = "/pressure", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<OutlierDTO> pressure(@RequestParam String registerId,
                                                           @RequestParam(defaultValue = "false") Boolean is_all,
                                                           @RequestParam(defaultValue = "0") String flag,
                                                           @RequestParam(defaultValue = "4") String size) {

        JsonListResponseEntity<OutlierDTO> response = new JsonListResponseEntity<>();
        List<OutlierDTO> outlierDTOs = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<NeoFamIntervention> outlierList = doctorInterventionService.findpressureOutlierListByRegisterId(registerId, is_all, pageNo, pageSize+1, Integer.valueOf(size)+1);

        for (NeoFamIntervention neoFamIntervention : outlierList){
            if(outlierDTOs.size()<pageSize){
                OutlierDTO  outlierDTO = new OutlierDTO(neoFamIntervention);
                outlierDTO.setSystolicFlag(String.valueOf(outlierDTO.getSystolicFlag()));
                outlierDTO.setDiastolicFlag(String.valueOf(outlierDTO.getDiastolicFlag()));
                outlierDTOs.add(outlierDTO);
            }
        }
        if(is_all && outlierList.size()>pageSize){
            more = true;
            flag = String.valueOf(pageNo + 1);
        }else if(!is_all && outlierList.size()> Integer.valueOf(size)){
            more = true;
            flag = String.valueOf(pageNo + 1);
        }

        response.setContent(outlierDTOs, more, null, flag);

        return response;
    }

    /**
     * 干预 保存医生建议
     * @param body
     * @return
     */
    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> updateIntro(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        String doctorId = reader.readString("doctorId", false);
        String patientId = reader.readString("patientId", false);
        String content = reader.readString("content", false);

        doctorInterventionService.intervenSaveOrUpdate(doctorId,patientId,content);

        response.setMsg("干预成功");
        return response;
    }

}
