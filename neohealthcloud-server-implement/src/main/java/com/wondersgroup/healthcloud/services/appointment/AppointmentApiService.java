package com.wondersgroup.healthcloud.services.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;

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

    List<AppointmentDoctor> findDoctorListByKw(String kw, int pageSize, int pageNum);

    Map<String,Object> countDoctorReserveOrderNumByDoctorId(String id);

    AppointmentHospital findHospitalById(String hospitalId);
}
