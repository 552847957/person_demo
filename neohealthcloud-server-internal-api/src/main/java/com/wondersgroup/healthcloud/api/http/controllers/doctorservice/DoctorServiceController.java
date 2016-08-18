package com.wondersgroup.healthcloud.api.http.controllers.doctorservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceDic;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceRoleMap;
import com.wondersgroup.healthcloud.services.doctor.DoctorServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jimmy on 16/8/4.
 */

@RestController
@RequestMapping("api")
public class DoctorServiceController {

    @Autowired
    private DoctorServiceService doctorServiceService;

    /**
     * 保存医生服务字典
     *
     * @return
     */
    @RequestMapping(value = "config/saveDoctorServiceDic", method = RequestMethod.POST)
    public JsonResponseEntity saveDoctorServiceDic(@RequestBody Map para) {
        para.put("id", IdGen.uuid());
        DoctorServiceDic doctorServiceDic = MapToBeanUtil.fromMapToBean(DoctorServiceDic.class, para);
        doctorServiceService.saveDoctorServiceDic(doctorServiceDic);
        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 查询医生服务包字典列表
     *
     * @param pageable
     * @return
     */
    @RequestMapping(value = "config/getServiceDic", method = RequestMethod.GET)
    public String queryDoctorServiceDices(String key, @PageableDefault Pageable pageable) throws JsonProcessingException {
        Page<DoctorServiceDic> doctorServiceDices = doctorServiceService.queryDoctorServiceDices(key, pageable);

        Map<Class, Object> filterMap = new HashMap();
//        filterMap.put(DoctorServiceDic.class, new String[]{"create_by", "create_date", "update_by", "update_date"});
        filterMap.put(PageImpl.class, new String[]{"number_of_elements", "sort", "first"});
        SimpleFilterProvider filter = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity result = new JsonResponseEntity(0, "查询成功", doctorServiceDices);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filter).writeValueAsString(result);
    }

    /**
     * 医生服务包和web医生角色关联配置
     */
    @PostMapping(value = "config/saveServiceRoleMap")
    public JsonResponseEntity saveDoctorServiceRoleMap(@RequestBody Map para) {
        para.put("id", IdGen.uuid());
        DoctorServiceRoleMap serviceRoleMap = MapToBeanUtil.fromMapToBean(DoctorServiceRoleMap.class, para);
        doctorServiceService.saveDoctorServiceRoleMap(serviceRoleMap);
        return new JsonResponseEntity(0, "保存成功");
    }

    /**
     * 查询医生服务包和web医生角色关联配置列表
     */
    @RequestMapping(value = "config/getServiceRoleMap", method = RequestMethod.GET)
    public String queryDoctorServiceRoleMap(String key, @PageableDefault Pageable pageable) throws JsonProcessingException {
        Page<DoctorServiceRoleMap> doctorServiceRoleMaps = doctorServiceService.queryDoctorServiceRoleMap(key, pageable);

        Map<Class, Object> filterMap = new HashMap();
        filterMap.put(DoctorServiceRoleMap.class, new String[]{"id", "service_id", "service_name", "role_id", "role_name"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last",});
        SimpleFilterProvider filter = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity result = new JsonResponseEntity(0, "查询成功", doctorServiceRoleMaps);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filter).writeValueAsString(result);
    }

    /**
     * 删除医生服务包和web医生角色关联配置列表
     */

    @RequestMapping(value = "config/deleteServiceRoleMap/{id}", method = RequestMethod.GET)
    public JsonResponseEntity deleteServiceRoleMap(@PathVariable String id) {
        int index = doctorServiceService.deleteDoctorServiceRoleMap(id);
        if (index == 1) {
            return new JsonResponseEntity(0, "删除成功");
        }
        return new JsonResponseEntity(-1, "删除失败");
    }
}
