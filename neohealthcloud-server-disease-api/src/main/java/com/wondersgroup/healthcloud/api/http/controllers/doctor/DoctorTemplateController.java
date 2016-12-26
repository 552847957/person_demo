package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorTemplateDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import com.wondersgroup.healthcloud.services.doctor.DoctorTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/doctor/template")
public class DoctorTemplateController {

    @Autowired
    private DoctorTemplateService doctorTemplateService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<List<DoctorTemplateDTO>> getList(@RequestParam("doctor_id") String doctorId,
                                                             @RequestParam String type) {
        List<DoctorTemplate> templates = doctorTemplateService.findByDoctorIdAndType(doctorId, type);
        JsonResponseEntity<List<DoctorTemplateDTO>> response = new JsonResponseEntity<>();
        List<DoctorTemplateDTO> dtos = Lists.newLinkedList();
        for (DoctorTemplate template : templates) {
            dtos.add(new DoctorTemplateDTO(template));
        }
        response.setData(dtos);
        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<DoctorTemplateDTO> update(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String id = reader.readString("id", true);
        String doctorId = reader.readString("doctor_id", false);
        String type = reader.readString("type", false);
        String title = reader.readString("title", false);
        String content = reader.readString("content", false);

        DoctorTemplate doctorTemplate = doctorTemplateService.update(id, doctorId, type, title, content);

        JsonResponseEntity<DoctorTemplateDTO> response = new JsonResponseEntity<>();
        response.setData(new DoctorTemplateDTO(doctorTemplate));
        return response;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @VersionRange
    public JsonResponseEntity<String> delete(@RequestParam String id) {
        doctorTemplateService.deleteOne(id);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        response.setMsg("删除成功");
        return response;
    }
}
