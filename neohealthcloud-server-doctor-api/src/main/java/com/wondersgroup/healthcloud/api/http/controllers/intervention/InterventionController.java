package com.wondersgroup.healthcloud.api.http.controllers.intervention;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.IntervenDetailDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.OutlierDTO;
import com.wondersgroup.healthcloud.api.http.dto.doctor.intervention.PersonDTO;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorUsedTemplate;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.doctor.DoctorTemplateService;
import com.wondersgroup.healthcloud.services.interven.DoctorIntervenService;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import com.wondersgroup.healthcloud.services.user.UserService;
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

    @Autowired
    private DoctorTemplateService doctorTemplateService;

    @Autowired
    private UserService userService;


    /**
     *
     * @param doctorId 医生Id
     * @param flag
     * @param sign 0-非签约 1-签约
     * @param interven_type 异常类型
     * @param name 如果name不为空则为搜索 其他参数忽略
     * @return
     */
    @RequestMapping(value = "/todoList", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<PersonDTO> toDoList(@RequestParam String doctorId,
                                                      @RequestParam(defaultValue = "") String sign,
                                                      @RequestParam(defaultValue = "") String interven_type,
                                                      @RequestParam(defaultValue = "", required = false) String flag,
                                                      @RequestParam(defaultValue = "",required = false) String name) {

        JsonListResponseEntity<PersonDTO> response = new JsonListResponseEntity<>();
        List<PersonDTO> toDoList = Lists.newArrayList();
        boolean more = false;
        int pageNo = 0;
        if(StringUtils.isNotBlank(flag)){
            pageNo = Integer.valueOf(flag);
        }
        int pageSize = 20;
        List<IntervenEntity> interventionList = doctorInterventionService.findTodoInterveneList(name,doctorId, sign, interven_type, pageNo, pageSize);

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
     * 未干预的血糖异常(查看全部)
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
        List<NeoFamIntervention> outlierList = doctorInterventionService.findBloodGlucoseOutlierListByRegisterId(registerId, is_all, pageNo, pageSize, Integer.valueOf(size)+1);

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
     * 未干预的血压(查看全部)
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
        List<NeoFamIntervention> outlierList = doctorInterventionService.findpressureOutlierListByRegisterId(registerId, is_all, pageNo, pageSize, Integer.valueOf(size)+1);

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
        String templateId = reader.readString("templateId", true);//模板Id

        doctorInterventionService.intervenSaveOrUpdate(doctorId,patientId,content);

        //调用模板接口
        if(StringUtils.isNotBlank(templateId)){
            doctorTemplateService.saveDoctorUsedTemplate(new DoctorUsedTemplate(doctorId,templateId));
        }

        response.setMsg("干预成功");
        return response;
    }

    /**
     * 异常干预 近期异常详情
     * @param registerId
     * @param size
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<IntervenDetailDTO> detail(@RequestParam String registerId,
                                                           @RequestParam(defaultValue = "4") String size) {

        JsonResponseEntity<IntervenDetailDTO> response = new JsonResponseEntity<>();
        List<OutlierDTO> bloodGlucoseOutlierDTOs = Lists.newArrayList();
        List<OutlierDTO> pressureOutlierDTOs = Lists.newArrayList();
        IntervenDetailDTO intervenDetailDTO = new IntervenDetailDTO();

        /**
         * 如果不是C端用户则直接返回空
         */
        RegisterInfo registerInfo = userService.findOne(registerId);
        if(registerInfo == null){
            response.setData(intervenDetailDTO);
            return response;
        }

        IntervenEntity intervenEntity = doctorInterventionService.getUserDiseaseLabelByRegisterId(registerId);
        Boolean canIntervention = doctorInterventionService.hasTodoIntervensByRegisterId(registerId);
        String typeList = doctorInterventionService.findNotDealInterveneTypes(registerId);
        PersonDTO personDTO = new PersonDTO(intervenEntity);
        String interveneTypeNames = "";
        if(StringUtils.isNotBlank(typeList)){
            interveneTypeNames = IntervenEnum.getIntervenTypeNames(typeList);
        }
        personDTO.setMemo(interveneTypeNames);
        personDTO.setCanIntervene(canIntervention);

        intervenDetailDTO.setPersonDTO(personDTO);
        //血糖
        List<NeoFamIntervention> bloodGlucoseOutlierList = doctorInterventionService.findBloodGlucoseOutlierListByRegisterId(registerId, false, 0, 0, Integer.valueOf(size)+1);

        if(bloodGlucoseOutlierList!=null && bloodGlucoseOutlierList.size()>0){
            for (NeoFamIntervention neoFamIntervention : bloodGlucoseOutlierList){
                if(bloodGlucoseOutlierDTOs.size()<Integer.valueOf(size)){
                    OutlierDTO  outlierDTO = new OutlierDTO(neoFamIntervention);
                    outlierDTO.setFlag(outlierDTO.getFlag());
                    bloodGlucoseOutlierDTOs.add(outlierDTO);
                }
            }
            intervenDetailDTO.setBloodGlucoseList(bloodGlucoseOutlierDTOs);
            if(bloodGlucoseOutlierList.size()> Integer.valueOf(size)){
                intervenDetailDTO.setBloodGlucose_more(true);
            }
        }


        //血压
        List<NeoFamIntervention> pressureOutlierList = doctorInterventionService.findpressureOutlierListByRegisterId(registerId, false, 0, 0, Integer.valueOf(size) + 1);
        if(pressureOutlierList!=null && pressureOutlierList.size()>0){
            for (NeoFamIntervention neoFamIntervention : pressureOutlierList){
                if(pressureOutlierDTOs.size()<Integer.valueOf(size)){
                    OutlierDTO  outlierDTO = new OutlierDTO(neoFamIntervention);
                    outlierDTO.setFlag(outlierDTO.getFlag());
                    pressureOutlierDTOs.add(outlierDTO);
                }
            }
            intervenDetailDTO.setPressureList(pressureOutlierDTOs);
            if(pressureOutlierList.size()> Integer.valueOf(size)){
                intervenDetailDTO.setPressure_more(true);
            }
        }

        response.setData(intervenDetailDTO);

        return response;
    }

}
