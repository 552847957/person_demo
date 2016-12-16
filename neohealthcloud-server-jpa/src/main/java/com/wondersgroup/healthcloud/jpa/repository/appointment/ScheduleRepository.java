package com.wondersgroup.healthcloud.jpa.repository.appointment;


import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by longshasha on 16/5/23.
 */
public interface ScheduleRepository extends JpaRepository<AppointmentDoctorSchedule, String> {


    @Query("select a from AppointmentDoctorSchedule a where a.scheduleId=?1 and a.numSourceId=?2 and a.hospitalId=?3 ")
    AppointmentDoctorSchedule getAppointmentDoctorSchedule(String scheduleId, String numSourceId, String hospitalId);

    /**
     *逻辑删除所有的没有更新过的排班表
     * @param nowDate 删除的时间分割点
     */
//    @Modifying
//    @Query("update AppointmentDoctorSchedule a set a.delFlag = '1' where a.updateDate < ?1 " +
//            "or a.orderedNum >= a.reserveOrderNum")

    @Modifying
    @Query("update AppointmentDoctorSchedule a set a.delFlag = '1' where a.updateDate < ?1 ")
    void deleteSchedule(Date nowDate);
}
