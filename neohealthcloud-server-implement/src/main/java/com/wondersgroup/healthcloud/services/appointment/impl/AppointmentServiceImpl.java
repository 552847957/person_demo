package com.wondersgroup.healthcloud.services.appointment.impl;

import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.repository.appointment.*;
import com.wondersgroup.healthcloud.services.appointment.AppointmentService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/5/24.
 * 用于缓存webservice数据到本地
 */
@Transactional(readOnly = true)
@Service("appointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentL1Repository departmentL1Repository;

    @Autowired
    private DepartmentL2Repository departmentL2Repository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private JdbcTemplate jt;





    @Override
    @Transactional(readOnly = false)
    public void saveAndFlush(AppointmentHospital localHospital) {
        hospitalRepository.saveAndFlush(localHospital);
    }

    @Override
    public AppointmentHospital getHospitalByCode(String hosOrgCode) {
        return hospitalRepository.getHospitalByCode(hosOrgCode);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAndFlush(AppointmentL1Department localL1Department) {
        departmentL1Repository.saveAndFlush(localL1Department);
    }

    @Override
    public AppointmentL1Department getAppointmentDepartmentL1(String deptCode, String id) {
        return departmentL1Repository.getAppointmentDepartmentL1(deptCode,id);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAndFlush(AppointmentL2Department locaL2Department) {
        departmentL2Repository.saveAndFlush(locaL2Department);
    }

    @Override
    public AppointmentL2Department getAppointmentDepartmentL2(String deptCode, String hospitalId, String id) {
        return departmentL2Repository.getAppointmentDepartmentL2(deptCode,hospitalId,id);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAndFlush(AppointmentDoctorSchedule schedule) {
        scheduleRepository.saveAndFlush(schedule);
    }

    @Override
    public AppointmentDoctorSchedule getAppointmentDoctorSchedule(String scheduleId, String numSourceId, String hospitalId) {
        return scheduleRepository.getAppointmentDoctorSchedule(scheduleId, numSourceId, hospitalId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveAndFlush(AppointmentDoctor doctor) {
        doctorRepository.saveAndFlush(doctor);
    }

    @Override
    public AppointmentDoctor getAppointmentDoctor(String doctCode, String l2DepartmentId, String l1DepartmentId, String hospitalId) {
        return doctorRepository.getAppointmentDoctor(doctCode, l2DepartmentId, l1DepartmentId, hospitalId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAppointmentHospitalByNowDate( Date nowDate) {
        hospitalRepository.deleteAppointmentHospitalByNowDate( nowDate);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDepartmentL1ByNowDate( Date nowDate) {
        departmentL1Repository.deleteDepartmentL1ByNowDate(nowDate);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDepartmentL2ByNowDate(Date nowDate) {
        departmentL2Repository.deleteDepartmentL2ByNowDate( nowDate);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAppointmentDoctorByNowDate( Date nowDate) {
        doctorRepository.deleteAppointmentDoctorByNowDate( nowDate);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSchedule(Date nowDate) {
        scheduleRepository.deleteSchedule( nowDate);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDoctorHasNoSchedule() {
        doctorRepository.deleteDoctorHasNoSchedule();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDept2HasNoDoctorAndSchedule() {
        departmentL2Repository.deleteDept2HasNoDoctorAndSchedule();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDept1HasNoDept2() {
        departmentL1Repository.deleteDept1HasNoDept2();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteHospitalHasNoDept1() {
        hospitalRepository.deleteHospitalHasNoDept1();
    }


    /**
     * 查询本地需要修改状态的订单(job时间是晚上11点,查询的是当天和昨天的)
     * @return
     */
    @Override
    public List<OrderDto> findOrderListNeedUpdateStatus() {
        String sql = " select o.*,s.schedule_date " +
                " from app_tb_appointment_order o " +
                " left join app_tb_appointment_doctor_schedule s on o.schedule_id = s.id " +
                " where s.schedule_date >= '%s' and s.schedule_date<= '%s' " +
                " and o.`status` !='3' and o.`status` !='4' and o.`status` !='9' " +
                " GROUP BY o.user_card_id ";

        String startDate = DateFormatter.dateFormat(DateUtils.addDay(new Date(), -1));
        String endDate = DateFormatter.dateFormat(new Date());
        sql = String.format(sql,startDate,endDate);
        sql = String.format(sql);
        return jt.query(sql, new BeanPropertyRowMapper(OrderDto.class));
    }

    /**
     * 给医院表设置医生数量
     */
    @Override
    @Transactional(readOnly = false)
    public void setDoctorNumToHospital() {
        hospitalRepository.setDoctorNumToHospital();
    }

    @Override
    public AppointmentDoctor findDoctorById(String id) {
        return doctorRepository.findOne(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateDoctor(String avatar, String doctInfo, String id) {
        doctorRepository.updateDoctor(avatar,doctInfo,id);
    }


}
