package com.wondersgroup.healthcloud.api.http.controllers.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.doctor.group.PatientGroupDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.group.PatientGroup;
import com.wondersgroup.healthcloud.jpa.repository.group.PatientGroupRepository;
import com.wondersgroup.healthcloud.jpa.repository.group.SignUserDoctorGroupRepository;
import com.wondersgroup.healthcloud.services.group.PatientGroupService;

/**
 * 
 * @author zhongshuqing
 *
 */
@RestController
@RequestMapping(value="/api/group")
public class PatientGroupController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientGroupController.class);
    @Autowired
    private PatientGroupService patientGroupService;
    @Autowired
    PatientGroupRepository patientGroupRepository;
    @Autowired
    SignUserDoctorGroupRepository signUserDoctorGroupRepository;
    
    /**
     * 获取当前医生id下面的分组信息
     * @param doctorId
     * @return
     */
    @VersionRange
    @GetMapping(value="/list")
    public JsonResponseEntity<List<PatientGroupDto>> getGroupByDoctorId(@RequestParam(value="uid",required=true) String doctorId){
        JsonResponseEntity<List<PatientGroupDto>> entity = new JsonResponseEntity<>();
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
            entity.setData(list);
            return entity;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entity;
        
    }
    /**
     * 新增or修改分组信息
     * @param request
     * @return
     */
    @VersionRange
    @PostMapping(value="/saveOrUpdate")
    public JsonResponseEntity<Map<String, String>> saveOrUpdateGroup(@RequestBody String request){
        JsonResponseEntity<Map<String, String>> entity = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String doctorId = reader.readString("uid", false);
        String name = reader.readString("name", false);
        String id = reader.readString("id", true);
        try {
            String msg = patientGroupService.savePatientGroup(id, doctorId, name);
            PatientGroup group = patientGroupRepository.findIsNameRepeated(doctorId, name);
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
     * @param request
     * @return
     */
    @VersionRange
    @DeleteMapping(value="/delete")
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
    public JsonResponseEntity<Object> addUserToGroup(@RequestBody String request){
        JsonResponseEntity<Object> entity = new JsonResponseEntity<>();
        JSONObject object = JSONObject.fromObject(request);
        if(null == object ){
            entity.setCode(-1);
            entity.setMsg("数据格式转换异常");
            return entity;
        }

        String userId = object.getString("uid");
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
            patientGroupService.addUserToGroup(groupIds, userId);
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
    
}
