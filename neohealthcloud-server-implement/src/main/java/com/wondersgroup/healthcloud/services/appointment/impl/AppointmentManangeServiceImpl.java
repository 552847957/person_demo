package com.wondersgroup.healthcloud.services.appointment.impl;

import com.wondersgroup.healthcloud.jpa.entity.appointment.AppointmentHospital;
import com.wondersgroup.healthcloud.jpa.repository.appointment.HospitalRepository;
import com.wondersgroup.healthcloud.services.appointment.AppointmentManangeService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorAppointmentManageException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by longshasha on 16/12/12.
 * 用于预约挂号后台管理
 */
@Service
@Transactional(readOnly = true)
public class AppointmentManangeServiceImpl implements AppointmentManangeService {

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    HospitalRepository hospitalRepository;


    /**
     * 根据条件查询医院列表
     * @param name
     * @param areaCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<AppointmentHospital> findAllManageHospitalListByAreaCodeAndName(String name, String areaCode, int pageNum, int pageSize) {
        String sql = "select * from app_tb_appointment_hospital a where 1=1 ";

        if(StringUtils.isNotBlank(name)){
            sql += " and a.hos_name like '%"+name+"%' ";
        }
        if(StringUtils.isNotBlank(areaCode) && !"310100000000".equals(areaCode)){
            sql += " and a.address_county = '"+areaCode+"' ";
        }

        sql += " order by a.address_county asc limit  "+ (pageNum - 1) * pageSize + " , " + pageSize;

        return jt.query(sql.toString(), new BeanPropertyRowMapper(AppointmentHospital.class));
    }

    /**
     * 根据条件查询医院总数量
     * @param name
     * @param areaCode
     * @return
     */
    @Override
    public int countHospitalsByAreaCode(String name, String areaCode) {

        String sql = "select count(a.id) from app_tb_appointment_hospital a where 1=1 ";

        if(StringUtils.isNotBlank(name)){
            sql += " and a.hos_name like '%"+name+"%' ";
        }
        if(StringUtils.isNotBlank(areaCode) && !"310100000000".equals(areaCode)){
            sql += " and a.address_county = '"+areaCode+"' ";
        }

        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;

    }

    /**
     * 后台订单列表
     * @param patientName
     * @param patientMobile
     * @param id
     * @param pageNum
     * @param pageSize
     * @param isList
     * @return
     */
    @Override
    public List<OrderDto> findAllManageOrderListByNameAndMobile(String patientName, String patientMobile, String id,int pageNum, int pageSize,Boolean isList) {
        String sql = "select a.*,c.doct_name as doctorName,c.doct_tile as dutyName, " +
                " d.dept_name as departmentName,e.hos_name as hospitalName," +
                " b.start_time as startTime,b.end_time as endTime,b.`status` as scheduleStatus," +
                " b.visit_level_code as visitLevelCode,b.visit_cost as visitCost,b.schedule_date as scheduleDate," +
                " e.close_days as closeDays,e.close_time_hour as closeTimeHour,r.regmobilephone,b.time_range as timeRange " +
                " from app_tb_appointment_order a " +
                " left join app_tb_register_info r on a.uid = r.registerid  "+
                " left join app_tb_appointment_doctor_schedule b on a.appointment_schedule_id = b.id " +
                " left join app_tb_appointment_doctor c on b.doctor_id = c.id" +
                " left join app_tb_appointment_department_l2 d on b.department_l2_id = d.id" +
                " left join app_tb_appointment_hospital e on a.hospital_id = e.id ";


        if(isList){
            sql += " where 1=1 ";
            if(StringUtils.isNotBlank(patientName)){
                sql += " and a.user_name like '%"+patientName+"%' ";
            }
            if(StringUtils.isNotBlank(patientMobile))
                sql += " and a.user_phone like '%"+patientMobile+"%' ";
            sql += " order by a.create_date desc " +
                    " limit " + (pageNum - 1) * pageSize + " , " + pageSize;
        }else{
            sql += " where a.id = '%s'  ";
            sql = String.format(sql,id);
        }
        return jt.query(sql.toString(), new BeanPropertyRowMapper(OrderDto.class));
    }

    @Override
    public int countOrdersByNameAndMobile(String patientName, String patientMobile) {

        String sql = "select count(a.id) from app_tb_appointment_order a where 1=1 ";

        if(StringUtils.isNotBlank(patientName)){
            sql += " and a.user_name like '%"+patientName+"%' ";
        }
        if(StringUtils.isNotBlank(patientMobile)){
            sql += " and a.user_phone like '%"+patientMobile+"%' ";
        }

        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    /**
     * 批量启用或停用医院
     * @param hospitalIds
     * @param isonsale
     */
    @Override
    @Transactional(readOnly = false)
    public void batchSetIsonsaleByHospitalIds(List<String> hospitalIds, String isonsale) {

        if("1".equals(isonsale)){
            //查询
            List<AppointmentHospital> hospitals = hospitalRepository.findPicIsBlankHosiptalsByIds(hospitalIds);
            if (hospitals.size()>0)
                throw new ErrorAppointmentManageException("选中的医院图片必须设置完整");
        }
        hospitalRepository.batchSetIsonsaleByHospitalIds(isonsale,hospitalIds);

    }
}
