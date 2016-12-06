package com.wondersgroup.healthcloud.services.appointment.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentDoctor;
import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import com.wondersgroup.healthcloud.jpa.repository.appointment.DoctorRepository;
import com.wondersgroup.healthcloud.jpa.repository.appointment.HospitalRepository;
import com.wondersgroup.healthcloud.jpa.repository.appointment.ScheduleRepository;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by longshasha on 16/12/5.
 *
 * 用于api 客户端接口
 */
@Service
public class AppointmentApiServiceImpl implements AppointmentApiService{

    @Autowired
    private HospitalRepository hospitalRepository;

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
     * @return
     */
    @Override
    public List<AppointmentDoctor> findDoctorListByKw(String kw, int pageSize, int pageNum) {
        String sql = "select a.* , h.hos_name as 'hospitalName',h.hospital_rule as 'reservationRule', d.dept_name as 'departmentName'  " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " left join app_tb_appointment_department_l2 d on a.department_l2_id = d.id " ;
        if(StringUtils.isNotBlank(kw)){
            sql += " where a.doct_name like '%"+kw+"%' ";
        }
        sql += " limit " + (pageNum - 1) * pageSize + " , " + pageSize+1;
        List<AppointmentDoctor> list = jt.query(sql.toString(), new BeanPropertyRowMapper(AppointmentDoctor.class));
        return list;
    }

    @Override
    public Map<String, Object> countDoctorReserveOrderNumByDoctorId(String doctorId) {

        String sql = "select count(a.id) as scheduleNum ,SUM(a.reserve_order_num) as reserveOrderNum "+
                " from app_tb_appointment_doctor_schedule a where a.del_flag = '0' AND a.status = '1'" +
                " AND a.doctor_id = '%s' ";

        return jt.queryForMap(String.format(sql, doctorId));
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
}
