package com.wondersgroup.healthcloud.services.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 *
 * 提供给客户端接口使用的service
 */
public interface AppointmentApiService {

    List<Map<String,Object>> findAppointmentAreaByUpperCode(String areaCode);

    List<AppointmentHospital> findAllHospitalListByAreaCodeOrKw(String kw,String areaCode,Integer flag,int pageSize);

    int countDoctorNumByHospitalId(String id);

    List<AppointmentHospital> findAllHospitalListByKw(String kw,Integer flag,int pageSize);

    List<AppointmentDoctor> findDoctorListByKw(String kw, int pageSize, int pageNum,Boolean hasDepartRegistration);

    Map<String,Object> countDoctorReserveOrderNumByDoctorId(String doctorId);

    AppointmentHospital findHospitalById(String hospitalId);

    List<AppointmentL1Department> findAllAppointmentL1Department(String hospital_id);

    List<AppointmentL2Department> findAppointmentL2Department(String hospital_id, String department_l1_id);

    Map<String,Object> countDepartmentReserveOrderNumByDepartmentId(String department_l2_id);

    AppointmentL2Department findAppointmentL2DepartmentById(String department_l2_id);

    AppointmentHospital findHospitalByDepartmentL2Id(String department_l2_id);

    List<ScheduleDto> findScheduleByDepartmentL2IdAndScheduleDate(String department_l2_id, String schedule_date, Integer pageNum, int pageSize);

}
