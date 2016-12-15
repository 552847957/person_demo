package com.wondersgroup.healthcloud.services.appointment;



import com.wondersgroup.healthcloud.jpa.entity.appointment.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/5/24.
 */
public interface AppointmentService {

    void saveAndFlush(AppointmentHospital localHospital);

    AppointmentHospital getHospitalByCode(String hosOrgCode);

    void saveAndFlush(AppointmentL1Department localL1Department);

    AppointmentL1Department getAppointmentDepartmentL1(String deptCode, String id);

    void saveAndFlush(AppointmentL2Department locaL2Department);

    AppointmentL2Department getAppointmentDepartmentL2(String deptCode, String hospitalId, String id);

    void saveAndFlush(AppointmentDoctorSchedule schedule);

    AppointmentDoctorSchedule getAppointmentDoctorSchedule(String scheduleId, String numSourceId, String hospitalId);

    void saveAndFlush(AppointmentDoctor doctor);

    AppointmentDoctor getAppointmentDoctor(String doctCode, String l2DepartmentId, String l1DepartmentId, String hospitalId);

    void deleteAppointmentHospitalByNowDate(Date nowDate);

    void deleteDepartmentL1ByNowDate(Date nowDate);

    void deleteDepartmentL2ByNowDate(Date nowDate);

    void deleteAppointmentDoctorByNowDate(Date nowDate);

    void deleteSchedule(Date nowDate);

    void deleteDoctorHasNoSchedule();

    void deleteDept2HasNoDoctorAndSchedule();

    void deleteDept1HasNoDept2();

    void deleteHospitalHasNoDept1();

    List<Map<String,Object>> findOrderListNeedUpdateStatus();

    void setDoctorNumToHospital();

}
