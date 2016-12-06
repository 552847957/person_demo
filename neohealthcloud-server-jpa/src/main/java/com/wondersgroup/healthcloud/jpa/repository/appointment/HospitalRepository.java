package com.wondersgroup.healthcloud.jpa.repository.appointment;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import org.springframework.data.domain.Pageable;
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
public interface HospitalRepository extends JpaRepository<AppointmentHospital, String> {

    /**根据code查询医院信息
     *
     * @param code
     * @return
     */
    @Query("select a from AppointmentHospital a where a.hosOrgCode=?1")
    AppointmentHospital getHospitalByCode(String code);


    /**
     * 逻辑删除删除没有update过的医院
     * @param date 删除的时间分割点
     */
    @Modifying
    @Query("update AppointmentHospital a set a.delFlag = '1' where a.updateDate < ?1")
    void deleteAppointmentHospitalByNowDate(Date date);


    /**
     * 删除没有一级科室的医院
     */
    @Modifying
    @Query(value = "update app_tb_appointment_hospital a set a.del_flag = '1' " +
            "        where NOT EXISTS(select 1 from app_tb_appointment_department_l1 c " +
            "                         where c.del_flag = '0' and a.id = c.hospital_id)",nativeQuery = true)
    void deleteHospitalHasNoDept1();


    /**
     * 查询所有在线医院列表
     */
    @Query("select a from AppointmentHospital a where a.isonsale= '1' AND a.delFlag = '0' ")
    List<AppointmentHospital> findAllOnsaleHospitalList(Pageable pageable);


    /**
     * 根据地区查询所有在线医院列表
     */
    @Query("select a from AppointmentHospital a where a.isonsale= '1' AND a.delFlag = '0' AND a.addressCounty = ?1 ")
    List<AppointmentHospital> findAllOnsaleHospitalListByAreaCode(String areaCode,Pageable pageable);

    /**
     * 根据条件查询医院列表
     * @param kw
     * @return
     */
    @Query(value = "select a from AppointmentHospital a where a.isonsale= '1' AND a.delFlag = '0' AND a.hosName like %?1% ")
    List<AppointmentHospital> findAllHospitalListByKw(String kw,Pageable pageable);
}
