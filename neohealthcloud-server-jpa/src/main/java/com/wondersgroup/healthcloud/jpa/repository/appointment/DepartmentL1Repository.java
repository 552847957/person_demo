package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL1Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;


/**
 * ░░░░░▄█▌▀▄▓▓▄▄▄▄▀▀▀▄▓▓▓▓▓▌█
 * ░░░▄█▀▀▄▓█▓▓▓▓▓▓▓▓▓▓▓▓▀░▓▌█
 * ░░█▀▄▓▓▓███▓▓▓███▓▓▓▄░░▄▓▐█▌
 * ░█▌▓▓▓▀▀▓▓▓▓███▓▓▓▓▓▓▓▄▀▓▓▐█
 * ▐█▐██▐░▄▓▓▓▓▓▀▄░▀▓▓▓▓▓▓▓▓▓▌█▌
 * █▌███▓▓▓▓▓▓▓▓▐░░▄▓▓███▓▓▓▄▀▐█
 * █▐█▓▀░░▀▓▓▓▓▓▓▓▓▓██████▓▓▓▓▐█
 * ▌▓▄▌▀░▀░▐▀█▄▓▓██████████▓▓▓▌█▌
 * ▌▓▓▓▄▄▀▀▓▓▓▀▓▓▓▓▓▓▓▓█▓█▓█▓▓▌█▌
 * █▐▓▓▓▓▓▓▄▄▄▓▓▓▓▓▓█▓█▓█▓█▓▓▓▐█
 * <p/>
 * Created by zhangzhixiu on 15/11/25.
 */
public interface DepartmentL1Repository extends JpaRepository<AppointmentL1Department, String> {
    /**
     * 根据医院id和科室代码查询一级科室
     * @param code
     * @param hospitalId
     * @return
     */
    @Query("select a from AppointmentL1Department a where a.hosDeptCode = ?1  AND a.hospitalId = ?2")
    AppointmentL1Department getAppointmentDepartmentL1(String code, String hospitalId);


    /**
     *逻辑删除所有的没有更新过的一级科室
     * @param date 删除的时间分割点
     */
    @Modifying
    @Query("update AppointmentL1Department a set a.delFlag = '1' where a.updateDate < ?1")
    void deleteDepartmentL1ByNowDate(Date date);

    /**
     * 删除没有二级科室的一级科室
     */
    @Modifying
    @Query(value = "update app_tb_appointment_department_l1 a set a.del_flag = '1' " +
            "        where NOT EXISTS(select 1 from app_tb_appointment_department_l2 c " +
            "                         where c.del_flag = '0' and a.id = c.department_l1_id)",nativeQuery = true)
    void deleteDept1HasNoDept2();


    /**
     * 根据医院Id查询一级科室
     * @param hospital_id
     * @return
     */
    @Query(value = "select a from AppointmentL1Department a where a.delFlag=0 and a.hospitalId =?1")
    List<AppointmentL1Department> findAllAppointmentL1Department(String hospital_id);


    /**
     * 后台管理查询所有的一级科室
     * @param hospital_id
     * @return
     */
    @Query(value = "select a from AppointmentL1Department a where a.hospitalId =?1 order by a.hosDeptCode asc ")
    List<AppointmentL1Department> findManageAppointmentL1Department(String hospital_id);
}
