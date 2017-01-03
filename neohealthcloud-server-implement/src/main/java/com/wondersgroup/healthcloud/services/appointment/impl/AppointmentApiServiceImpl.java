package com.wondersgroup.healthcloud.services.appointment.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.repository.appointment.*;
import com.wondersgroup.healthcloud.registration.client.DoctInfoClient;
import com.wondersgroup.healthcloud.registration.client.OrderClient;
import com.wondersgroup.healthcloud.registration.entity.request.*;
import com.wondersgroup.healthcloud.registration.entity.response.*;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.services.appointment.dto.ScheduleDto;
import com.wondersgroup.healthcloud.services.appointment.exception.ErrorReservationException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneContactException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneScheduleException;
import com.wondersgroup.healthcloud.services.appointment.exception.NoneSchedulePayModeException;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.utils.registration.SignatureGenerator;
import com.wondersgroup.healthcloud.utils.sms.SMS;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by longshasha on 16/12/5.
 *
 * 用于api 客户端接口
 */
@Service
@Transactional(readOnly = true)
public class AppointmentApiServiceImpl implements AppointmentApiService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AppointmentApiServiceImpl.class);

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
    private SmsTempletRepository smsTempletRepository;

    @Autowired
    private Environment environment;

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private OrderClient orderClient;//调用第三方webservice接口

    @Autowired
    private DoctInfoClient doctInfoClient;

    @Autowired
    private AppConfigService appConfigService;

    @Resource(name="default")
    private SMS sms;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    private HttpRequestExecutorManager httpRequestExecutorManager;



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
    public List<AppointmentDoctor> findDoctorListByKw(String kw, int pageSize, int pageNum,Boolean hasDepartRegistration,
                                                      String departmentL2Id) {
        String sql = "select a.* , h.hos_name as 'hospitalName',h.hospital_rule as 'reservationRule', d.dept_name as 'departmentName'  " +
                " from app_tb_appointment_doctor a  " +
                " left join app_tb_appointment_hospital h on a.hospital_id = h.id " +
                " left join app_tb_appointment_department_l1 b on a.department_l1_id = b.id " +
                " left join app_tb_appointment_department_l2 d on a.department_l2_id = d.id " ;

        String commonWhereSql = " where a.del_flag = '0' and h.del_flag = '0' and h.isonsale='1' and  b.del_flag = '0' and d.del_flag ='0' ";
        if(StringUtils.isNotBlank(departmentL2Id)){
            sql += " INNER JOIN (" +
                    "select doctor_id,sum(reserve_order_num) cc from app_tb_appointment_doctor_schedule" +
                    " where (reserve_order_num>0 or ordered_num>0)  and doctor_id is not null and `status`= '1' and del_flag = '0' " +
                    " and start_time > '%s' group by doctor_id " +
                    ") s on a.id=s.doctor_id " + commonWhereSql +
                    " and a.department_l2_id = '"+departmentL2Id+"'";
            sql = String.format(sql,DateFormatter.dateTimeFormat(new Date()));
        }
        if(StringUtils.isNotBlank(kw)){
            sql += commonWhereSql + " and a.doct_name like '%"+kw+"%' ";
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
                " AND a.doctor_id = '%s' and a.start_time > '%s'";

        sql = String.format(sql, doctorId,DateFormatter.dateTimeFormat(new Date()));
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
                " AND a.department_l2_id = '%s' and a.start_time > '%s' " ;

        sql = String.format(sql, department_l2_id,DateFormatter.dateTimeFormat(new Date()));
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
                " and s.schedule_date = '%s' and s.start_time > '%s' " +
                " and (reserve_order_num>0 or ordered_num>0)  order by s.start_time asc ";

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
                " d.dept_name as departmentName,e.hos_name as hospitalName,e.hos_org_code," +
                " b.start_time as scheduleStartTime,b.end_time as scheduleEndTime,b.`status` as scheduleStatus,b.register_type,b.register_name," +
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
    @Transactional(readOnly = false)
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

        if("1".equals(orderType) && doctor!=null){
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

        submitOrder.setPlatformUserId(contact.getPlatformUserId());//数据库的全部是主注册人
        submitOrder.setUserBD(IdcardUtils.getBirthStrByIdCard(contact.getIdcard()));
        submitOrder.setUserCardId(contact.getIdcard());
        submitOrder.setUserCardType("1");


        submitOrder.setUserSex(IdcardUtils.getGenderByIdCard(contact.getIdcard()));

        if(StringUtils.isNotBlank(contact.getMediCardId())){
            submitOrder.setMediCardId(contact.getMediCardId());
            submitOrder.setMediCardType("1");//1：社保卡（医保卡
        }

        submitOrderRequest.submitOrder = submitOrder;
        submitOrderRequest.requestMessageHeader = new RequestMessageHeader(environment);
        submitOrderRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(submitOrderRequest));

        String xmlRequest = JaxbUtil.convertToXml(submitOrderRequest);
        OrderResultResponse orderResultResponse = new OrderResultResponse();
        try {
            orderResultResponse = orderClient.submitOrderByUserInfo(xmlRequest);
        }catch (Exception e){
            logger.error("预约提交失败=="+e.getLocalizedMessage());
            logger.error("参数=="+xmlRequest);
            throw new ErrorReservationException("医联预约平台接口调用失败");
        }

        if(!"0".equals(orderResultResponse.messageHeader.getCode())){
            try {
                //调用jobClient的接口
                Request req = new RequestBuilder().get().url(jobClientUrl + "/api/jobclient/appointment/updateDepartmentNumSource").param("department_id", l2Department.getId()).build();
                JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(req).run().as(JsonNodeResponseWrapper.class);
            }catch (Exception e){
                logger.error("预约失败后更新排班信息,department_id="+l2Department.getId()+","+e.getLocalizedMessage());
            }
            throw new ErrorReservationException(orderResultResponse.messageHeader.getDesc());
        }

        //刷新排班资源
        schedule.setOrderedNum(schedule.getOrderedNum()+1);
        schedule.setReserveOrderNum(schedule.getReserveOrderNum()-1);
        scheduleRepository.saveAndFlush(schedule);

        if("1".equals(orderType) && doctor!=null){
            int reservationNum = doctor.getReservationNum()==null?0:doctor.getReservationNum();
            doctor.setReservationNum(reservationNum+1);
            doctorRepository.saveAndFlush(doctor);
        }else{
            int reservationNum = l2Department.getReservationNum()==null?0:l2Department.getReservationNum();
            l2Department.setReservationNum(reservationNum+1);
            departmentL2Repository.saveAndFlush(l2Department);
        }

        OrderDto orderDto = saveOrderToLocal(submitOrder,orderResultResponse,contact,doctor,schedule,l2Department);

        try {
            sendAppointmentMessage(contact,orderDto,"1");
        }catch (Exception e){
            logger.error("预约发送短信失败,orderId="+orderDto.getId()+e.getMessage());
        }



        return orderDto;
    }

    /**
     * 给用户发送短信
     * @param contact
     * @param orderDto
     * @param type 类型 1-预约，2-取消，3-停诊
     */
    private void sendAppointmentMessage(AppointmentContact contact, OrderDto orderDto, String type) {
        AppointmentSmsTemplet smsTemplet = smsTempletRepository.findOne(orderDto.getHosOrgCode());
        String RealName = contact.getName();
        String ReservationId = orderDto.getHosNumSourceId();
        String MedicineCardNo ="";
        String Date = DateFormatter.scheduleDateFormat(orderDto.getScheduleDate());

        String Time = "";
        if(orderDto.getStartTime()!=null&&orderDto.getEndTime()!=null){
            Time = DateFormatter.hourDateFormat(orderDto.getStartTime())+"-"+DateFormatter.hourDateFormat(orderDto.getEndTime());
        }else{
            Time = DateFormatter.hourDateFormat(orderDto.getScheduleStartTime())+"-"+DateFormatter.hourDateFormat(orderDto.getScheduleEndTime());
        }

        String HospitalName = smsTemplet.getHospitalName();
        String OfficeName = orderDto.getDepartmentName();
        String DoctorName = "";
        String RegisterFee = orderDto.getVisitCost();
        String queueNo = orderDto.getVisitNo();

        try {
            String vistCost = orderDto.getVisitCost().replace("元","");
            RegisterFee = String.valueOf(new BigDecimal(vistCost).stripTrailingZeros().toEngineeringString()) + "元";
        }catch (Exception e){

        }
        String RegisterLocation = orderDto.getLocation();//他们说这个字段只有华山医院的用

        String DiseaseName = "";
        String CommonName = "";

        if(smsTemplet!=null){
            String content = "";
            //预约
            if("1".equals(type)){
                //1专家.2专病3.普通
                if("1".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getOrderDoctor();
                    DoctorName = orderDto.getDoctorName();
                }else if("2".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getOrderDisease();
                    DiseaseName = orderDto.getRegisterName();
                }else if("3".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getOrderCommon();
                    CommonName = orderDto.getRegisterName();
                }
                content += "如需取消:"+smsTemplet.getCancelDesc();
            //取消预约
            }else if("2".equals(type)){
                if("1".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getCancelDoctor();
                    DoctorName = orderDto.getDoctorName();
                }else if("2".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getCancelDisease();
                    DiseaseName = orderDto.getRegisterName();
                }else if("3".equals(orderDto.getRegisterType())){
                    content = smsTemplet.getCancelCommon();
                    CommonName = orderDto.getRegisterName();
                }
            //停诊
            }else if("3".equals(type)){
                if("1".equals(orderDto.getRegisterType())){
                    content = "【医联预约平台】{RealName}，您预约的{Date}{Time}{HospitalName}{OfficeName}{DoctorName}的门诊已停诊,系统已为您取消。【上海健康云】";
                    DoctorName = orderDto.getDoctorName();
                }else if("2".equals(orderDto.getRegisterType())){
                    content = "【医联预约平台】{RealName}，您预约的{Date}{Time}{HospitalName}{OfficeName}{DiseaseName}的门诊已停诊,系统已为您取消。【上海健康云】";
                    DiseaseName = orderDto.getRegisterName();
                }else if("3".equals(orderDto.getRegisterType())){
                    content = "【医联预约平台】{RealName}，您预约的{Date}{Time}{HospitalName}{CommonName}的普通门诊已停诊,系统已为您取消。【上海健康云】";
                    CommonName = orderDto.getRegisterName();
                }

            }

            content = content.replaceAll("\\{RealName\\}",RealName==null?"":RealName);
            content = content.replaceAll("\\{ReservationId\\}",ReservationId==null?"":ReservationId);
            content = content.replaceAll("\\{MedicineCardNo\\}",MedicineCardNo==null?"":MedicineCardNo);
            content = content.replaceAll("\\{Date\\}",Date==null?"":Date);
            content = content.replaceAll("\\{Time\\}",Time==null?"":Time);
            content = content.replaceAll("\\{HospitalName\\}",HospitalName==null?"":HospitalName);
            content = content.replaceAll("\\{OfficeName\\}",OfficeName==null?"":OfficeName);
            content = content.replaceAll("\\{DoctorName\\}",DoctorName==null?"":DoctorName);
            content = content.replaceAll("\\{RegisterFee\\}",RegisterFee==null?"":RegisterFee);
            content = content.replaceAll("\\{RegisterLocation\\}",RegisterLocation==null?"":RegisterLocation);

            content = content.replaceAll("\\{DiseaseName\\}",DiseaseName==null?"":DiseaseName);
            content = content.replaceAll("\\{CommonName\\}",CommonName==null?"":CommonName);

            content = content.replaceAll("\\{queueNo\\}",queueNo==null?"":queueNo);
            sms.send(contact.getMobile(), content);
        }

    }

    /**
     * 取消订单
     * @param id
     */
    @Override
    @Transactional(readOnly = false)
    public OrderDto cancelReservationOrderById(String id) {
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

        OrderCancelResponse orderCancelResponse = new OrderCancelResponse();
        try {
            orderCancelResponse = orderClient.orderCancel(JaxbUtil.convertToXml(orderCancelRequest));
        }catch (Exception e){
            logger.error("医疗预约平台取消订单,orderId="+id+","+e.getLocalizedMessage());
            throw new ErrorReservationException("医联预约平台接口调用失败");
        }

        if(!"0".equals(orderCancelResponse.messageHeader.getCode())){
            throw new ErrorReservationException(orderCancelResponse.messageHeader.getDesc());
        }
        //修改order的状态为 取消
        order.setOrderStatus("3");
        order.setUpdateDate(new Date());
        order.setCancelTime(new Date());
        orderRepository.saveAndFlush(order);

        OrderDto orderDto = findOrderByUidOrId(id,null,null,false).get(0);
        AppointmentContact contact = contactRepository.findOne(orderDto.getContactId());

        try {
            sendAppointmentMessage(contact,orderDto,"2");
        }catch (Exception e){
            logger.error("取消发送短信失败,orderId="+orderDto.getId()+e.getLocalizedMessage());
        }
        return orderDto;
    }

    /**
     * 查询预约挂号开关是否开启
     * @return
     */
    @Override
    public Boolean getRegistrationIsOn(String mainArea) {
        Boolean isOn = false;
        AppConfig registrationConfig = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "app.common.registration");
        if(registrationConfig != null){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode content = objectMapper.readTree(registrationConfig.getData());
                String isOnStr = content.get("isOn") == null ? "" : content.get("isOn").asText();
                if(StringUtils.isNotBlank(isOnStr) && "1".equals(isOnStr)){
                    isOn = true;
                }
            }catch (Exception ex){
                logger.error("CommonController.appConfig Error -->" + ex.getLocalizedMessage());
            }

        }
        return isOn;
    }

    @Override
    public AppointmentDoctorSchedule findScheduleById(String scheduleId) {
        return scheduleRepository.findOne(scheduleId);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdateAppointmentScheduleByScheduleId(String scheduleId) {
        AppointmentDoctorSchedule schedule = findScheduleById(scheduleId);
        if(schedule!=null){
            AppointmentHospital hospital = findHospitalById(schedule.getHospitalId());
            NumSourceInfo numSourceInfo = new NumSourceInfo();
            numSourceInfo.setHosOrgCode(hospital.getHosOrgCode());
            numSourceInfo.setScheduleId(schedule.getScheduleId());
            List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(numSourceInfo);
            if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0) {
                for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                    //根据  NumSourceId
                    AppointmentDoctorSchedule scheduleUpdate = scheduleRepository.getAppointmentDoctorSchedule(schedule.getScheduleId(), segmentNumberInfo.getNumSourceId(), schedule.getHospitalId());
                    try {
                        if(scheduleUpdate!=null){
                            scheduleUpdate.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                            scheduleUpdate.setUpdateDate(new Date());
                            scheduleRepository.saveAndFlush(scheduleUpdate);
                        }
                    }catch (Exception e){
                        logger.error("saveOrUpdateAppointmentSchedule="+e.getLocalizedMessage());
                    }

                }
            }

        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdateAppointmentScheduleByDoctorId(String id,String type) {
        Map<String,Object> result = getNumberSourceParameter(id,type);
        if(result!=null){
            String hospitalId = result.get("hospitalId")==null?"":result.get("hospitalId").toString();
            List<NumSourceInfo> docNumSourceList = getDoctorNumSourceByMap(result, type);
            //保存医生预约资源信息
            List<FutureTask<Integer[]>> futureTasks = Lists.newArrayList();
            if(docNumSourceList!=null && docNumSourceList.size()>0){
                for (NumSourceInfo schedule :docNumSourceList){
                    ExecutorService executor = Executors.newFixedThreadPool(1);
                    UpdateNumberSourceTask task = new UpdateNumberSourceTask(schedule,hospitalId);
                    FutureTask<Integer[]> task1 = new FutureTask<Integer[]>(task);
                    executor.submit(task1);
                    futureTasks.add(task1);
                }
            }
        }

    }

    /**
     * 查询科室下所有医生的历史预约数
     * @param department_l2_id
     * @return
     */
    @Override
    public int countAllDoctorReservationNumByDepartmentL2Id(String department_l2_id) {
        Integer result = doctorRepository.countAllDoctorReservationNumByDepartmentL2Id(department_l2_id);
        if(result==null){
            result = 0;
        }
        return result;
    }

    /**
     * 如果有停诊的排班则发送停诊短信和修改订单状态
     * @param orderId
     */
    @Override
    @Transactional(readOnly = false)
    public void closeNumberSourceByOrderId(String orderId) {
        OrderDto orderDto = findOrderByUidOrId(orderId,null,null,false).get(0);
        if(orderDto!=null && "1".equals(orderDto.getOrderStatus())){
            orderRepository.updateOrderWhencloseNumberSource("1",orderId);
            AppointmentContact contact = contactRepository.findOne(orderDto.getContactId());
            sendAppointmentMessage(contact,orderDto,"3");
        }

    }

    @Override
    public List<OrderDto> findOrderListByScheduleId(String scheduleId) {
        String sql = "select a.*,c.doct_name as doctorName,c.doct_tile as dutyName, " +
                " d.dept_name as departmentName,e.hos_name as hospitalName,e.hos_org_code," +
                " b.start_time as scheduleStartTime,b.end_time as scheduleEndTime,b.`status` as scheduleStatus,b.register_type,b.register_name," +
                " b.visit_level_code as visitLevelCode,b.visit_cost as visitCost,b.schedule_date as scheduleDate," +
                " e.close_days as closeDays,e.close_time_hour as closeTimeHour " +
                " from app_tb_appointment_order a " +
                " left join app_tb_appointment_doctor_schedule b on a.appointment_schedule_id = b.id " +
                " left join app_tb_appointment_doctor c on b.doctor_id = c.id" +
                " left join app_tb_appointment_department_l2 d on b.department_l2_id = d.id" +
                " left join app_tb_appointment_hospital e on a.hospital_id = e.id " +
                " where b.schedule_id = '%s' and a.order_status = '1' ";
        sql = String.format(sql,scheduleId);
        return jt.query(sql.toString(), new BeanPropertyRowMapper(OrderDto.class));
    }

    @Override
    @Transactional(readOnly = false)
    public void updateOrderWhencloseNumberSource(String closeSms,String orderId) {
        orderRepository.updateOrderWhencloseNumberSource(closeSms,orderId);
    }

    /**
     * 根据科室更新排班信息
     * @param departmentId
     */
    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdateAppointmentScheduleByDepartmentId(String departmentId) {
        //保存二级科室
        AppointmentL2Department l2Department = departmentL2Repository.findOne(departmentId);
        Map<String,Object> result = getNumberSourceParameter(departmentId,"2");
        if(l2Department!=null && result !=null){
            String hospitalId = l2Department.getHospitalId();
            TwoDeptInfo twoDeptInfo = new TwoDeptInfo();
            twoDeptInfo.setHosOrgCode(result.get("hosOrgCode").toString());
            twoDeptInfo.setTopHosDeptCode(result.get("topHosDeptCode").toString());
            twoDeptInfo.setHosDeptCode(l2Department.getHosDeptCode());

            //查询科室的普通预约资源
            List<NumSourceInfo> deptNumSourceCommonList = getDeptNumSourceByTwoDeptInfo(twoDeptInfo,"3");
            //保存预约资源信息
            if(deptNumSourceCommonList!=null && deptNumSourceCommonList.size()>0){
                for (NumSourceInfo numSourceInfo :deptNumSourceCommonList){
                    //保存科室的预约资源
                    List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(numSourceInfo);
                    if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                        for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                            //根据  NumSourceId
                            AppointmentDoctorSchedule scheduleUpdate = scheduleRepository.getAppointmentDoctorSchedule
                                    (numSourceInfo.getScheduleId(), segmentNumberInfo.getNumSourceId(), hospitalId);
                            try {
                                if(scheduleUpdate!=null){
                                    scheduleUpdate.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                                    scheduleUpdate.setUpdateDate(new Date());
                                    scheduleRepository.saveAndFlush(scheduleUpdate);
                                }
                            }catch (Exception e){
                                logger.error("saveOrUpdateAppointmentSchedule="+e.getLocalizedMessage());
                            }

                        }
                    }
                }
            }

            //查询科室的专病预约资源
            List<NumSourceInfo> deptNumSourceDiseaseList = getDeptNumSourceByTwoDeptInfo(twoDeptInfo,"2");
            //保存预约资源信息
            if(deptNumSourceDiseaseList!=null && deptNumSourceDiseaseList.size()>0){
                for (NumSourceInfo numSourceInfo :deptNumSourceDiseaseList){
                    //保存科室的预约资源
                    List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(numSourceInfo);
                    if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                        for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                            //根据  NumSourceId
                            AppointmentDoctorSchedule scheduleUpdate = scheduleRepository.getAppointmentDoctorSchedule
                                    (numSourceInfo.getScheduleId(), segmentNumberInfo.getNumSourceId(), hospitalId);
                            try {
                                if(scheduleUpdate!=null){
                                    scheduleUpdate.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                                    scheduleUpdate.setUpdateDate(new Date());
                                    scheduleRepository.saveAndFlush(scheduleUpdate);
                                }
                            }catch (Exception e){
                                logger.error("saveOrUpdateAppointmentSchedule="+e.getLocalizedMessage());
                            }
                        }
                    }
                }
            }

            //查询二级科室下所有的医生
            List<DoctInfo> doctInfoList = getDoctorListByTwoDept(twoDeptInfo);
            if(doctInfoList!=null && doctInfoList.size()>0){
                //根据医生查询医生的预约资源
                for(DoctInfo doctInfo : doctInfoList){

                    //保存医生信息
                    AppointmentDoctor doctor = saveOrUpdateAppointmentDoctor(l2Department,doctInfo);
                    if(doctor!=null){
                        List<NumSourceInfo> docNumSourceList = getDoctorNumSourceByDoctInfo(twoDeptInfo,doctInfo);
                        //保存医生预约资源信息
                        if(docNumSourceList!=null && docNumSourceList.size()>0){
                            for (NumSourceInfo numSourceInfo :docNumSourceList){
                                //保存医生的排班
                                List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(numSourceInfo);
                                if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                                    for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                                        AppointmentDoctorSchedule doctorSchedule = saveOrUpdateAppointmentDoctorSchedule(doctor, numSourceInfo, segmentNumberInfo);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        //逻辑删除没有二级科室的一级科室
        departmentL1Repository.deleteDept1HasNoDept2();

        //给医院设置医生数量
        hospitalRepository.setDoctorNumToHospital();

    }

    private AppointmentDoctorSchedule saveOrUpdateAppointmentDoctorSchedule(AppointmentDoctor doctor, NumSourceInfo numSourceInfo,SegmentNumberInfo segmentNumberInfo) {
        AppointmentDoctorSchedule schedule = new AppointmentDoctorSchedule();
        AppointmentDoctorSchedule localSchedule = scheduleRepository.getAppointmentDoctorSchedule
                (numSourceInfo.getScheduleId(),segmentNumberInfo.getNumSourceId(),doctor.getHospitalId());
        try {
            if(localSchedule == null){
                BeanUtils.copyProperties(numSourceInfo, schedule, "scheduleDate");

                schedule.setNumSourceId(segmentNumberInfo.getNumSourceId());
                schedule.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                schedule.setOrderedNum(Integer.valueOf(numSourceInfo.getOrderedNum()));
                schedule.setSumOrderNum(Integer.valueOf(numSourceInfo.getSumOrderNum()));
                schedule.setDoctorId(doctor.getId());
                schedule.setScheduleDate(DateUtils.parseDate(numSourceInfo.getScheduleDate(), "yyyy-MM-dd"));
                schedule.setStartTime(numSourceInfo.getStartTime());
                schedule.setEndTime(numSourceInfo.getEndTime());
                schedule.setStatus(numSourceInfo.getStatus());
                schedule.setHospitalId(doctor.getHospitalId());
                schedule.setL1DepartmentId(doctor.getL1DepartmentId());
                schedule.setL2DepartmentId(doctor.getL2DepartmentId());
                schedule.setDelFlag("0");
                schedule.setCreateDate(new Date());
                schedule.setUpdateDate(new Date());
                schedule.setId(IdGen.uuid());
                scheduleRepository.saveAndFlush(schedule);
            }else{
                BeanUtils.copyProperties(numSourceInfo,localSchedule,"scheduleDate");
                localSchedule.setNumSourceId(segmentNumberInfo.getNumSourceId());
                localSchedule.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                localSchedule.setOrderedNum(Integer.valueOf(numSourceInfo.getOrderedNum()));
                localSchedule.setSumOrderNum(Integer.valueOf(numSourceInfo.getSumOrderNum()));
                localSchedule.setScheduleDate(DateUtils.parseDate(numSourceInfo.getScheduleDate(), "yyyy-MM-dd"));
                localSchedule.setStartTime(segmentNumberInfo.getStartTime());
                localSchedule.setEndTime(segmentNumberInfo.getEndTime());
                localSchedule.setStatus(numSourceInfo.getStatus());
                localSchedule.setDelFlag("0");
                localSchedule.setUpdateDate(new Date());
                scheduleRepository.saveAndFlush(localSchedule);
                schedule = localSchedule;
            }
        }catch (Exception e){

        }
        return schedule;
    }


    /**
     * 保存医生信息
     * @param l2Department
     * @param doctInfo
     * @return
     */
    private AppointmentDoctor saveOrUpdateAppointmentDoctor(AppointmentL2Department l2Department, DoctInfo doctInfo) {
        AppointmentDoctor doctor = new AppointmentDoctor();
        AppointmentDoctor localDoctor = doctorRepository.getAppointmentDoctor
                (doctInfo.getHosDoctCode(),l2Department.getId(),l2Department.getL1DepartmentId(),l2Department.getHospitalId());
        if(localDoctor == null){
            BeanUtils.copyProperties(doctInfo, doctor);
            doctor.setHospitalId(l2Department.getHospitalId());
            doctor.setL1DepartmentId(l2Department.getL1DepartmentId());
            doctor.setL2DepartmentId(l2Department.getId());
            doctor.setDelFlag("0");
            doctor.setCreateDate(new Date());
            doctor.setUpdateDate(new Date());
            doctor.setId(IdGen.uuid());
            doctor.setIsonsale("1");
            doctorRepository.saveAndFlush(doctor);
        }else{
            BeanUtils.copyProperties(doctInfo,localDoctor);
            localDoctor.setDelFlag("0");
            localDoctor.setUpdateDate(new Date());
            doctorRepository.saveAndFlush(localDoctor);
            doctor = localDoctor;
        }
        return doctor;
    }

    /**
     * 根据二级科室查询科室的预约资源
     * @param twoDeptInfo
     * @return
     */
    private List<NumSourceInfo> getDeptNumSourceByTwoDeptInfo(TwoDeptInfo twoDeptInfo,String registerType) {
        NumSourceInfoRequest numSourceInfoRequest = new NumSourceInfoRequest();
        numSourceInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        NumSourceInfoR numSourceInfoR = new NumSourceInfoR(twoDeptInfo);
        numSourceInfoR.setRegisterType(registerType);
        numSourceInfoRequest.numSourceInfoR = numSourceInfoR;

        String sign = SignatureGenerator.generateSignature(numSourceInfoRequest);
        numSourceInfoRequest.requestMessageHeader.setSign(sign);

        String xmlRequest = JaxbUtil.convertToXml(numSourceInfoRequest);
        List<NumSourceInfo> numSourceInfoList = Lists.newArrayList();
        try{
            NumSourceInfoResponse numSourceInfoResponse = orderClient.getOrderNumInfoList(xmlRequest);
            if(numSourceInfoResponse!=null&&"0".equals(numSourceInfoResponse.messageHeader.getCode())){
                numSourceInfoList = numSourceInfoResponse.numSourceInfos;
            }
        }catch (Exception e){
            logger.error("getDeptNumSourceByTwoDeptInfo:" + e.getLocalizedMessage());
        }

        return numSourceInfoList;
    }

    private List<DoctInfo> getDoctorListByTwoDept(TwoDeptInfo twoDeptInfo) {

        DoctInfoRequest doctInfoRequest = new DoctInfoRequest();
        doctInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        doctInfoRequest.deptInfoR = new DeptInfoR(twoDeptInfo.getHosOrgCode(),twoDeptInfo.getTopHosDeptCode(),twoDeptInfo.getHosDeptCode());

        String sign = SignatureGenerator.generateSignature(doctInfoRequest);
        doctInfoRequest.requestMessageHeader.setSign(sign);

        String xmlRequest = JaxbUtil.convertToXml(doctInfoRequest);
        List<DoctInfo> doctInfoList = Lists.newArrayList();

        try{
            DoctInfoResponse doctInfoResponse = doctInfoClient.getDoctInfoList(xmlRequest);
            if(doctInfoResponse!=null&&"0".equals(doctInfoResponse.messageHeader.getCode())){
                doctInfoList = doctInfoResponse.doctInfos;
            }else{
                logger.error("getDoctorListByTwoDept:"+doctInfoResponse.messageHeader.getDesc());
            }
        }catch (Exception e){
            logger.error("getDoctorListByTwoDept:"+e.getLocalizedMessage());
            logger.error("xmlRequest="+xmlRequest);
        }
        return doctInfoList;
    }

    /**
     * 根据医生信息查询医生的预约资源
     * @param doctInfo
     * @return
     */
    private List<NumSourceInfo> getDoctorNumSourceByDoctInfo(TwoDeptInfo twoDeptInfo,DoctInfo doctInfo) {
        NumSourceInfoRequest numSourceInfoRequest = new NumSourceInfoRequest();
        numSourceInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        numSourceInfoRequest.numSourceInfoR = new NumSourceInfoR(twoDeptInfo,doctInfo);

        String sign = SignatureGenerator.generateSignature(numSourceInfoRequest);
        numSourceInfoRequest.requestMessageHeader.setSign(sign);

        String xmlRequest = JaxbUtil.convertToXml(numSourceInfoRequest);
        List<NumSourceInfo> numSourceInfoList = Lists.newArrayList();
        try{
            NumSourceInfoResponse numSourceInfoResponse = orderClient.getOrderNumInfoList(xmlRequest);
            if(numSourceInfoResponse!=null&&"0".equals(numSourceInfoResponse.messageHeader.getCode())){
                numSourceInfoList = numSourceInfoResponse.numSourceInfos;
            }
        }catch (Exception e){
            logger.error("getDoctorNumSourceByDoctInfo:"+e.getLocalizedMessage());
            logger.error("xmlRequest="+xmlRequest);
        }

        return numSourceInfoList;
    }

    public class UpdateNumberSourceTask implements Callable {
        private NumSourceInfo numSourceInfo;
        private String hospitalId;
        @Override
        public Object call() throws Exception{
            List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(numSourceInfo);
            if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                    //根据  NumSourceId
                    AppointmentDoctorSchedule scheduleUpdate = scheduleRepository.getAppointmentDoctorSchedule
                            (numSourceInfo.getScheduleId(), segmentNumberInfo.getNumSourceId(), hospitalId);
                    try {
                        if(scheduleUpdate!=null){
                            scheduleUpdate.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                            scheduleUpdate.setUpdateDate(new Date());
                            scheduleRepository.saveAndFlush(scheduleUpdate);
                        }
                    }catch (Exception e){
                        logger.error("saveOrUpdateAppointmentSchedule="+e.getLocalizedMessage());
                    }
                }
            }
            return null;
        }

        public UpdateNumberSourceTask(NumSourceInfo numSourceInfo,String hospitalId) {
            super();
            this.numSourceInfo = numSourceInfo;
            this.hospitalId = hospitalId;
        }
    }

    private <T> T getResult(FutureTask<T> task) throws InterruptedException{
        while (true) {
            if (task.isDone() && !task.isCancelled()) {
                break;
            }
        }
        try {
            return task.get();
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 查询
     * @param id
     * @param type
     * @return
     */
    private Map<String, Object> getNumberSourceParameter(String id, String type) {
        String sql = "";
        if("1".equals(type)){
            sql += "select a.doct_code as hosDoctCode,b.dept_code as hosDeptCode,c.dept_code as topHosDeptCode," +
                    " d.hos_org_code as  hosOrgCode,d.id as hospitalId  " +
                    " from app_tb_appointment_doctor a " +
                    " left join app_tb_appointment_department_l2 b on a.department_l2_id = b.id " +
                    " left join app_tb_appointment_department_l1 c on a.department_l1_id = c.id " +
                    " left join app_tb_appointment_hospital d on a.hospital_id = d.id " +
                    " where a.id = '%s' ";
        }else{
            sql += "select b.dept_code as hosDeptCode,c.dept_code as topHosDeptCode,d.hos_org_code as hosOrgCode," +
                    " d.id as hospitalId " +
                    " from app_tb_appointment_department_l2 b " +
                    " left join app_tb_appointment_department_l1 c on b.department_l1_id = c.id " +
                    " left join app_tb_appointment_hospital d on b.hospital_id = d.id " +
                    " where b.id = '%s' ";
        }
        sql = String.format(sql,id);

        return jt.queryForMap(sql);
    }

    private List<NumSourceInfo> getDoctorNumSourceByMap(Map<String,Object> map,String type) {
        NumSourceInfoRequest numSourceInfoRequest = new NumSourceInfoRequest();
        numSourceInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        NumSourceInfoR  numSourceInfoR = new NumSourceInfoR();

        String hosOrgCode = map.get("hosOrgCode")==null?"":map.get("hosOrgCode").toString();
        String topHosDeptCode = map.get("topHosDeptCode")==null?"":map.get("topHosDeptCode").toString();
        String hosDeptCode = map.get("hosDeptCode")==null?"":map.get("hosDeptCode").toString();
        String hosDoctCode = map.get("hosDoctCode")==null?"":map.get("hosDoctCode").toString();

        List<NumSourceInfo> numSourceInfoList = Lists.newArrayList();
        if(StringUtils.isNotBlank(hosDeptCode)&&StringUtils.isNotBlank(topHosDeptCode)&&StringUtils.isNotBlank(hosDeptCode)){
            numSourceInfoR.setHosOrgCode(hosOrgCode);
            numSourceInfoR.setTopHosDeptCode(topHosDeptCode);
            numSourceInfoR.setHosDeptCode(hosDeptCode);
            numSourceInfoR.setHosDoctCode(hosDoctCode);
            numSourceInfoR.setStartTime(DateFormatter.dateFormat(new Date()));
            numSourceInfoR.setEndTime(DateFormatter.dateFormat(com.wondersgroup.healthcloud.common.utils.DateUtils.addDay(new Date(), 15)));
            if("2".equals(type)){
                numSourceInfoR.setRegisterType("3");
            }
            numSourceInfoRequest.numSourceInfoR = numSourceInfoR;

            String sign = SignatureGenerator.generateSignature(numSourceInfoRequest);
            numSourceInfoRequest.requestMessageHeader.setSign(sign);

            String xmlRequest = JaxbUtil.convertToXml(numSourceInfoRequest);

            NumSourceInfoResponse numSourceInfoResponse = orderClient.getOrderNumInfoList(xmlRequest);

            if(numSourceInfoResponse!=null&&"0".equals(numSourceInfoResponse.messageHeader.getCode())){
                numSourceInfoList = numSourceInfoResponse.numSourceInfos;
            }
        }

        return numSourceInfoList;
    }

    private List<SegmentNumberInfo> getSegmentNumberInfoBySchedule(NumSourceInfo schedule) {

        SegmentNumberInfoRequest segmentNumberInfoRequest = new SegmentNumberInfoRequest();
        SegmentNumberInfoR segmentNumberInfoR = new SegmentNumberInfoR();
        segmentNumberInfoR.setHosOrgCode(schedule.getHosOrgCode());
        segmentNumberInfoR.setScheduleId(schedule.getScheduleId());
        segmentNumberInfoRequest.segmentNumberInfoR = segmentNumberInfoR;
        segmentNumberInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        segmentNumberInfoRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(segmentNumberInfoRequest));

        String xmlRequest = JaxbUtil.convertToXml(segmentNumberInfoRequest);
        List<SegmentNumberInfo> segmentNumberInfoList = Lists.newArrayList();
        SegmentNumberInfoResponse segmentNumberInfoResponse = orderClient.getOrderSegmentNumberInfoList(xmlRequest);
        if(segmentNumberInfoResponse!=null&&"0".equals(segmentNumberInfoResponse.messageHeader.getCode())){
            segmentNumberInfoList = segmentNumberInfoResponse.lists;
        }
        return segmentNumberInfoList;
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
        String hosNumSourceId = orderResultResponse.orderResult.getHosNumSourceId();
        String location = orderResultResponse.orderResult.getDoctorDesc();//楼层位置

        AppointmentOrder order = new AppointmentOrder();
        order.setId(IdGen.uuid());
        order.setUid(contact.getUid());
        order.setPlatformUserId(submitOrder.getPlatformUserId());
        order.setOrderId(orderId);
        order.setHosNumSourceId(hosNumSourceId);
        order.setVisitNo(visitNo);
        order.setLocation(location);
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
            order.setOrderType("1");//1-医生
        }else{
            order.setOrderType("2");//2-科室
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


        //查询订单信息
        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
        try {
            OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
            OrderDetailR orderDetailR = new OrderDetailR();
            orderDetailR.setOrderId(orderId);
            orderDetailRequest.orderDetailR = orderDetailR;
            orderDetailRequest.requestMessageHeader = new RequestMessageHeader(environment);
            orderDetailRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(orderDetailRequest));
            String xmlRequest = JaxbUtil.convertToXml(orderDetailRequest);
            orderDetailResponse = orderClient.getOrderDetail(xmlRequest);

            if("0".equals(orderDetailResponse.messageHeader.getCode())){
                order.setStartTime(orderDetailResponse.orderDetail.getStartTime());
                order.setEndTime(orderDetailResponse.orderDetail.getEndTime());
            }else{
                logger.error(xmlRequest);
                logger.error("获取订单详情失败-orderId="+orderId+orderDetailResponse.messageHeader.getDesc());
            }
        }catch (Exception e ){
            logger.error("获取订单详情失败-orderId="+orderId+e.getLocalizedMessage());
        }


        orderRepository.saveAndFlush(order);

        return findOrderByUidOrId(order.getId(),null,null,false).get(0);

    }

    @Autowired
    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }


}
