package com.wondersgroup.healthcloud.api.http.controllers.doctor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.api.http.dto.doctor.DoctorDepartmentEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorDepartment;
import com.wondersgroup.healthcloud.services.doctor.DoctorDepartService;
import com.wondersgroup.healthcloud.services.doctor.DoctorConcerService;
import com.wondersgroup.healthcloud.services.doctor.DoctorService;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/8/31.
 */

@RestController
@RequestMapping(value = "/api/doctorConcer")
public class DoctorConcerController {

    @Autowired
    private DoctorConcerService doctorConcerService;

    @Autowired
    private DoctorDepartService doctorDepartService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppUrlH5Utils appUrlH5Utils;

    /**
     * 医生二维码
     * @param doctorId
     * @return
     */
    @RequestMapping(value = "/getDocotrQRCode", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<String> getDocotrQRCode(@RequestParam String doctorId){

        JsonResponseEntity<String> body = new JsonResponseEntity();
        try{
            String qrCodeUrl = appUrlH5Utils.buildWeiXinScan(doctorId);
            body.setData(qrCodeUrl);
        }catch (Exception e){
            e.printStackTrace();
            body.setCode(3010);
            body.setMsg("调用失败");
        }
        return body;
    }

    /**
     * 科室列表
     * @return
     */
    @RequestMapping(value = "/getDepartList", method = RequestMethod.GET)
    @VersionRange
    public JsonListResponseEntity<DoctorDepartmentEntity> getDepartments(){

        JsonListResponseEntity<DoctorDepartmentEntity> body = new JsonListResponseEntity<>();
        try{
            List<DoctorDepartmentEntity> entities = Lists.newArrayList();
            List<DoctorDepartment> departments = doctorDepartService.queryFirstLevelDepartments();
            if(departments!=null&&!departments.isEmpty()){
                for(DoctorDepartment department:departments){
                    List<DoctorDepartment> subList = doctorDepartService.queryDoctorDepartmentsByPid(department.getId());
                    if(subList==null||subList.isEmpty()){
                        subList = Lists.newArrayList();
                        subList.add(department);
                    }
                    DoctorDepartmentEntity entity = new DoctorDepartmentEntity(department,subList);
                    entities.add(entity);
                }
            }

            body.setContent(entities,false,null,null);
        }catch (Exception e){
            e.printStackTrace();
            body.setCode(3010);
            body.setMsg("调用失败");
        }
        return body;
    }

    /**
     * 保存我关注的领域
     * @param request
     * @return
     */
    @RequestMapping(value = "/changeDepart", method = RequestMethod.POST)
    @VersionRange
    public JsonResponseEntity<String> changeRelationShipBtwDoctorAndDepartment(@RequestBody String request){

        JsonResponseEntity<String> body = new JsonResponseEntity();
        try{
            JsonKeyReader reader = new JsonKeyReader(request);
            String doctorId = reader.readString("doctorId",true);
            String departmentIds = reader.readString("departmentIds",true);
            doctorConcerService.updateDoctorConcerDepartment(doctorId,departmentIds);
            body.setMsg("保存成功");
        }catch (Exception e){
            e.printStackTrace();
            body.setCode(3010);
            body.setMsg("保存失败");
        }
        return body;
    }

    @RequestMapping(value = "/getDoctorConcer", method = RequestMethod.GET)
    @VersionRange
    public JsonResponseEntity<Map<String,Object>> getConcerDepartment(@RequestParam(required = false) String doctorId){

        JsonResponseEntity<Map<String,Object>> body = new JsonResponseEntity();
        Map<String, Object> data = Maps.newHashMap();
        try{
            List<DoctorDepartment> departmentList = doctorConcerService.queryDoctorDepartmentsByDoctorId(doctorId);

            if(departmentList==null||departmentList.isEmpty()){
                Doctor doctorInfo = doctorService.findDoctorByUid(doctorId);
                departmentList = Lists.newArrayList();
                DoctorDepartment department = new DoctorDepartment();
                department.setName(doctorInfo.getDepartName());
            }

            data.put("department",departmentList);

            body.setData(data);
        }catch (Exception e){
            e.printStackTrace();
            body.setCode(3010);
            body.setMsg("调用失败");
        }
        return body;
    }
}
