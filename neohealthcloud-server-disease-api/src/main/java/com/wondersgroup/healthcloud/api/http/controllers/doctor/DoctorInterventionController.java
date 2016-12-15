package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import java.util.List;

import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

@RestController
@RequestMapping("/api/doctorIntervention")
public class DoctorInterventionController {
    @Autowired
    private DoctorInterventionService doctorInterventionService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonResponseEntity list(@RequestParam(name = "uid", required = true) String patientId,
                                   @RequestParam(name = "type", required = true) String type) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention doctorIntervention = new DoctorIntervention();
        doctorIntervention.setPatientId(patientId);
        doctorIntervention.setType(type);
        List<DoctorIntervention> rtnList = doctorInterventionService.list(doctorIntervention);
        if(rtnList != null && rtnList.size() > 0) {
            result.setData(rtnList);
        } else {
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/saveAndUpdate", method = RequestMethod.POST)
    public JsonResponseEntity saveAndUpdate(@RequestBody DoctorIntervention doctorIntervention) {
        JsonResponseEntity result = new JsonResponseEntity();
        DoctorIntervention rtnDoctorIntervention = doctorInterventionService.saveAndUpdate(doctorIntervention);
        if(rtnDoctorIntervention != null) {
            result.setMsg("数据保存成功！");
        } else {
            result.setCode(1000);
            result.setMsg("数据保存失败！");
        }
        return result;
    }

}