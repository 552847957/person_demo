package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by longshasha on 16/5/23.
 */
public interface DoctorRepository extends JpaRepository<AppointmentDoctor, String> {


    @Query("select a from AppointmentDoctor a where a.hosDoctCode=?1 and a.l2DepartmentId=?2 and a.l1DepartmentId=?3  and a.hospitalId=?4")
    AppointmentDoctor getAppointmentDoctor(String doctCode, String l2DepartmentId, String l1DepartmentId, String hospitalId);


    /**
     * 逻辑删除所有的没有更新过的医生
     * @param date 删除的时间分割点
     */
    @Modifying
    @Query("update AppointmentDoctor a set a.delFlag = '1' where a.updateDate < ?1")
    void deleteAppointmentDoctorByNowDate(Date date);


    /**
     * 逻辑删除没有排班数据的医生
     */
    @Modifying
    @Query(value = "update app_tb_appointment_doctor a set a.del_flag = '1' " +
            "        where NOT EXISTS(select 1 from app_tb_appointment_doctor_schedule c " +
            "                          where c.del_flag = '0' and a.id = c.doctor_id)", nativeQuery = true)
    void deleteDoctorHasNoSchedule();

    /**
     * 修改医生是否显示
     * @param isonsale
     * @param doctorId
     */
    @Modifying
    @Query("update AppointmentDoctor a set a.isonsale = ?1  where a.id = ?2")
    void updateDoctorIsonsaleByDoctorId(String isonsale, String doctorId);

    /**
     * 根据医院Id查询医院有多少医生
     * @param hospitalId
     * @return
     */
    @Query("select count(a) from AppointmentDoctor a where a.hospitalId=?1 AND a.delFlag = '0' AND a.isonsale ='1'  ")
    int countDoctorNumByHospitalId(String hospitalId);

    @Query("select a from AppointmentDoctor a where a.doctName like %?1% AND a.delFlag = '0' AND a.isonsale ='1'  ")
    List<AppointmentDoctor> findDoctorListByKw(String kw,Pageable pageable);
}
