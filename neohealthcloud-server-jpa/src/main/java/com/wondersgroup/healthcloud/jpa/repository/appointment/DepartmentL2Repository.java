package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentL2Department;
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
public interface DepartmentL2Repository extends JpaRepository<AppointmentL2Department, String> {
    /**
     * 根据医院id和一级科室id 和 二级科室code 查询二级科室信息
     * @param deptCode
     * @param hospitalId
     * @param departmentL1Id
     * @return
     */
    @Query("select a from AppointmentL2Department a where a.hosDeptCode = ?1 AND a.hospitalId = ?2 AND a.l1DepartmentId = ?3 ")
    AppointmentL2Department getAppointmentDepartmentL2(String deptCode, String hospitalId, String departmentL1Id);

    /**
     *逻辑删除所有的没有更新过的二级科室
     * @param date 删除的时间分割点
     */
    @Modifying
    @Query("update AppointmentL2Department a set a.delFlag = '1' where a.updateDate < ?1")
    void deleteDepartmentL2ByNowDate(Date date);


    /**
     *逻辑删除没有医生排班表的二级科室
     */
    @Modifying
    @Query(value = "update app_tb_appointment_department_l2 a set a.del_flag = '1' " +
            "        where NOT EXISTS(select 1 from app_tb_appointment_doctor_schedule c " +
            "                          where c.del_flag = '0' and a.id = c.department_l2_id )",nativeQuery = true)
    void deleteDept2HasNoDoctorAndSchedule();


    /**
     * 根据医院hospital_id 和一级科室id查询 二级科室列表
     * @param hospital_id
     * @param department_l1_id
     * @return
     */
    @Query(value = "select a from AppointmentL2Department a where a.delFlag=0 and a.hospitalId =?1 and a.l1DepartmentId =?2")
    List<AppointmentL2Department> findAppointmentL2DepartmentList(String hospital_id, String department_l1_id);


    /**
     * 修改科室预约 是否显示
     * @param isonsale
     * @param id
     */
    @Modifying
    @Query("update AppointmentL2Department a set a.isonsale = ?1  where a.id = ?2")
    void updateDeptIsonsaleById(String isonsale, String id);

    /**
     * 后台管理根据一级科室ID查询所有的二级科室
     * @param department_l1_id
     * @return
     */
    @Query(value = "select a from AppointmentL2Department a where  a.l1DepartmentId =?1")
    List<AppointmentL2Department> findManageAppointmentL2Department(String department_l1_id);
}
