package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.*;
import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.annotations.Admin;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorAccount;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorInfo;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorServiceEntity;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.AppointmentManangeService;
import com.wondersgroup.healthcloud.services.appointment.AppointmentService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorAppointmentManageException;
import com.wondersgroup.healthcloud.services.doctor.entity.Doctor;
import com.wondersgroup.healthcloud.services.doctor.exception.ErrorDoctorAccountNoneException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/12.
 * 后台管理预约挂号数据
 */
@RestController
@RequestMapping(value = "/api/reservation/manage")
public class AppointmentManageController {
    @Autowired
    private AppointmentApiService appointmentApiService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentManangeService appointmentManangeService;

    /**
     * 区县
     * @param request
     * @return
     */
    @Admin
    @RequestMapping(value="/areas",method = RequestMethod.GET)
    public JsonListResponseEntity<AreaDTO> getAreasDict(HttpServletRequest request){
        JsonListResponseEntity<AreaDTO> areaJsonListResponseEntity = new JsonListResponseEntity<>();
        List<AreaDTO> list = new ArrayList<AreaDTO>();
        AreaDTO area = new AreaDTO();
        area.setAreaCode("310100000000");
        area.setAreaName("全上海");
        area.setAreaUpperCode("310000000000");
        list.add(area);
        List<Map<String,Object>> resultList =  appointmentApiService.findAppointmentAreaByUpperCode(area.getAreaCode());
        for(Map<String,Object> result:resultList){
            list.add(new AreaDTO(result));
        }
        areaJsonListResponseEntity.setContent(list);
        return areaJsonListResponseEntity;
    }


    /**
     * 医院列表
     * @param pager
     * @return
     */
    @Admin
    @PostMapping(value = "/hospital/list")
    public Pager hospitalList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        Map<String,Object> map = pager.getParameter();
        String areaCode = map.get("areaCode")==null?"":map.get("areaCode").toString();
        String name = map.get("name")==null?"":map.get("name").toString();
        List<ManageHospitalDTO> list = Lists.newArrayList();
        List<AppointmentHospital> appointmentHospitals = appointmentManangeService.findAllManageHospitalListByAreaCodeAndName(name, areaCode, pageNum, pager.getSize());
        ManageHospitalDTO manageHospitalDTO;
        for(AppointmentHospital hospital : appointmentHospitals){
            if(list.size()>pager.getSize())
                break;
            manageHospitalDTO = new ManageHospitalDTO(hospital);
            list.add(manageHospitalDTO);
        }
        int totalSize = appointmentManangeService.countHospitalsByAreaCode(name,areaCode);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }


    /**
     * 查询医院详情
     * @param hospitalId
     * @return
     */
    @Admin
    @RequestMapping(value = "/hospital/detail", method = RequestMethod.GET)
    public JsonResponseEntity hospitalDetail(
            @RequestParam(required = true, defaultValue = "",value = "id" ) String hospitalId) {
        JsonResponseEntity<ManageHospitalDTO> body = new JsonResponseEntity<>();
        AppointmentHospital hospital = appointmentApiService.findHospitalById(hospitalId);
        ManageHospitalDTO hospitalDTO = new ManageHospitalDTO(hospital);
        body.setData(hospitalDTO);
        return body;
    }

    /**
     * 批量启用或停用医院
     * isonsale=1 启用,isonsale=0 停用
     * @return
     */
    @RequestMapping(value = "/hospital/batchSetIsonsale", method = RequestMethod.POST)
    @Admin
    public JsonResponseEntity batchSetIsonsale(@RequestBody String request){
        JsonKeyReader reader = new JsonKeyReader(request);
        String isonsale = reader.readString("isonsale", true);
        List<String> hospitalIds = reader.readObject("hospitalIds", true,List.class);

        if(!"1".equals(isonsale) || !"0".equals(isonsale)){
            throw new ErrorAppointmentManageException("启用或停用状态错误");
        }
        if(hospitalIds.size()<1){
            throw new ErrorAppointmentManageException("请选择至少一家医院");
        }

        appointmentManangeService.batchSetIsonsaleByHospitalIds(hospitalIds,isonsale);
        return new JsonResponseEntity(0, "保存成功");
    }



    /**
     * 保存医院信息
     * @param manageHospitalDTO
     * @return
     */
    @Admin
    @PostMapping(path = "/hospital/save")
    public JsonResponseEntity saveHospital(@RequestBody ManageHospitalDTO manageHospitalDTO){

        if(manageHospitalDTO!=null && StringUtils.isNotBlank(manageHospitalDTO.getId())){
            AppointmentHospital hospital = appointmentApiService.findHospitalById(manageHospitalDTO.getId());
            if(hospital!=null){
                hospital = manageHospitalDTO.mergeHospital(hospital, manageHospitalDTO);
                appointmentService.saveAndFlush(hospital);
            }else{
                return new JsonResponseEntity(3011, "保存失败,该医院不存在");
            }
            return new JsonResponseEntity(0, "保存成功");
        }else{
            return new JsonResponseEntity(3011, "保存失败,参数有误");
        }

    }

    /**
     * 订单列表
     * @param pager
     * @return
     */
    @Admin
    @PostMapping(value = "/order/list")
    public Pager orderList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        Map<String,Object> map = pager.getParameter();
        String patientName = map.get("patientName")==null?"":map.get("patientName").toString();
        String patientMobile = map.get("patientMobile")==null?"":map.get("patientMobile").toString();
        List<ManageOrderDTO> list = Lists.newArrayList();
        List<OrderDto> orderList = appointmentManangeService.findAllManageOrderListByNameAndMobile(patientName, patientMobile,null, pageNum, pager.getSize(),true);
        ManageOrderDTO orderDTO;
        for(OrderDto order : orderList){
            if(list.size()>pager.getSize())
                break;
            orderDTO = new ManageOrderDTO(order);
            list.add(orderDTO);
        }
        int totalSize = appointmentManangeService.countOrdersByNameAndMobile(patientName, patientMobile);
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    /**
     * 订单详情
     * @param id
     * @return
     */
    @Admin
    @RequestMapping(value = "/order/detail", method = RequestMethod.GET)
    public JsonResponseEntity<ManageOrderDTO> orderDetail(@RequestParam(required = true) String id) {
        JsonResponseEntity<ManageOrderDTO> response = new JsonResponseEntity<>();
        OrderDto orderDto = appointmentManangeService.findAllManageOrderListByNameAndMobile(null,null,id,0,0,false).get(0);

        if (orderDto != null) {
            ManageOrderDTO manageOrderDTO = new ManageOrderDTO(orderDto);
            response.setData(manageOrderDTO);
        }

        return response;
    }


    /**
     * 根据医院Id查询一级科室列表
     * @param hospital_id
     * @return
     */
    @Admin
    @RequestMapping(value = "/department1/list", method = RequestMethod.GET)
    public JsonListResponseEntity<DepartmentDTO> getdepartment1Lists(@RequestParam(required = true) String hospital_id) {
        JsonListResponseEntity<DepartmentDTO> body = new JsonListResponseEntity<>();
        List<AppointmentL1Department> appointmentL1Departments = appointmentManangeService.findManageAppointmentL1Department(hospital_id);
        List<DepartmentDTO> list = Lists.newArrayList();
        DepartmentDTO apiEntity;
        for (AppointmentL1Department _appointmentL1Department : appointmentL1Departments) {
            apiEntity = new DepartmentDTO(_appointmentL1Department);
            list.add(apiEntity);
        }
        body.setContent(list);
        return body;

    }

    /**
     * 根据一级科室Id查询二级科室列表
     *
     * @param department_l1_id
     * @return
     */
    @Admin
    @RequestMapping(value = "/department2/list", method = RequestMethod.GET)
    public JsonListResponseEntity<DepartmentDTO> getdepartment2Lists(@RequestParam(required = true) String department_l1_id) {
        JsonListResponseEntity<DepartmentDTO> body = new JsonListResponseEntity<>();
        List<AppointmentL2Department> appointmentL2Departments = appointmentManangeService.findManageAppointmentL2Department(department_l1_id);
        List<DepartmentDTO> list = Lists.newArrayList();
        DepartmentDTO apiEntity;
        for (AppointmentL2Department _appointmentL2Department : appointmentL2Departments) {
            apiEntity = new DepartmentDTO(_appointmentL2Department);
            list.add(apiEntity);
        }
        body.setContent(list);
        return body;

    }

    /**
     * 医生列表
     * @param pager
     * @return
     */
    @Admin
    @PostMapping(value = "/doctor/list")
    public Pager doctorList(@RequestBody Pager pager){
        int pageNum = 1;
        if(pager.getNumber()!=0)
            pageNum = pager.getNumber();

        List<ManageDoctorDTO> list = Lists.newArrayList();
        List<AppointmentDoctor> doctorList = appointmentManangeService.findAllManageDoctorListByMap(pager.getParameter(), pageNum, pager.getSize());
        ManageDoctorDTO manageDoctorDTO;
        for(AppointmentDoctor doctor : doctorList){
            if(list.size()>pager.getSize())
                break;
            manageDoctorDTO = new ManageDoctorDTO(doctor);
            list.add(manageDoctorDTO);
        }
        int totalSize = appointmentManangeService.countDoctorByMap(pager.getParameter());
        pager.setTotalElements(totalSize);
        pager.setData(list);
        return pager;
    }

    /**
     * 查询医院详情
     * @param doctorId
     * @return
     */
    @Admin
    @RequestMapping(value = "/doctor/detail", method = RequestMethod.GET)
    public JsonResponseEntity doctorDetail(
            @RequestParam(required = true, defaultValue = "",value = "id" ) String doctorId) {
        JsonResponseEntity<ManageDoctorDTO> body = new JsonResponseEntity<>();
        AppointmentDoctor doctor = appointmentApiService.findDoctorById(doctorId);
        ManageDoctorDTO manageDoctorDTO = new ManageDoctorDTO(doctor);
        body.setData(manageDoctorDTO);
        return body;
    }


    /**
     * 保存医生信息
     * @param manageDoctorDTO
     * @return
     */
    @Admin
    @PostMapping(path = "/doctor/save")
    public JsonResponseEntity saveDoctor(@RequestBody ManageDoctorDTO manageDoctorDTO){

        if(manageDoctorDTO!=null && StringUtils.isNotBlank(manageDoctorDTO.getId())){
            AppointmentDoctor doctor = appointmentApiService.findDoctorById(manageDoctorDTO.getId());
            if(doctor!=null){
                doctor = manageDoctorDTO.mergeDoctor(doctor, manageDoctorDTO);
                appointmentService.saveAndFlush(doctor);
            }else{
                return new JsonResponseEntity(3011, "保存失败,该医院不存在");
            }
            return new JsonResponseEntity(0, "保存成功");
        }else{
            return new JsonResponseEntity(3011, "保存失败,参数有误");
        }

    }


}
