package com.wondersgroup.healthcloud.api.http.controllers.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wondersgroup.healthcloud.common.http.annotations.JsonEncode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.doctor.group.PatientGroupDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.DoctorTubeSignUser;
import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;
import com.wondersgroup.healthcloud.jpa.entity.group.SignUserDoctorGroup;
import com.wondersgroup.healthcloud.jpa.repository.group.PatientGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.services.disease.DoctorTubeSignUserService;
import com.wondersgroup.healthcloud.services.disease.dto.ResidentInfoDto;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;

/**
 * 
 * @author zhongshuqing
 *
 */
@Controller
@RequestMapping(value="/api/group")
public class PatientGroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientGroupController.class);
    @Autowired
    private PatientGroupService patientGroupService;
    @Autowired
    PatientGroupRepository patientGroupRepository;
    @Autowired
    private DoctorTubeSignUserService doctorTubeSignUserService;
    @Autowired
    SignUserDoctorGroupRepository signUserDoctorGroupRepository;
    
    /**
     * 获取当前医生id下面的分组信息
     * @param doctorId
     * @return
     */
    @VersionRange
    @GetMapping(value="/list")
    @JsonEncode(encode = true)
    public JsonListResponseEntity<PatientGroupDto> getGroupByDoctorId(@RequestParam(value="uid",required=true) String doctorId){
        JsonListResponseEntity<PatientGroupDto> entity = new JsonListResponseEntity<>();
        List<PatientGroupDto> list = new ArrayList<>();
        try {
            List<PatientGroup> patientList = patientGroupService.getPatientGroupByDoctorId(doctorId);
            for(PatientGroup p:patientList){
                PatientGroupDto dto = new PatientGroupDto();
                dto.setId(p.getId());
                dto.setName(p.getName());
                dto.setIsDefault(p.getIsDefault());
                dto.setSort(p.getRank()+"");
                dto.setCreateDate(PatientGroupDto.dateToString(p.getCreateTime()));
                int patientNum=signUserDoctorGroupRepository.getNumByGroupId(p.getId());
                dto.setPatientNum(patientNum);
                list.add(dto);
            }
            entity.setContent(list);
            return entity;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entity;
        
    }

    @VersionRange
    @GetMapping(value = "/patientList")
    @JsonEncode(encode = true)
    public JsonListResponseEntity<ResidentInfoDto> patientList(@RequestParam(value = "groupId", required = true) Integer groupId,
                                                               @RequestParam(required = true) String doctorId,
                                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                               @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
        JsonListResponseEntity listResponseEntity = new JsonListResponseEntity();
        Page<DoctorTubeSignUser> pageData = doctorTubeSignUserService.queryByGroupId(groupId, page, pageSize);
        List<ResidentInfoDto> dtoList = doctorTubeSignUserService.pageDataToDtoList(doctorId, pageData);
        dtoList = doctorTubeSignUserService.sortTheGroupedResidents(dtoList,groupId);
        if (dtoList.size() > 0) {
            for (ResidentInfoDto residentInfoDto : dtoList) {
                // 全部是已分组状态
                residentInfoDto.setIfGrouped(true);
            }
            boolean more = false;
            // 总页数>当前页码
            if (pageData.getTotalPages() > page) {
                more = true;
            }
            Map<String, Integer> extrasMap = Maps.newHashMap();
            extrasMap.put("totalPage", pageData.getTotalPages());
            extrasMap.put("page", page);
            listResponseEntity.setContent(dtoList, more, null, page.toString());
            listResponseEntity.setExtras(extrasMap);
        } else {
            listResponseEntity.setContent(dtoList, false, null, page.toString());
        }
        return listResponseEntity;
    }
    /**
     * 新增or修改分组信息
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(value="/saveOrUpdate")
    @JsonEncode(encode = true)
    public JsonResponseEntity<Map<String, String>> saveOrUpdateGroup(@RequestBody String request){
        JsonResponseEntity<Map<String, String>> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String doctorId = reader.readString("uid", false);
        String name = reader.readString("name", false);
        String id = reader.readString("id", true);
        try {
            String msg = patientGroupService.savePatientGroup(id, doctorId, name);
            PatientGroup group = patientGroupRepository.findIsNameRepeated(doctorId,StringUtils.trim(name));
            Map<String, String> data = Maps.newHashMap();
            data.put("name", name);
            data.put("id", group.getId().toString());
            entity.setData(data);
            entity.setMsg(msg);
            return entity;
        } catch (CommonException e) {
            String errorMsg = e.msg();
            logger.error(errorMsg);
            entity.setCode(e.code());
            entity.setMsg(errorMsg);
        }
        return entity;
    }
    /**
     * 删除分组
     * @param uid
     * @return
     */
    @VersionRange
    @DeleteMapping(value="/delete")
    @JsonEncode(encode = true)
    public JsonResponseEntity<Map<String, Boolean>> delGroup(@RequestParam String uid,@RequestParam String id){
        JsonResponseEntity<Map<String, Boolean>> entity = new JsonResponseEntity<>();
            Boolean delPatientGroup = patientGroupService.delPatientGroup(id, uid);
            if(!delPatientGroup){
                entity.setCode(2021);
            }
            entity.setMsg(delPatientGroup?"删除成功":"删除失败");
        return entity;
    }
    /**
     * 分组拖动排序
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(value="/sort")
    @JsonEncode(encode = true)
    public JsonResponseEntity<Object> sortGroup(@RequestBody String request){
        JsonResponseEntity<Object> entity = new JsonResponseEntity<>();
        JSONObject object = JSONObject.fromObject(request);

        if(null == object ){
            entity.setCode(-1);
            entity.setMsg("排序失败，数据格式转换异常");
            return entity;
        }

        String doctorId = object.getString("uid");
        JSONArray json = object.getJSONArray("ids");
        List<String> editIds = null;
        if (json.size() > 0) {
            editIds = new ArrayList<String>();
            for (int i = 0; i < json.size(); i++) {
                JSONObject job = json.getJSONObject(i);
                editIds.add(String.valueOf(job.get("id")));
            }
        }
        patientGroupService.sortPatientGroup(editIds, doctorId);
        entity.setCode(0);
        entity.setMsg("排序成功");
        return entity;
    }
    /**
     * 居民列表添加到分组
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(value="/addUserToGroup")
    @JsonEncode(encode = true)
    public JsonResponseEntity<Object> addUserToGroup(@RequestBody String request){
        JsonResponseEntity<Object> entity = new JsonResponseEntity<>();
        JSONObject object = JSONObject.fromObject(request);
        if(null == object ){
            entity.setCode(-1);
            entity.setMsg("数据格式转换异常");
            return entity;
        }

        String userId = object.getString("uid");
        String doctorId=object.getString("doctorId");
        JSONArray json = object.getJSONArray("ids");
        List<String> groupIds = null;
        if (json.size() > 0) {
            groupIds = new ArrayList<String>();
            for (int i = 0; i < json.size(); i++) {
                JSONObject job = json.getJSONObject(i);
                groupIds.add(String.valueOf(job.get("id")));
            }
        }
        try {
            patientGroupService.addUserToGroup(groupIds, userId,doctorId);
            entity.setCode(0);
            entity.setMsg("用户分组成功");
        } catch (Exception e) {
            String errorMsg = "用户分组出错";
            logger.error(errorMsg, e);
            entity.setCode(1000);
            entity.setMsg(errorMsg);
        }
        return entity;
    }

    @VersionRange
    @GetMapping(value = "/selectGroupList")
    @JsonEncode(encode = true)
    public JsonListResponseEntity<PatientGroupDto> getGroupByUserIdAndDoctorId(
            @RequestParam(value = "doctorId", required = true) String doctorId,
            @RequestParam(value = "userId", required = true) String userId) {
        JsonListResponseEntity<PatientGroupDto> entity = new JsonListResponseEntity<>();
        List<PatientGroupDto> list = new ArrayList<>();
        List<PatientGroup> patientList = patientGroupService.getPatientGroupByDoctorId(doctorId);
        for(PatientGroup p:patientList){
            PatientGroupDto dto = new PatientGroupDto();
            dto.setId(p.getId());
            SignUserDoctorGroup isSelectedByGroupIdAndUserId = signUserDoctorGroupRepository.getIsSelectedByGroupIdAndUserId(userId, p.getId(),"0");
            SignUserDoctorGroup delFlagAndUid = signUserDoctorGroupRepository.queryByDoctorIdUid(doctorId, userId);
            if(null==isSelectedByGroupIdAndUserId&&null==delFlagAndUid&&"1".equals(p.getIsDefault())){
                dto.setIsSelected(true); 
            }else if(null!=isSelectedByGroupIdAndUserId){
                dto.setIsSelected(true);
            }else{
                dto.setIsSelected(false);
            }
            dto.setName(p.getName());
            dto.setIsDefault(p.getIsDefault());
            int patientNum=signUserDoctorGroupRepository.getNumByGroupId(p.getId());
            dto.setPatientNum(patientNum);
            dto.setCreateDate(PatientGroupDto.dateToString(p.getCreateTime()));
            list.add(dto);
        }
        entity.setContent(list);
        return entity;
    }
}
