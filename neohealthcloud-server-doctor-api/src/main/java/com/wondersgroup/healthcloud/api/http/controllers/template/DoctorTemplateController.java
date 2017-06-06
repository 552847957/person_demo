package com.wondersgroup.healthcloud.api.http.controllers.template;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.template.MyTemplateDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.template.TemplateDTO;
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



    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<TemplateDTO> update(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String id = reader.readString("id", true);
        String doctorId = reader.readString("doctor_id", false);
        String type = reader.readString("type", false);
        String title = reader.readString("title", false);
        String content = reader.readString("content", false);

        DoctorTemplate doctorTemplate = doctorTemplateService.update(id, doctorId, type, title, content);

        JsonResponseEntity<TemplateDTO> response = new JsonResponseEntity<>();
        response.setData(new TemplateDTO(doctorTemplate));
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

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<MyTemplateDTO> listTemplate(@RequestParam("doctorId") String doctorId) {
        String defaultType = "1";
        List<DoctorTemplate> templates = doctorTemplateService.findByDoctorIdAndType(doctorId, defaultType);
        JsonResponseEntity<MyTemplateDTO> response = new JsonResponseEntity<>();
        MyTemplateDTO dto = new MyTemplateDTO();
        List<TemplateDTO> lastUsed = Lists.newLinkedList();

        List<DoctorTemplate> tmp =doctorTemplateService.findByDoctorIdAndType("123456", defaultType);

        for (DoctorTemplate template : tmp) {
            lastUsed.add(new TemplateDTO(template));
         }
        dto.setLastUsed(lastUsed);

        List<TemplateDTO> dtos = Lists.newLinkedList();
        for (DoctorTemplate template : templates) {
            dtos.add(new TemplateDTO(template));
        }
        dto.setTemplates(dtos);

        response.setData(dto);
        return response;
    }


}


