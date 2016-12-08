package com.wondersgroup.healthcloud.services.appointment.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.repository.appointment.*;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 *
 * 用于api 客户端接口
 */
@Service
public class AppointmentApiServiceImpl implements AppointmentApiService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentL1Repository departmentL1Repository;

    @Autowired
    private DepartmentL2Repository departmentL2Repository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private JdbcTemplate jt;


    /**
     * 查询上海市下面的区
     * @param areaCode
     * @return
     */
    @Override
    public List<Map<String, Object>> findAppointmentAreaByUpperCode(String areaCode) {

        return jt.queryForList(String.format("select code, explain_memo , upper_code from t_dic_area where upper_code ='%s' ", areaCode));
    }


    /**
     * 根据地区查询上架的医院列表
     * @param areaCode
     * @return
     */
    @Override
    public List<AppointmentHospital> findAllHospitalListByAreaCodeOrKw(String kw,String areaCode,Integer page,int pageSize) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,"weight"));
        List<AppointmentHospital> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(kw)){
            list = hospitalRepository.findAllHospitalListByKw(kw, new PageRequest(page - 1, pageSize + 1, sort));
        }else if(StringUtils.isBlank(areaCode) || "310100000000".equals(areaCode)){
            list = hospitalRepository.findAllOnsaleHospitalList(new PageRequest(page - 1, pageSize + 1, sort));
        }else{
            list = hospitalRepository.findAllOnsaleHospitalListByAreaCode(areaCode,new PageRequest(page-1, pageSize+1, sort));
        }

        return list;

    }

    /**
     * 根据医院Id查询医院中医生的数量
     * @param hospitalId
     * @return
     */
    @Override
    public int countDoctorNumByHospitalId(String hospitalId) {
        return doctorRepository.countDoctorNumByHospitalId(hospitalId);
    }


    /**
     * 条件查询医院列表
     * @param kw
     * @return
     */
    @Override
    public List<AppointmentHospital> findAllHospitalListByKw(String kw, Integer pageSize, int pageNum) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,"weight"));
        return hospitalRepository.findAllHospitalListByKw(kw,new PageRequest(pageNum - 1, pageSize + 1, sort));
    }

    /**
     * 根据关键字搜索医生列表(分页)
     * @param kw
     * @param pageSize
     * @param pageNum
     * @param hasDepartRegistration 是否有科室预约 科室预约那一条要放在列表第一条 医生列表的分页要考虑进去
     *
     * @return
     */
    @Override
    public List<AppointmentDoctor> findDoctorListByKw(String kw, int pageSize, int pageNum,Boolean hasDepartRegistration) {
        String sql = "select a.* , h.hos_name as 'hospitalName',h.hospital_rule as 'reservationRule', d.dept_name as 'departmentName'  " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " left join app_tb_appointment_department_l2 d on a.department_l2_id = d.id " ;
        if(StringUtils.isNotBlank(kw)){
            sql += " where a.doct_name like '%"+kw+"%' ";
        }
        if(!hasDepartRegistration){
            sql += " limit " + (pageNum - 1) * pageSize + " , " + (pageSize+1);
        }else if(pageNum == 1){
            sql += " limit " + (pageNum - 1) * pageSize + " , " + pageSize;
        }else if(pageNum>1){
            sql += " limit " + ((pageNum - 1) * pageSize -1) + " , " + (pageSize+1);
        }

        List<AppointmentDoctor> list = jt.query(sql.toString(), new BeanPropertyRowMapper(AppointmentDoctor.class));
        return list;
    }

    /**
     * 根据医生Id 查询医生的排班资源
     * @param doctorId
     * @return
     */
    @Override
    public Map<String, Object> countDoctorReserveOrderNumByDoctorId(String doctorId) {

        String sql = "select count(a.id) as scheduleNum ,SUM(a.reserve_order_num) as reserveOrderNum "+
                " from app_tb_appointment_doctor_schedule a where a.del_flag = '0' AND a.status = '1'" +
                " AND a.doctor_id = '%s' ";

        sql = String.format(sql, doctorId);
        return jt.queryForMap(sql);
    }

    /**
     * 根据医院Id查询医院详情
     * @param hospitalId
     * @return
     */
    @Override
    public AppointmentHospital findHospitalById(String hospitalId) {
        return hospitalRepository.findOne(hospitalId);
    }

    /**
     * 根据医院Id查询所有的一级科室
     * @param hospital_id
     * @return
     */
    @Override
    public List<AppointmentL1Department> findAllAppointmentL1Department(String hospital_id) {

        List<AppointmentL1Department> l1Departments = departmentL1Repository.findAllAppointmentL1Department(hospital_id);

        return l1Departments;
    }

    /**
     * 根据医院Id和一级科室Id查询二级科室
     * @param hospital_id
     * @param department_l1_id
     * @return
     */
    @Override
    public List<AppointmentL2Department> findAppointmentL2Department(String hospital_id, String department_l1_id) {
        List<AppointmentL2Department> l2Departments = departmentL2Repository.findAppointmentL2DepartmentList(hospital_id, department_l1_id);

        return l2Departments;
    }

    /**
     * 根据二级科室Id查询二级科室的排班资源信息
     * @param department_l2_id
     * @return
     */
    @Override
    public Map<String, Object> countDepartmentReserveOrderNumByDepartmentId(String department_l2_id) {
        String sql = "select count(a.id) as scheduleNum,SUM(a.reserve_order_num) as reserveOrderNum,MAX(a.visit_level_code) as visitLevelCode "+
                " from app_tb_appointment_doctor_schedule a  " +
                " where a.del_flag = '0' AND a.status = '1' AND a.doctor_id is null " +
                " AND a.department_l2_id = '%s'" ;

        sql = String.format(sql, department_l2_id);
        return jt.queryForMap(sql);
    }

    /**
     * 根据二级科室Id查询二级科室
     * @param department_l2_id
     * @return
     */
    @Override
    public AppointmentL2Department findAppointmentL2DepartmentById(String department_l2_id) {
        return departmentL2Repository.findOne(department_l2_id);
    }

    /**
     * 根据二级科室Id查询医院信息
     * @param department_l2_id
     * @return
     */
    @Override
    public AppointmentHospital findHospitalByDepartmentL2Id(String department_l2_id) {

        String sql = " select h.* from app_tb_appointment_department_l2 a " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " where a.id = '%s' ";
        sql = String.format(sql,department_l2_id);

        return jt.queryForObject(sql, new BeanPropertyRowMapper<>(AppointmentHospital.class));
    }


    /**
     * 根据二级科室Id和就诊日期查询排班数据
     * @param department_l2_id
     * @param schedule_date
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<ScheduleDto> findScheduleByDepartmentL2IdAndScheduleDate(String department_l2_id, String schedule_date, Integer pageNum, int pageSize) {

        String sql = "select s.*,if(s.doctor_id is null,d.dept_name,doc.doct_name) as name ,if(s.doctor_id is null,'2','1') as type," +
                " if(s.doctor_id is null,d.reservation_num,doc.reservation_num) as reservationNum," +
                " doc.avatar,doc.doct_tile as dutyName,doc.doct_info as specialty " +
                " from app_tb_appointment_doctor_schedule s " +
                " left join app_tb_appointment_doctor doc on s.doctor_id = doc.id " +
                " left join app_tb_appointment_department_l2 d on s.department_l2_id = d.id " +
                " where s.`status`= '1' and s.del_flag = '0' and s.department_l2_id='%s'  " +
                " and s.schedule_date = '%s' and s.start_time > '%s' ";

        sql += " limit " + (pageNum - 1) * pageSize + " , " + (pageSize+1);

        sql = String.format(sql,department_l2_id,schedule_date, DateFormatter.dateTimeFormat(new Date()));

        return jt.query(sql.toString(), new BeanPropertyRowMapper(ScheduleDto.class));
    }
}
