package com.wondersgroup.healthcloud.services.appointment.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.repository.appointment.DepartmentL1Repository;
import com.wondersgroup.healthcloud.jpa.repository.appointment.DepartmentL2Repository;
import com.wondersgroup.healthcloud.jpa.repository.appointment.HospitalRepository;
import com.wondersgroup.healthcloud.jpa.repository.appointment.SmsTempletRepository;
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
import java.util.Map;

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
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentL1Repository departmentL1Repository;

    @Autowired
    private DepartmentL2Repository departmentL2Repository;

    @Autowired
    private SmsTempletRepository smsTempletRepository;


    /**
     * 根据条件查询医院列表
     * @param name
     * @param areaCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<AppointmentHospital> findAllManageHospitalListByAreaCodeAndName(String name, String areaCode, int pageNum, int pageSize,Boolean isPage) {
        String sql = "select * from app_tb_appointment_hospital a where 1=1 ";

        if(StringUtils.isNotBlank(name)){
            sql += " and a.hos_name like '%"+name+"%' ";
        }
        if(StringUtils.isNotBlank(areaCode) && !"310100000000".equals(areaCode)){
            sql += " and a.address_county = '"+areaCode+"' ";
        }

        sql += " order by a.address_county asc " ;
        if(isPage){
            sql +=  " limit  "+ (pageNum - 1) * pageSize + " , " + pageSize;
        }

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

            //如果有医院没有短信模板的就不能设置为启用
            List<String> hospitalCodes = Lists.newArrayList();
            for (AppointmentHospital hospital : hospitals){
                hospitalCodes.add(hospital.getHosOrgCode());
            }

            List<AppointmentSmsTemplet> smsTemplets = smsTempletRepository.findSmsTempletsByHospitalCodes(hospitalCodes);
            if(hospitals.size()>smsTemplets.size()){
                throw new ErrorAppointmentManageException("选中的医院中有医院没有短信模板,请联系开发人员添加");
            }

        }
        hospitalRepository.batchSetIsonsaleByHospitalIds(isonsale,hospitalIds);

    }

    @Override
    public List<AppointmentL1Department> findManageAppointmentL1Department(String hospital_id) {
        return departmentL1Repository.findManageAppointmentL1Department(hospital_id);
    }

    @Override
    public List<AppointmentL2Department> findManageAppointmentL2Department(String department_l1_id) {
        return departmentL2Repository.findManageAppointmentL2Department(department_l1_id);
    }

    /**
     * 查询医生列表
     * @param parameter
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<AppointmentDoctor> findAllManageDoctorListByMap(Map<String, Object> parameter, int pageNum, int pageSize) {
        String sql = "select a.* " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id ";


        String whereSql = getManageDoctorListWhereSql(parameter);
        sql += whereSql;
        sql += "order by h.address_county,a.hospital_id,a.department_l1_id,a.department_l2_id,a.doct_code limit " + (pageNum - 1) * pageSize  + " , " + pageSize;
        List<AppointmentDoctor> list = jt.query(sql, new BeanPropertyRowMapper(AppointmentDoctor.class));
        return list;
    }

    /**
     * 根据参数查询医生列表
     * @param parameter
     * @return
     */
    private String getManageDoctorListWhereSql(Map<String, Object> parameter) {
        StringBuffer sb = new StringBuffer();
        sb.append(" where 1=1 ");
        String areaCode = parameter.get("areaCode")==null?"":parameter.get("areaCode").toString();
        String hospitalId = parameter.get("hospitalId")==null?"":parameter.get("hospitalId").toString();
        String departL1Id = parameter.get("departL1Id")==null?"":parameter.get("departL1Id").toString();
        String departL2Id = parameter.get("departL2Id")==null?"":parameter.get("departL2Id").toString();
        String name = parameter.get("name")==null?"":parameter.get("name").toString();

        if(StringUtils.isNotBlank(areaCode) && !"310100000000".equals(areaCode) ){
            sb.append(" and h.address_county = '"+areaCode+"' ");
        }
        if(StringUtils.isNotBlank(hospitalId)){
            sb.append(" and a.hospital_id = '"+hospitalId+"' ");
        }
        if(StringUtils.isNotBlank(departL1Id)){
            sb.append(" and a.department_l1_id = '"+departL1Id+"' ");
        }
        if(StringUtils.isNotBlank(departL2Id)){
            sb.append(" and a.department_l2_id = '"+departL2Id+"' ");
        }
        if(StringUtils.isNotBlank(name)){
            sb.append(" and a.doct_name like '%"+name+"%' ");
        }

        return sb.toString();
    }

    /**
     * 查询医生总数
     * @param parameter
     * @return
     */
    @Override
    public int countDoctorByMap(Map parameter) {
        String sql = "select count(a.id) " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id ";

        sql += getManageDoctorListWhereSql(parameter);
        Integer count = jt.queryForObject(sql, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public AppointmentSmsTemplet findSmsTempletByHosCode(String hosOrgCode) {
        return smsTempletRepository.findOne(hosOrgCode);
    }

}
