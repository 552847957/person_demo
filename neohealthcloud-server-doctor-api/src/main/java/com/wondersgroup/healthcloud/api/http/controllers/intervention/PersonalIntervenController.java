package com.wondersgroup.healthcloud.api.http.controllers.intervention;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.OutlierDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.PersonDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by longshasha on 17/5/27.
 */
@RestController
@RequestMapping("/api/doctor/personal")
public class PersonalIntervenController {

    @Autowired
    private DoctorIntervenService doctorInterventionService;

    /**
     *
     * @param uid 医生registerId
     * @param flag
     * @return
     */
    @RequestMapping(value = "/interventionList", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<PersonDTO> interventionList(@RequestParam String uid,
                                                      @RequestParam(defaultValue = "", required = false) String flag) {

        JsonListResponseEntity<PersonDTO> response = new JsonListResponseEntity<>();
        List<PersonDTO> toDoList = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<IntervenEntity> interventionList = doctorInterventionService.findPersonalInterveneList(uid, pageNo, pageSize + 1);

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
     * 我的干预详情血糖异常
     * @param interventionId
     * @param is_all
     * @param flag
     * @param size
     * @return
     */
    @RequestMapping(value = "/intervention/bloodGlucose", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<OutlierDTO> bloodGlucose(@RequestParam(required = true) String interventionId,
                                                           @RequestParam(defaultValue = "false") Boolean is_all,
                                                           @RequestParam(defaultValue = "0") String flag,
                                                           @RequestParam(defaultValue = "3") String size) {

        JsonListResponseEntity<OutlierDTO> response = new JsonListResponseEntity<>();
        List<OutlierDTO> outlierDTOs = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<NeoFamIntervention> outlierList = doctorInterventionService.findBloodGlucoseOutlierListByInterventionId(interventionId, is_all, pageNo, pageSize + 1, Integer.valueOf(size) + 1);

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
     * 我的干预详情的血压异常
     * @param interventionId
     * @param is_all
     * @param flag
     * @param size
     * @return
     */
    @RequestMapping(value = "/intervention/pressure", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<OutlierDTO> pressure(@RequestParam(required = true) String interventionId,
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
        List<NeoFamIntervention> outlierList = doctorInterventionService.findpressureOutlierListByInterventionId(interventionId, is_all, pageNo, pageSize + 1, Integer.valueOf(size)+1);

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

}
