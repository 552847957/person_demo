package com.wondersgroup.healthcloud.services.appointment.impl;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.repository.appointment.*;
import com.wondersgroup.healthcloud.registration.client.OrderClient;
import com.wondersgroup.healthcloud.registration.entity.request.*;
import com.wondersgroup.healthcloud.registration.entity.response.OrderCancelResponse;
import com.wondersgroup.healthcloud.registration.entity.response.OrderResultResponse;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorReservationException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneContactException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneScheduleException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneSchedulePayModeException;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.utils.registration.SignatureGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    private ContactRepository contactRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private OrderClient orderClient;//调用第三方webservice接口



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
                " if(s.doctor_id is null,d.id,doc.id) as doctorId, " +
                " if(s.doctor_id is null,d.reservation_num,doc.reservation_num) as reservationNum," +
                " doc.avatar,doc.doct_tile as dutyName,doc.doct_info as specialty, " +
                " d.dept_name as departmentName,h.hos_name as hospitalName  " +
                " from app_tb_appointment_doctor_schedule s " +
                " left join app_tb_appointment_doctor doc on s.doctor_id = doc.id " +
                " left join app_tb_appointment_department_l2 d on s.department_l2_id = d.id " +
                " left join app_tb_appointment_hospital h on s.hospital_id = h.id " +
                " where s.`status`= '1' and s.del_flag = '0' and s.department_l2_id='%s'  " +
                " and s.schedule_date = '%s' and s.start_time > '%s' order by s.start_time asc ";

        sql += " limit " + (pageNum - 1) * pageSize + " , " + (pageSize+1);

        sql = String.format(sql,department_l2_id,schedule_date, DateFormatter.dateTimeFormat(new Date()));

        return jt.query(sql.toString(), new BeanPropertyRowMapper(ScheduleDto.class));
    }

    /**
     * 根据医生Id查询医生详情
     * @param id
     * @return
     */
    @Override
    public AppointmentDoctor findDoctorById(String id) {
        String sql = "select a.* , h.hos_name as 'hospitalName',h.hospital_rule as 'reservationRule', d.dept_name as 'departmentName'  " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " left join app_tb_appointment_department_l2 d on a.department_l2_id = d.id " +
                " where a.id = '%s' " ;
        sql = String.format(sql,id);

        return jt.queryForObject(sql, new BeanPropertyRowMapper<>(AppointmentDoctor.class));
    }

    /**
     * 根据二级科室Id查询二级科室详情
     * @param id
     * @return
     */
    @Override
    public AppointmentL2Department findL2DepartmentById(String id) {
        String sql = "select a.* , h.hos_name as 'hospitalName',h.hospital_rule as 'reservationRule' " +
                " from app_tb_appointment_department_l2 a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " where a.id = '%s' " ;
        sql = String.format(sql,id);

        return jt.queryForObject(sql, new BeanPropertyRowMapper<>(AppointmentL2Department.class));
    }


    @Override
    public List<ScheduleDto> findScheduleByDepartmentL2IdOrDoctorId(String id,String type, Integer pageNum, int pageSize) {
        String sql = "select s.*,if(s.doctor_id is null,d.dept_name,doc.doct_name) as name ,if(s.doctor_id is null,'2','1') as type," +
                " if(s.doctor_id is null,d.reservation_num,doc.reservation_num) as reservationNum," +
                " doc.avatar,doc.doct_tile as dutyName,doc.doct_info as specialty " +
                " from app_tb_appointment_doctor_schedule s " +
                " left join app_tb_appointment_doctor doc on s.doctor_id = doc.id " +
                " left join app_tb_appointment_department_l2 d on s.department_l2_id = d.id " +
                " where s.`status`= '1' and s.del_flag = '0'" ;

        //医生
        if("1".equals(type)){
            sql += " and s.doctor_id='%s' ";
         //科室
        }else if("2".equals(type)){
            sql += " and s.department_l2_id='%s' and s.doctor_id is null  ";
        }
        sql += " and s.start_time > '%s'  order by s.start_time asc";
        sql += " limit " + (pageNum - 1) * pageSize + " , " + (pageSize+1);

        sql = String.format(sql,id, DateFormatter.dateTimeFormat(new Date()));

        return jt.query(sql.toString(), new BeanPropertyRowMapper(ScheduleDto.class));
    }

    /**
     * 根据用户Id查询所有的订单 按照下单时间倒序排列
     * @param isList 为true时id为uid 为false时是订单Id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<OrderDto> findOrderByUidOrId(String id, Integer pageNum, Integer pageSize,Boolean isList) {
        String sql = "select a.*,c.doct_name as doctorName,c.doct_tile as dutyName, " +
                " d.dept_name as departmentName,e.hos_name as hospitalName," +
                " b.start_time as startTime,b.end_time as endTime,b.`status` as scheduleStatus," +
                " b.visit_level_code as visitLevelCode,b.visit_cost as visitCost,b.schedule_date as scheduleDate," +
                " e.close_days as closeDays,e.close_time_hour as closeTimeHour " +
                " from app_tb_appointment_order a " +
                " left join app_tb_appointment_doctor_schedule b on a.appointment_schedule_id = b.id " +
                " left join app_tb_appointment_doctor c on b.doctor_id = c.id" +
                " left join app_tb_appointment_department_l2 d on b.department_l2_id = d.id" +
                " left join app_tb_appointment_hospital e on a.hospital_id = e.id ";


        if(isList){
            sql += " where a.uid = '%s' order by a.create_date desc " +
                   " limit " + (pageNum - 1) * pageSize + " , " + (pageSize+1);
        }else{
            sql += " where a.id = '%s'  ";
        }
        sql = String.format(sql,id);
        return jt.query(sql.toString(), new BeanPropertyRowMapper(OrderDto.class));
    }

    /**
     * 提交预约
     * @param contactId
     * @param scheduleId
     * @param orderType
     * @return
     */
    @Override
    public OrderDto submitUserReservation(String contactId, String scheduleId, String orderType) {
        AppointmentContact contact = contactRepository.findOne(contactId);


        if(contact == null){
            throw new NoneContactException();
        }

        AppointmentDoctorSchedule schedule = scheduleRepository.findOne(scheduleId);
        if(schedule == null){
            throw new NoneScheduleException();
        }

        AppointmentDoctor doctor = new AppointmentDoctor();
        if("1".equals(orderType) && StringUtils.isNotBlank(schedule.getDoctorId())){
            doctor = doctorRepository.findOne(schedule.getDoctorId());
        }
        AppointmentHospital hospital = hospitalRepository.findOne(schedule.getHospitalId());
        AppointmentL2Department l2Department = departmentL2Repository.findOne(schedule.getL2DepartmentId());

        SubmitOrderRequest submitOrderRequest = new SubmitOrderRequest();
        SubmitOrder submitOrder = new SubmitOrder();


        submitOrder.setHosOrgCode(hospital.getHosOrgCode());
        submitOrder.setHosName(hospital.getHosName());
        String payMode = hospital.getPayMode();
        if(payMode.contains("3")){
            submitOrder.setPayMode("3"); //3 窗口支付
        }else if(payMode.contains("2")){
            submitOrder.setPayMode("2"); //2 诊疗卡支付
        }else{
            throw new NoneSchedulePayModeException();
        }

        submitOrder.setDeptName(l2Department.getDeptName());
        submitOrder.setHosDeptCode(l2Department.getHosDeptCode());

        if(doctor!=null){
            submitOrder.setDoctName(doctor.getDoctName());
            submitOrder.setHosDoctCode(doctor.getHosDoctCode());
        }

        submitOrder.setScheduleId(schedule.getScheduleId());
        submitOrder.setNumSourceId(schedule.getNumSourceId());
        submitOrder.setOrderTime(schedule.getStartTime());
        submitOrder.setPayState("2");//1:已付费 2:未付费
        submitOrder.setTimeRange(schedule.getTimeRange());
        submitOrder.setVisitCost(schedule.getVisitCost());
        submitOrder.setVisitLevel(schedule.getVisitLevel());
        submitOrder.setVisitLevelCode(schedule.getVisitLevelCode());

        //如果是主注册人
        if("1".equals(contact.getIsMain())){
            submitOrder.setPlatformUserId(contact.getPlatformUserId());
        //如果是成员
        }else{
            AppointmentContact mainContract = contactRepository.findMainContactByUid(contact.getUid());
            submitOrder.setPlatformUserId(mainContract.getPlatformUserId());
            submitOrder.setMemberId(contact.getMemberId());
        }
        submitOrder.setUserBD(IdcardUtils.getBirthStrByIdCard(contact.getIdcard()));
        submitOrder.setUserCardId(contact.getIdcard());
        submitOrder.setUserCardType("1");
        submitOrder.setUserName(contact.getName());
        submitOrder.setUserPhone(contact.getMobile());
        submitOrder.setUserSex(IdcardUtils.getGenderByIdCard(contact.getIdcard()));

        if(StringUtils.isNotBlank(contact.getMediCardId())){
            submitOrder.setMediCardId(contact.getMediCardId());
            submitOrder.setMediCardType("1");//1：社保卡（医保卡
        }

        submitOrderRequest.submitOrder = submitOrder;
        submitOrderRequest.requestMessageHeader = new RequestMessageHeader(environment);
        submitOrderRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(submitOrderRequest));

        String xmlRequest = JaxbUtil.convertToXml(submitOrderRequest);
        OrderResultResponse orderResultResponse = orderClient.submitOrderByUserInfo(xmlRequest);

        if(!"0".equals(orderResultResponse.messageHeader.getCode())){
            throw new ErrorReservationException(orderResultResponse.messageHeader.getDesc());
        }

        //刷新排班资源
        schedule.setOrderedNum(schedule.getOrderedNum()+1);
        schedule.setReserveOrderNum(schedule.getReserveOrderNum()-1);
        scheduleRepository.saveAndFlush(schedule);

        if(doctor!=null){
            doctor.setReservationNum(doctor.getReservationNum()+1);
            doctorRepository.saveAndFlush(doctor);
        }else{
            l2Department.setReservationNum(l2Department.getReservationNum()+1);
            departmentL2Repository.saveAndFlush(l2Department);
        }

        OrderDto orderDto = saveOrderToLocal(submitOrder,orderResultResponse,contact,doctor,schedule,l2Department);
        return orderDto;
    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    public void cancelReservationOrderById(String id) {
        AppointmentOrder order = orderRepository.findOne(id);
        AppointmentHospital hospital = hospitalRepository.findOne(order.getHospitalId());

        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        OrderCancelR orderCancelR = new OrderCancelR();
        orderCancelR.setHosOrgCode(hospital.getHosOrgCode());
        orderCancelR.setOrderId(order.getOrderId());
        orderCancelR.setPlatformUserId(order.getPlatformUserId());
        orderCancelR.setTakePassword(order.getTakePassword());
        orderCancelR.setNumSourceId(order.getNumSourceId());
        orderCancelR.setCancelObj("1");
        orderCancelR.setCancelDesc("");
        orderCancelR.setCancelReason("1");
        orderCancelRequest.orderCancelR = orderCancelR;
        orderCancelRequest.requestMessageHeader = new RequestMessageHeader(environment);
        String sign = SignatureGenerator.generateSignature(orderCancelRequest);
        orderCancelRequest.requestMessageHeader.setSign(sign);
        OrderCancelResponse orderCancelResponse = orderClient.orderCancel(JaxbUtil.convertToXml(orderCancelRequest));

        if(!"0".equals(orderCancelResponse.messageHeader.getCode())){
            throw new ErrorReservationException(orderCancelResponse.messageHeader.getDesc());
        }
        //修改order的状态为 取消
        order.setStatus("3");
        order.setUpdateDate(new Date());
        orderRepository.saveAndFlush(order);
    }

    /**
     * 将订单保存到本地
     * @param submitOrder
     * @param orderResultResponse
     * @param contact
     * @param doctor
     * @param schedule
     * @param l2Department
     * @return
     */
    private OrderDto saveOrderToLocal(SubmitOrder submitOrder,OrderResultResponse orderResultResponse, AppointmentContact contact, AppointmentDoctor doctor, AppointmentDoctorSchedule schedule, AppointmentL2Department l2Department) {
        String orderId = orderResultResponse.orderResult.getOrderId();
        String visitNo = orderResultResponse.orderResult.getVisitNo();
        String takePassword = orderResultResponse.orderResult.getTakePassword();

        AppointmentOrder order = new AppointmentOrder();
        order.setId(IdGen.uuid());
        order.setUid(contact.getUid());
        order.setPlatformUserId(submitOrder.getPlatformUserId());
        order.setOrderId(orderId);
        order.setVisitNo(visitNo);
        order.setTakePassword(takePassword);
        order.setAppointmentScheduleId(schedule.getId());
        order.setScheduleId(schedule.getScheduleId());
        order.setNumSourceId(schedule.getNumSourceId());
        order.setStatus(schedule.getStatus());
        order.setHospitalId(l2Department.getHospitalId());
        order.setL1DepartmentId(l2Department.getL1DepartmentId());
        order.setL2DepartmentId(l2Department.getId());
        order.setContactId(contact.getId());

        if(StringUtils.isNotBlank(schedule.getDoctorId())){
            order.setOrderType("2");
        }else{
            order.setOrderType("1");
        }

        order.setPayMode(submitOrder.getPayMode());
        order.setDoctorId(doctor.getId());
        order.setUserCardType("1");
        order.setUserCardId(contact.getIdcard());
        if(StringUtils.isNotBlank(contact.getMediCardId())){
            order.setMediCardId(contact.getMediCardId());
            order.setMediCardType("1");//1：社保卡（医保卡
        }
        order.setUserName(contact.getName());
        order.setUserPhone(contact.getMobile());
        order.setUserSex(IdcardUtils.getGenderByIdCard(contact.getIdcard()));
        order.setUserBd(IdcardUtils.getBirthStrByIdCard(contact.getIdcard()));
        order.setOrderStatus("1");//1:预约成功
        order.setOrderTime(new Date());
        order.setCreateDate(new Date());
        order.setUpdateDate(new Date());
        order.setDelFlag("0");
        orderRepository.saveAndFlush(order);

        return findOrderByUidOrId(order.getId(),null,null,false).get(0);

    }

}
