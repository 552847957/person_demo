package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.appointment.*;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorAppointmentException;
import com.wondersgroup.healthcloud.utils.EmojiUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 * 预约挂号api接口
 */
@RestController
@RequestMapping("/api/reservation")
public class AppointmentResourceController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AppointmentResourceController.class);

    @Autowired
    private AppointmentApiService appointmentApiService;


    /**
     * 查询预约挂号地址列表
     * @param request
     * @return
     */
    @VersionRange
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
     * 根据地区查询医院列表
     *
     * @param areaCode
     * @param kw
     *
     * 如果kw不为空为 搜索查询更多
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/hospital/list", method = RequestMethod.GET)
    public JsonListResponseEntity getHospitalList(
            @RequestParam(required = false, defaultValue = "",value = "area_code" ) String areaCode,
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "") String kw,
            @RequestHeader(name = "main-area", required = true) String mainArea) {

        JsonListResponseEntity<AppointmentHospitalDTO> body = new JsonListResponseEntity<>();
        List<AppointmentHospitalDTO> list = Lists.newArrayList();

        Boolean isOn = appointmentApiService.getRegistrationIsOn(mainArea);
        if(!isOn){
            body.setCode(1);//与前端约定code为1时 是服务未开通
            body.setMsg("该服务暂未开通,敬请期待");
            return body;
        }

        int pageSize = 10;
        Boolean more = false;
        List<AppointmentHospital> appointmentHospitals = appointmentApiService.findAllHospitalListByAreaCodeOrKw(kw, areaCode, flag, pageSize);

        if(appointmentHospitals.size()>10){
            more = true;
            flag = flag + 1;
        }
        AppointmentHospitalDTO hospitalDTO;
        for (AppointmentHospital hospital : appointmentHospitals) {
            hospitalDTO = new AppointmentHospitalDTO(hospital);
            list.add(hospitalDTO);
        }

        body.setContent(list, more, null, String.valueOf(flag));
        return body;

    }

    /**
     * 搜索
     *
     * @param kw
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public JsonResponseEntity search(
            @RequestParam(required = false, defaultValue = "") String kw) {

        JsonResponseEntity<SearchDTO> body = new JsonResponseEntity<>();

        SearchDTO searchDTO = new SearchDTO();

        if(StringUtils.isNotBlank(kw)){
            String cleanName = EmojiUtils.cleanEmoji(kw);
            if(kw.length() > cleanName.length()){
                return body;
            }
        }

        /**
         * 医生
         */
        List<AppointmentDoctorDTO> doctorDTOList = Lists.newArrayList();
        Boolean moreDoctor = false;
        int pageSize = 10;
        int pageNum = 1;

        List<AppointmentDoctor> appointmentDoctors = appointmentApiService.findDoctorListByKw(kw,pageSize,pageNum,false,null);
        if(appointmentDoctors.size()>2){
            moreDoctor = true;
        }
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        int count = 1;
        for (AppointmentDoctor doctor : appointmentDoctors) {
            if(count<3){
                doctorDTO = doctorDTO.getDoctorDTOSearchList(doctor);
                doctorDTOList.add(doctorDTO);
                count+=1;
            }

        }

        SearchDTO.SearchDoctorDTO searchDoctorDTO = new SearchDTO().new SearchDoctorDTO(doctorDTOList,moreDoctor);


        /**
         * 医院
         */
        List<AppointmentHospitalDTO> hospitalDTOList = Lists.newArrayList();
        Boolean moreHospital = false;

        List<AppointmentHospital> appointmentHospitals = appointmentApiService.findAllHospitalListByKw(kw,pageSize,pageNum);
        if(appointmentHospitals.size()>2){
            moreHospital = true;
        }
        AppointmentHospitalDTO hospitalDTO;
        int c = 1;
        for (AppointmentHospital hospital : appointmentHospitals) {
            if(c<3){
                hospitalDTO = new AppointmentHospitalDTO(hospital);
                hospitalDTOList.add(hospitalDTO);
                c+=1;
            }

        }

        SearchDTO.SearchHospitalDTO searchHospitalDTO = new SearchDTO().new SearchHospitalDTO(hospitalDTOList,moreHospital);


        searchDTO.setDoctor(searchDoctorDTO);
        searchDTO.setHospital(searchHospitalDTO);
        body.setData(searchDTO);
        return body;

    }


    /**
     * 搜索更多医生
     *
     * @param kw 搜索词
     * @param flag 分页
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/searchMore/doctor", method = RequestMethod.GET)
    public JsonListResponseEntity searchMoreDoctors(
            @RequestParam(required = false, defaultValue = "1") Integer flag,
            @RequestParam(required = false, defaultValue = "") String kw){

        JsonListResponseEntity<AppointmentDoctorDTO> body = new JsonListResponseEntity<>();
        List<AppointmentDoctorDTO> doctorDTOList = Lists.newArrayList();
        Boolean moreDoctor = false;
        int pageSize = 10;

        List<AppointmentDoctor> appointmentDoctors = appointmentApiService.findDoctorListByKw(kw,pageSize,flag,false,null);
        if(appointmentDoctors.size()>pageSize){
            moreDoctor = true;
            flag = flag +1;
        }
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        int count = 1;
        for (AppointmentDoctor doctor : appointmentDoctors) {
            if(count > pageSize){
                moreDoctor = true;
                break;
            }
            doctorDTO = doctorDTO.getDoctorDTOSearchList(doctor);
            doctorDTOList.add(doctorDTO);
            count += 1;
        }

        body.setContent(doctorDTOList, moreDoctor, null, String.valueOf(flag));
        return body;

    }

    /**
     * 根据医院Id查询医院详情(医院主页)
     * @param hospitalId
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/hospital/detail", method = RequestMethod.GET)
    public JsonResponseEntity hospitalDetail(
            @RequestParam(required = true, defaultValue = "",value = "id" ) String hospitalId) {
        JsonResponseEntity<AppointmentHospitalDTO> body = new JsonResponseEntity<>();

        AppointmentHospital hospital = appointmentApiService.findHospitalById(hospitalId);
        AppointmentHospitalDTO hospitalDTO = AppointmentHospitalDTO.getHospitalDetail(hospital);
        body.setData(hospitalDTO);
        return body;
    }


    /**
     * 根据医院Id查询一级科室列表
     * @param hospital_id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/department1/list", method = RequestMethod.GET)
    public JsonListResponseEntity<DepartmentDTO> getdepartment1Lists(@RequestParam(required = true) String hospital_id) {
        JsonListResponseEntity<DepartmentDTO> body = new JsonListResponseEntity<>();
        List<AppointmentL1Department> appointmentL1Departments = appointmentApiService.findAllAppointmentL1Department(hospital_id);
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
     * 根据医院Id和一级科室Id查询二级科室列表
     *
     * @param hospital_id
     * @param department_l1_id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/department2/list", method = RequestMethod.GET)
    public JsonListResponseEntity<DepartmentDTO> getdepartment2Lists(@RequestParam(required = true) String hospital_id,
                                                                     @RequestParam(required = true) String department_l1_id) {
        JsonListResponseEntity<DepartmentDTO> body = new JsonListResponseEntity<>();
        List<AppointmentL2Department> appointmentL2Departments = appointmentApiService.findAppointmentL2Department(hospital_id, department_l1_id);
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
     * 根据二级科室查询医生列表(按专家预约)
     *
     * @param department_l2_id
     * @param flag
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/doctor/expertList", method = RequestMethod.GET)
    public JsonListResponseEntity getDcotorList(@RequestParam(required = false) String department_l2_id,
                                                @RequestParam(required = false, defaultValue = "1") Integer flag) {
        JsonListResponseEntity<AppointmentDoctorDTO> body = new JsonListResponseEntity<>();
        Boolean has_more = false;
        List<AppointmentDoctorDTO> list = new ArrayList<>();
        int pageSize = 10;

        Boolean hasDepartRegistration = false;

        //判断二级科室有没有科室预约
        Map<String,Object> result = appointmentApiService.countDepartmentReserveOrderNumByDepartmentId(department_l2_id);
        AppointmentL2Department department = appointmentApiService.findAppointmentL2DepartmentById(department_l2_id);

        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO(department,result);
        /**
         * 第一页请求把科室预约放在第一条
         */
        int doctorNum = pageSize;
        if(flag ==1 && doctorDTO!=null && doctorDTO.getReservationStatus()!=0){
            hasDepartRegistration = true;
            int doctorReservationNum = appointmentApiService.countAllDoctorReservationNumByDepartmentL2Id(department_l2_id);
            doctorDTO.setReservationNum(doctorDTO.getReservationNum()+doctorReservationNum);
            list.add(doctorDTO);
            doctorNum = doctorNum - 1;
        }

        List<AppointmentDoctor> appointmentDoctors = appointmentApiService.findDoctorListByKw(null,pageSize,flag,hasDepartRegistration,department_l2_id);


        int count = 1;
        for (AppointmentDoctor doctor : appointmentDoctors) {
            if(count > doctorNum){
                has_more = true;
                break;
            }
            Map<String,Object> re = appointmentApiService.countDoctorReserveOrderNumByDoctorId(doctor.getId());
            doctorDTO = new AppointmentDoctorDTO(doctor,re);
            list.add(doctorDTO);
            count +=1;
        }

        body.setContent(list, has_more, null, has_more?String.valueOf(flag+1):String.valueOf(flag));
        return body;

    }


    /**
     * 科室下面-按照日期预约列表-日期列表(14天)
     * @param department_l2_id
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/doctor/dateList", method = RequestMethod.GET)
    public JsonListResponseEntity getDcotorList(@RequestParam(required = false) String department_l2_id) {
        JsonListResponseEntity<AppointmentDateDTO> body = new JsonListResponseEntity<>();

        List<AppointmentDateDTO> list = Lists.newArrayList();
        //判断医院支不支持当天预约
        AppointmentHospital hospital = appointmentApiService.findHospitalByDepartmentL2Id(department_l2_id);
        Date firstDate = new Date();

        //如果不支持当天预约 则第一天加1天
        if("0".equals(hospital.getIsOrderToday()==null?0:hospital.getIsOrderToday())){
            firstDate = DateUtils.addDay(firstDate,1);
        }

        Date currentDate = firstDate;
        AppointmentDateDTO dateDTO;
        for(int i = 0; i<14; i++){
            dateDTO = new AppointmentDateDTO(currentDate);
            list.add(dateDTO);
            currentDate = DateUtils.addDay(currentDate,1);
        }

        body.setContent(list, false, null, null);
        return body;

    }

    /**
     * 根据二级科室和就诊时间查询 排班列表
     * @param department_l2_id
     * @param schedule_date
     * @param flag
     * @return
     */
    @VersionRange
    @RequestMapping(value = "/doctor/dateRegistrationList", method = RequestMethod.GET)
    public JsonListResponseEntity getDateRegistrationList(@RequestParam(required = true ) String department_l2_id,
                                                          @RequestParam(required = true ) String schedule_date,
                                                @RequestParam(required = false, defaultValue = "1") Integer flag) {
        JsonListResponseEntity<ScheduleDetailDTO> body = new JsonListResponseEntity<>();
        int pageSize = 10;
        Boolean more = false;
        List<ScheduleDto> schedules = appointmentApiService.findScheduleByDepartmentL2IdAndScheduleDate(department_l2_id,schedule_date,flag,pageSize);

        if(schedules.size()>pageSize)
            more = true;

        List<ScheduleDetailDTO> list = Lists.newArrayList();
        ScheduleDetailDTO detailDTO;
        int count = 1;
        for(ScheduleDto schedule : schedules){
            if(count>pageSize){
                break;
            }
            detailDTO = new ScheduleDetailDTO(schedule);
            detailDTO.setScheduleId(schedule.getId());
            detailDTO.setId(schedule.getDoctorId());
            list.add(detailDTO);
            count += 1;
        }


        body.setContent(list, more, null, more?String.valueOf(flag+1):String.valueOf(flag));
        return body;
    }


    /**
     * 医生详情
     *
     * @param id type为1时是医生的Id,type为2时是二级科室的Id
     * @return type 1:医生 2:科室
     */
    @VersionRange
    @RequestMapping(value = "/doctor/detail", method = RequestMethod.GET)
    public JsonResponseEntity getDcotorDetail(@RequestParam(required = true) String id,
                                              @RequestParam(required = true) String type) {
        JsonResponseEntity<AppointmentDoctorDTO> body = new JsonResponseEntity<>();
        AppointmentDoctorDTO doctorDTO = new AppointmentDoctorDTO();
        /**
         * 医生详情
         */
        if("1".equals(type)){
            AppointmentDoctor doctor = appointmentApiService.findDoctorById(id);
            doctorDTO = new AppointmentDoctorDTO(doctor,null);
            doctorDTO.setReservationRule(doctor.getReservationRule());
            doctorDTO.setHospitalName(doctor.getHospitalName());
            doctorDTO.setDepartmentName(doctor.getDepartmentName());
        //科室详情
        }else if("2".equals(type)){
            AppointmentL2Department department = appointmentApiService.findL2DepartmentById(id);
            doctorDTO = new AppointmentDoctorDTO(department,null);
            doctorDTO.setReservationRule(department.getReservationRule());
            doctorDTO.setHospitalName(department.getHospitalName());
            doctorDTO.setDepartmentName(department.getDeptName());
        }


        body.setData(doctorDTO);
        return body;
    }


    @VersionRange
    @RequestMapping(value = "/doctor/scheduleList", method = RequestMethod.GET)
    public JsonListResponseEntity getDcotorscheduleList(@RequestParam(required = true) String id,
                                                @RequestParam(required = true) String type,
                                                @RequestParam(required = false, defaultValue = "1") Integer flag) {
        JsonListResponseEntity<ScheduleDetailDTO> body = new JsonListResponseEntity<>();
        int pageSize = 10;
        Boolean more = false;
        List<ScheduleDetailDTO> list = Lists.newArrayList();

        if(!"1".equals(type) && !"2".equals(type)){
            body.setContent(list, more, null, more?String.valueOf(flag+1):String.valueOf(flag));
            return body;
        }
        List<ScheduleDto> schedules = appointmentApiService.findScheduleByDepartmentL2IdOrDoctorId(id,type, flag, pageSize);
        if(schedules.size()>pageSize)
            more = true;
        ScheduleDetailDTO detailDTO;
        int count = 1;
        for(ScheduleDto schedule : schedules){
            if(count>pageSize){
                break;
            }
            detailDTO = new ScheduleDetailDTO(schedule);
            list.add(detailDTO);
            count += 1;
        }

        body.setContent(list, more, null, more?String.valueOf(flag+1):String.valueOf(flag));
        return body;

    }














}
