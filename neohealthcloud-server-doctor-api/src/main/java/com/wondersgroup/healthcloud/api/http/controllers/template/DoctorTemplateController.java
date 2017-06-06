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

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/doctor/template")
public class DoctorTemplateController {

    @Autowired
    private DoctorTemplateService doctorTemplateService;

    private String defaultType = "1";





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

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<TemplateDTO> detail(@RequestParam("doctorId") String doctorId,String id) {
        DoctorTemplate template = doctorTemplateService.findOne(id);
        JsonResponseEntity<TemplateDTO> response = new JsonResponseEntity<>();
        if(null != template){
            response.setData(new TemplateDTO(template));
        }else{
            response.setCode(-1);
            response.setMsg("无数据");
        }
        return response;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity add(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctorId = reader.readString("doctorId", false);
        String title = reader.readString("title", false);
        String content = reader.readString("content", false);

        DoctorTemplate entity = new DoctorTemplate();
        entity.setTitle(title);//TODO　数据检查
        entity.setContent(content);
        entity.setDoctorId(doctorId);
        entity.setType("1");
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());

        doctorTemplateService.saveTemplate(entity);
        JsonResponseEntity response = new JsonResponseEntity<>();
        response.setData("添加成功");
        response.setCode(0);
        return response;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity edit(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String id = reader.readString("id", false);
        String doctorId = reader.readString("doctorId", false);
        String title = reader.readString("title", false);
        String content = reader.readString("content", false);


        //TODO 数据检查

        doctorTemplateService.update(id, doctorId, defaultType, title, content);
        JsonResponseEntity response = new JsonResponseEntity<>();
        response.setData("编辑成功");
        response.setCode(0);
        return response;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> delete(@RequestBody String body) {
        JsonKeyReader reader = new JsonKeyReader(body);
        String id = reader.readString("id", false);
        String doctorId = reader.readString("doctorId", false);
        doctorTemplateService.deleteOne(id);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        response.setMsg("删除成功");
        return response;
    }

}


