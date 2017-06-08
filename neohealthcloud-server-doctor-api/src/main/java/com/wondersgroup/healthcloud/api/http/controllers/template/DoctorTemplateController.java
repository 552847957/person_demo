package com.wondersgroup.healthcloud.api.http.controllers.template;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.template.MyTemplateDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.template.TemplateDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorTemplate;
import com.wondersgroup.healthcloud.services.doctor.DoctorTemplateService;
import org.apache.commons.collections.CollectionUtils;
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



    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<MyTemplateDTO> listAll(@RequestParam("doctorId") String doctorId) {
        String defaultType = "1";
        JsonResponseEntity<MyTemplateDTO> response = new JsonResponseEntity<>();

        List<DoctorTemplate> templates = doctorTemplateService.findByDoctorIdAndType(doctorId, defaultType);
        Integer count = doctorTemplateService.findDoctorTemplateCount(doctorId, defaultType);
        MyTemplateDTO dto = new MyTemplateDTO();
        dto.setTotalCount(count == null ? 0:count);
        dto.setCurrentIndex(CollectionUtils.isEmpty(templates)?0:templates.size());

        List<TemplateDTO> dtos = Lists.newLinkedList();
        for (DoctorTemplate template : templates) {
            dtos.add(new TemplateDTO(template));
        }
        dto.setTemplates(dtos);

        response.setData(dto);
        return response;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<MyTemplateDTO> lastUsedList(@RequestParam("doctorId") String doctorId) {
        String defaultType = "1";
        JsonResponseEntity<MyTemplateDTO> response = new JsonResponseEntity<>();

        List<DoctorTemplate> templates = doctorTemplateService.findByDoctorIdAndType(doctorId, defaultType);
        Integer count = doctorTemplateService.findDoctorTemplateCount(doctorId, defaultType);
        MyTemplateDTO dto = new MyTemplateDTO();
        dto.setTotalCount(count == null ? 0:count);
        dto.setCurrentIndex(CollectionUtils.isEmpty(templates)?0:templates.size());

        List<TemplateDTO> lastUsed = Lists.newLinkedList();

        List<DoctorTemplate> last =doctorTemplateService.findLastUsedTemplate(doctorId);

        for (DoctorTemplate template : last) {
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
        JsonResponseEntity response = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(body);
        String doctorId = reader.readString("doctorId", false);
        String title = reader.readString("title", false);
        String content = reader.readString("content", false);

        Integer count = doctorTemplateService.findDoctorTemplateCount(doctorId, defaultType);
        if(count > 30){
            response.setData("添加失败");
            response.setCode(-1);
            return response;
        }

        DoctorTemplate entity = new DoctorTemplate();
        entity.setTitle(title);//TODO　数据检查
        entity.setContent(content);
        entity.setDoctorId(doctorId);
        entity.setType("1");
        entity.setUpdateTime(new Date());
        entity.setCreateTime(new Date());

        doctorTemplateService.saveTemplate(entity);
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


