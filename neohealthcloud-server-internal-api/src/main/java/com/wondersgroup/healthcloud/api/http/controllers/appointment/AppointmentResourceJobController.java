package com.wondersgroup.healthcloud.api.http.controllers.appointment;

import com.google.common.collect.Lists;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.appointment.*;
import com.wondersgroup.healthcloud.registration.client.*;
import com.wondersgroup.healthcloud.registration.entity.request.*;
import com.wondersgroup.healthcloud.registration.entity.response.*;
import com.wondersgroup.healthcloud.services.appointment.AppointmentApiService;
import com.wondersgroup.healthcloud.services.appointment.AppointmentService;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import com.wondersgroup.healthcloud.utils.registration.JaxbUtil;
import com.wondersgroup.healthcloud.utils.registration.SignatureGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by longshasha on 16/12/2.
 *
 * 缓存预约挂号数据
 *
 */
@RestController
@RequestMapping(value = "/api/reservation/job")
public class AppointmentResourceJobController {

    private static final Logger log = Logger.getLogger(AppointmentResourceJobController.class);
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String ONSALE_FLAG_ON = "1";
    public static final String ONSALE_FLAG_OFF = "0";


    @Autowired
    private HospitalInfoClient hospitalInfoClient;

    @Autowired
    private TopDeptInfoTopClient topDeptInfoTopClient;

    @Autowired
    private DeptInfoTwoClient deptInfoTwoClient;

    @Autowired
    private DoctInfoClient doctInfoClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Environment environment;

    @Autowired
    private DictCache dictCache;

    @Autowired
    private AppointmentApiService appointmentApiService;

    @Value("${JOB_CONNECTION_URL}")
    private String jobClientUrl;

    private HttpRequestExecutorManager httpRequestExecutorManager;



    private ExecutorService executor = Executors.newFixedThreadPool(15);

    /**
     * 根据scheduleId更新排班信息
     * (本来想要JobClient调用的，邱俊说健康云的jobClient不靠谱,所以这个方法现在不用)
     * @return
     */
    @RequestMapping(value = "/updateNumSource", method = RequestMethod.GET)
    public JsonResponseEntity updateNumSource(@RequestParam(required = true,value = "schedule_id") String scheduleId) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        appointmentApiService.saveOrUpdateAppointmentScheduleByScheduleId(scheduleId);
        return  responseEntity;
    }

    /**
     * 根据医生Id(或科室Id)更新排班信息
     * (本来想要JobClient调用的，邱俊说健康云的jobClient不靠谱,所以这个方法现在不用)
     * @return
     */
    @RequestMapping(value = "/updateDoctorNumSource", method = RequestMethod.GET)
    public JsonResponseEntity updateDoctorNumSource(@RequestParam(required = true,value = "id") String id,
                                                    @RequestParam(required = true,value = "type") String type) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        appointmentApiService.saveOrUpdateAppointmentScheduleByDoctorId(id,type);
        return  responseEntity;
    }

    @RequestMapping(value = "/updateDepartmentNumSource", method = RequestMethod.GET)
    public JsonResponseEntity updateDepartmentNumSource(@RequestParam(required = true,value = "department_id") String id) {
        log.info("------------------updateDepartmentNumSource  start  -------------------");
        JsonResponseEntity responseEntity = new JsonResponseEntity();
        appointmentApiService.saveOrUpdateAppointmentScheduleByDepartmentId(id);
        responseEntity.setMsg("success");
        log.info("------------------updateDepartmentNumSource  end  -------------------");
        return  responseEntity;
    }


    @RequestMapping(value = "/updateHospitalNumSource", method = RequestMethod.GET)
    public JsonResponseEntity updateHospitalNumSource(@RequestParam(required = true,value = "hospital_id") String hospitalId) {
        JsonResponseEntity responseEntity = new JsonResponseEntity();

        log.info("------------------updateHospitalNumSource  start  -------------------");
        Date nowDate  = DateUtils.addMinutes(new Date(), -10);
        log.info("job-start"+ DateFormatter.dateTimeFormat(nowDate));

        AppointmentHospital hospital = appointmentApiService.findHospitalById(hospitalId);
        List<HosInfo> hosInfoList = getAllHosInfo(hospital.getHosOrgCode());
//        List<FutureTask<Integer[]>> futureTasks = Lists.newArrayList();
        if(hosInfoList!=null && hosInfoList.size()>0){
            for(HosInfo hosInfo:hosInfoList){
                AppointmentTask appointmentTask = new AppointmentTask(hosInfo);
            }

        }
        //逻辑删除没有二级科室的一级科室
        appointmentService.deleteDept1HasNoDept2();
        //给医院设置医生数量
        appointmentService.setDoctorNumToHospital();
        log.info("------------------updateHospitalNumSource  end  -------------------");
        return  responseEntity;
    }



    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    public JsonResponseEntity updateAppointmentSource() {

        JsonResponseEntity responseEntity = new JsonResponseEntity();
        log.info("------------------HospitalAppointmentJob  start  -------------------");
        Date nowDate  = DateUtils.addMinutes(new Date(), -10);
        log.info("job-start"+ DateFormatter.dateTimeFormat(nowDate));
        List<HosInfo> hosInfoList = getAllHosInfo(null);
        List<FutureTask<Integer[]>> futureTasks = Lists.newArrayList();
        if(hosInfoList!=null && hosInfoList.size()>0){
            for(HosInfo hosInfo:hosInfoList){
                AppointmentTask appointmentTask = new AppointmentTask(hosInfo);
                FutureTask<Integer[]> task1 = new FutureTask<Integer[]>(appointmentTask);
                executor.submit(task1);
                futureTasks.add(task1);
            }

        }
        for (int i = 0;i<futureTasks.size();i++){
            try {
                getResult(futureTasks.get(i));
            }catch (Exception e){
                log.info("futureTasks--"+e.getLocalizedMessage());
            }

        }

        log.info("job-delete-start"+DateFormatter.dateTimeFormat(new Date()));
        logicalDeletePastRecords(nowDate);
        log.info("------------------HospitalAppointmentJob  end  -------------------");

        return  responseEntity;
    }

    /**
     * 逻辑删除没有更新的资源信息
     * @param nowDate
     */
    private void logicalDeletePastRecords(Date nowDate) {
        //逻辑删除没有更新的医院
        appointmentService.deleteAppointmentHospitalByNowDate(nowDate);
        //逻辑删除没有更新的一级科室
        appointmentService.deleteDepartmentL1ByNowDate(nowDate);
        //逻辑删除没有更新的二级科室
        appointmentService.deleteDepartmentL2ByNowDate(nowDate);
        //逻辑删除没有更新的医生信息
        appointmentService.deleteAppointmentDoctorByNowDate(nowDate);

        //逻辑删除没有更新的预约资源信息
        appointmentService.deleteSchedule(nowDate);

        //逻辑删除没有二级科室的一级科室
        appointmentService.deleteDept1HasNoDept2();

        //给医院设置医生数量
        appointmentService.setDoctorNumToHospital();



    }


    public class AppointmentTask implements Callable {
        private HosInfo hosInfo;

        @Override
        public Object call() throws Exception{

            //保存医院信息
            AppointmentHospital hospital = saveOrUpdateHospital(hosInfo);

            //根据医院code查询医院下所有的一级科室
            List<TopDeptInfo> topDeptInfoList = getTopDeptListByHosInfo(hosInfo);

            if(topDeptInfoList!=null && topDeptInfoList.size()>0){
                for (TopDeptInfo topDeptInfo : topDeptInfoList){

                    //保存一级科室
                    AppointmentL1Department l1Department = saveOrUpdateAppointmentDepartmentL1(hospital,topDeptInfo);

                    //根据一级科室查询二级科室列表
                    List<TwoDeptInfo> twoDeptInfoList = getTwoDeptInfoListByTopDept(hosInfo.getHosOrgCode(),topDeptInfo);
                    if(twoDeptInfoList!=null && twoDeptInfoList.size()>0){
                        for(TwoDeptInfo twoDeptInfo : twoDeptInfoList){

                            // todo 这个判断条件暂时放这里
                            if(!IdcardUtils.containsChinese(twoDeptInfo.getHosOrgCode())){

                                //保存二级科室
                                AppointmentL2Department l2Department = saveOrUpdateAppointmentDepartmentL2(l1Department,twoDeptInfo);

                                //查询科室的普通预约资源
                                List<NumSourceInfo> deptNumSourceCommonList = getDeptNumSourceByTwoDeptInfo(twoDeptInfo,"3");
                                //保存预约资源信息
                                if(deptNumSourceCommonList!=null && deptNumSourceCommonList.size()>0){
                                    for (NumSourceInfo schedule :deptNumSourceCommonList){
                                        //保存科室的预约资源
                                        List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(schedule);
                                        if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                                            if("2".equals(schedule.getStatus())){//停诊
                                                //如果是停诊的看是否有状态为1的订单,修改为停诊 并触发发短信的job
                                                triggerJobClient(schedule.getScheduleId());
                                            }
                                            for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                                                AppointmentDoctorSchedule doctorSchedule = saveOrUpdateAppointmentDeptSchedule(l2Department, schedule, segmentNumberInfo);
                                            }
                                        }
                                    }
                                }

                                //查询科室的专病预约资源
                                List<NumSourceInfo> deptNumSourceDiseaseList = getDeptNumSourceByTwoDeptInfo(twoDeptInfo,"2");
                                //保存预约资源信息
                                if(deptNumSourceDiseaseList!=null && deptNumSourceDiseaseList.size()>0){
                                    for (NumSourceInfo schedule :deptNumSourceDiseaseList){
                                        //保存科室的预约资源
                                        List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(schedule);
                                        if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                                            if("2".equals(schedule.getStatus())){//停诊
                                                //如果是停诊的看是否有状态为1的订单,修改为停诊 并触发发短信的job
                                                triggerJobClient(schedule.getScheduleId());
                                            }
                                            for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                                                AppointmentDoctorSchedule doctorSchedule = saveOrUpdateAppointmentDeptSchedule(l2Department, schedule, segmentNumberInfo);
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

                                        List<NumSourceInfo> docNumSourceList = getDoctorNumSourceByDoctInfo(twoDeptInfo,doctInfo);
                                        //保存医生预约资源信息
                                        if(docNumSourceList!=null && docNumSourceList.size()>0){
                                            for (NumSourceInfo schedule :docNumSourceList){

                                                //保存医生的排班
                                                List<SegmentNumberInfo> segmentNumberInfoList = getSegmentNumberInfoBySchedule(schedule);
                                                if(segmentNumberInfoList!=null && segmentNumberInfoList.size()>0){
                                                    if("2".equals(schedule.getStatus())){//停诊
                                                        //如果是停诊的看是否有状态为1的订单,修改为停诊 并触发发短信的job
                                                        triggerJobClient(schedule.getScheduleId());
                                                    }
                                                    for(SegmentNumberInfo segmentNumberInfo : segmentNumberInfoList){
                                                        AppointmentDoctorSchedule doctorSchedule = saveOrUpdateAppointmentDoctorSchedule(doctor, schedule,segmentNumberInfo);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }


                    }
                }
            }
            return null;
        }

        public AppointmentTask(HosInfo hosInfo) {
            super();
            this.hosInfo = hosInfo;
        }
    }




    private AppointmentDoctorSchedule saveOrUpdateAppointmentDoctorSchedule(AppointmentDoctor doctor, NumSourceInfo numSourceInfo,SegmentNumberInfo segmentNumberInfo) {
        AppointmentDoctorSchedule schedule = new AppointmentDoctorSchedule();
        AppointmentDoctorSchedule localSchedule = appointmentService.getAppointmentDoctorSchedule
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
                schedule.setDelFlag(DEL_FLAG_NORMAL);
                schedule.setCreateDate(new Date());
                schedule.setUpdateDate(new Date());
                schedule.setId(IdGen.uuid());
                appointmentService.saveAndFlush(schedule);
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
                localSchedule.setDelFlag(DEL_FLAG_NORMAL);
                localSchedule.setUpdateDate(new Date());
                appointmentService.saveAndFlush(localSchedule);
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
        AppointmentDoctor localDoctor = appointmentService.getAppointmentDoctor
                (doctInfo.getHosDoctCode(),l2Department.getId(),l2Department.getL1DepartmentId(),l2Department.getHospitalId());
        if(localDoctor == null){
            BeanUtils.copyProperties(doctInfo,doctor);
            doctor.setHospitalId(l2Department.getHospitalId());
            doctor.setL1DepartmentId(l2Department.getL1DepartmentId());
            doctor.setL2DepartmentId(l2Department.getId());
            doctor.setDelFlag(DEL_FLAG_NORMAL);
            doctor.setCreateDate(new Date());
            doctor.setUpdateDate(new Date());
            doctor.setId(IdGen.uuid());
            doctor.setIsonsale(ONSALE_FLAG_ON);
            appointmentService.saveAndFlush(doctor);
        }else{
            BeanUtils.copyProperties(doctInfo,localDoctor);
            localDoctor.setDelFlag(DEL_FLAG_NORMAL);
            localDoctor.setUpdateDate(new Date());
            appointmentService.saveAndFlush(localDoctor);
            doctor = localDoctor;
        }
        return doctor;
    }

    /**
     * 保存预约资源
     * @param l2Department
     * @param numSourceInfo
     * @return
     */
    private AppointmentDoctorSchedule saveOrUpdateAppointmentDeptSchedule(AppointmentL2Department l2Department, NumSourceInfo numSourceInfo,SegmentNumberInfo segmentNumberInfo) {
        AppointmentDoctorSchedule schedule = new AppointmentDoctorSchedule();
        AppointmentDoctorSchedule localSchedule = appointmentService.getAppointmentDoctorSchedule
                (numSourceInfo.getScheduleId(),segmentNumberInfo.getNumSourceId(),l2Department.getHospitalId());

        String registerName  = numSourceInfo.getDoctName();
        if(StringUtils.isBlank(registerName)){
            registerName = numSourceInfo.getDeptName();
        }
        try {
            if(localSchedule == null){
                BeanUtils.copyProperties(numSourceInfo,schedule,"scheduleDate");
                schedule.setNumSourceId(segmentNumberInfo.getNumSourceId());
                schedule.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));

                schedule.setOrderedNum(Integer.valueOf(numSourceInfo.getOrderedNum()));
                schedule.setSumOrderNum(Integer.valueOf(numSourceInfo.getSumOrderNum()));
                schedule.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                schedule.setScheduleDate(DateUtils.parseDate(numSourceInfo.getScheduleDate(), "yyyy-MM-dd"));
                schedule.setStartTime(numSourceInfo.getStartTime());
                schedule.setEndTime(numSourceInfo.getEndTime());
                schedule.setStatus(numSourceInfo.getStatus());
                schedule.setHospitalId(l2Department.getHospitalId());
                schedule.setL1DepartmentId(l2Department.getL1DepartmentId());
                schedule.setL2DepartmentId(l2Department.getId());
                schedule.setRegisterName(registerName);//专病或普通的名称用doctorName传
                schedule.setDelFlag(DEL_FLAG_NORMAL);
                schedule.setCreateDate(new Date());
                schedule.setUpdateDate(new Date());
                schedule.setId(IdGen.uuid());
                appointmentService.saveAndFlush(schedule);
            }else{
                BeanUtils.copyProperties(numSourceInfo,localSchedule,"scheduleDate");
                localSchedule.setOrderedNum(Integer.valueOf(numSourceInfo.getOrderedNum()));
                localSchedule.setSumOrderNum(Integer.valueOf(numSourceInfo.getSumOrderNum()));
                localSchedule.setReserveOrderNum(Integer.valueOf(segmentNumberInfo.getReserveOrderNum()));
                localSchedule.setScheduleDate(DateUtils.parseDate(numSourceInfo.getScheduleDate(), "yyyy-MM-dd"));
                localSchedule.setStartTime(segmentNumberInfo.getStartTime());
                localSchedule.setEndTime(segmentNumberInfo.getEndTime());
                localSchedule.setStatus(numSourceInfo.getStatus());
                localSchedule.setRegisterName(registerName);//专病或普通的名称用doctorName传
                localSchedule.setDelFlag(DEL_FLAG_NORMAL);
                localSchedule.setUpdateDate(new Date());
                appointmentService.saveAndFlush(localSchedule);
                schedule = localSchedule;
            }
        }catch (Exception e){
            log.error("saveOrUpdateAppointmentDeptSchedule="+e.getLocalizedMessage());
        }
        return schedule;
    }

    /**
     * 根据排班信息查询订单为1的设置为停诊(医院的排班ID)
     * @param scheduleId
     */
    private void triggerJobClient(String scheduleId) {
        try {
            List<OrderDto> orderList = appointmentApiService.findOrderListByScheduleId(scheduleId);
            if(orderList.size()>0){
                for(OrderDto orderDto : orderList){
                    appointmentApiService.updateOrderWhencloseNumberSource("0",orderDto.getId());
                    try {
                        //调用jobClient的接口
                        Request req = new RequestBuilder().get().url(jobClientUrl + "/api/jobclient/appointment/closeNumberSource").param("order_id", orderDto.getId()).build();
                        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(req).run().as(JsonNodeResponseWrapper.class);
                    }catch (Exception e){
                        log.error("根据排班信息查询订单为1的设置为停诊,orderId=" + orderDto.getId() + "," + e.getLocalizedMessage());
                    }
                }
            }
        }catch (Exception e){
            log.error("triggerJobClient_"+scheduleId);
        }

    }

    /**
     * 保存二级科室
     * @param l1Department
     * @param twoDeptInfo
     * @return
     */
    private AppointmentL2Department saveOrUpdateAppointmentDepartmentL2(AppointmentL1Department l1Department, TwoDeptInfo twoDeptInfo) {
        AppointmentL2Department l2Department = new AppointmentL2Department();
        AppointmentL2Department locaL2Department = appointmentService.getAppointmentDepartmentL2
                (twoDeptInfo.getHosDeptCode(), l1Department.getHospitalId(), l1Department.getId());

        if(locaL2Department == null){
            BeanUtils.copyProperties(twoDeptInfo,l2Department);
            l2Department.setHospitalId(l1Department.getHospitalId());
            l2Department.setL1DepartmentId(l1Department.getId());
            l2Department.setDelFlag(DEL_FLAG_NORMAL);
            l2Department.setCreateDate(new Date());
            l2Department.setUpdateDate(new Date());
            l2Department.setIsonsale(ONSALE_FLAG_ON);
            l2Department.setId(IdGen.uuid());
            appointmentService.saveAndFlush(l2Department);
        }else{
            BeanUtils.copyProperties(twoDeptInfo,locaL2Department);
            locaL2Department.setDelFlag(DEL_FLAG_NORMAL);
            locaL2Department.setUpdateDate(new Date());
            appointmentService.saveAndFlush(locaL2Department);
            l2Department = locaL2Department;
        }
        return l2Department;
    }

    /**
     * 保存一级科室信息
     * @param hospital
     * @param topDeptInfo
     * @return
     */
    private AppointmentL1Department saveOrUpdateAppointmentDepartmentL1(AppointmentHospital hospital, TopDeptInfo topDeptInfo) {
        AppointmentL1Department l1Department = new AppointmentL1Department();
        AppointmentL1Department localL1Department = appointmentService.getAppointmentDepartmentL1(topDeptInfo.getHosDeptCode(),hospital.getId());
        if(localL1Department == null){
            BeanUtils.copyProperties(topDeptInfo,l1Department);
            l1Department.setHospitalId(hospital.getId());
            l1Department.setDelFlag(DEL_FLAG_NORMAL);
            l1Department.setCreateDate(new Date());
            l1Department.setUpdateDate(new Date());
            l1Department.setId(IdGen.uuid());
            appointmentService.saveAndFlush(l1Department);
        }else{
            BeanUtils.copyProperties(topDeptInfo,localL1Department);
            localL1Department.setDelFlag(DEL_FLAG_NORMAL);
            localL1Department.setUpdateDate(new Date());
            appointmentService.saveAndFlush(localL1Department);
            l1Department = localL1Department;
        }
        return l1Department;
    }

    /**
     * 保存医院信息
     * @param hosInfo
     * @return
     */
    private AppointmentHospital saveOrUpdateHospital(HosInfo hosInfo) {
        AppointmentHospital hospital = new AppointmentHospital();

        AppointmentHospital localHospital = appointmentService.getHospitalByCode(hosInfo.getHosOrgCode());
        if(localHospital == null){
            BeanUtils.copyProperties(hosInfo,hospital);
            hospital.setDelFlag(DEL_FLAG_NORMAL);
            hospital.setIsonsale(ONSALE_FLAG_OFF);
            hospital.setCreateDate(new Date());
            hospital.setUpdateDate(new Date());
            hospital.setId(IdGen.uuid());
            String addressCounty = dictCache.queryHospitalAddressCounty(hospital.getHosOrgCode());
            if(StringUtils.isNotBlank(addressCounty)){
                hospital.setAddressCounty(addressCounty);
            }
            appointmentService.saveAndFlush(hospital);
        }else{
            BeanUtils.copyProperties(hosInfo,localHospital,"hospitalDesc");
            localHospital.setDelFlag(DEL_FLAG_NORMAL);
            localHospital.setUpdateDate(new Date());
            appointmentService.saveAndFlush(localHospital);
            hospital = localHospital;
        }

        return hospital;
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
            log.error("getDeptNumSourceByTwoDeptInfo:"+e.getLocalizedMessage());
        }

        return numSourceInfoList;
    }

    /**
     * 根据排版Id查询可预约时间段
     * @param schedule
     * @return
     */
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
        try{
            SegmentNumberInfoResponse segmentNumberInfoResponse = orderClient.getOrderSegmentNumberInfoList(xmlRequest);
            if(segmentNumberInfoResponse!=null&&"0".equals(segmentNumberInfoResponse.messageHeader.getCode())){
                segmentNumberInfoList = segmentNumberInfoResponse.lists;
            }
        }catch (Exception e){
            log.error("getSegmentNumberInfoBySchedule:"+e.getLocalizedMessage());
            log.error("xmlRequest="+xmlRequest);
        }


        return segmentNumberInfoList;
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
            log.error("getDoctorNumSourceByDoctInfo:"+e.getLocalizedMessage());
            log.error("xmlRequest="+xmlRequest);
        }

        return numSourceInfoList;
    }

    /**
     * 根据二级科室查询所有的医生
     * @param twoDeptInfo
     * @return
     */
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
                log.error("getDoctorListByTwoDept:"+doctInfoResponse.messageHeader.getDesc());
            }
        }catch (Exception e){
            log.error("getDoctorListByTwoDept:"+e.getLocalizedMessage());
            log.error("xmlRequest="+xmlRequest);
        }
        return doctInfoList;
    }

    /**
     * 根据一级科室查询二级科室列表
     * @param topDeptInfo
     * @return
     */
    private List<TwoDeptInfo> getTwoDeptInfoListByTopDept(String hosOrgCode,TopDeptInfo topDeptInfo) {
        DeptInfoTwoRequest deptInfoTwoRequest = new DeptInfoTwoRequest();
        deptInfoTwoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        deptInfoTwoRequest.deptInfoR = new DeptInfoR(hosOrgCode,topDeptInfo.getHosDeptCode());

        String sign = SignatureGenerator.generateSignature(deptInfoTwoRequest);
        deptInfoTwoRequest.requestMessageHeader.setSign(sign);

        List<TwoDeptInfo> twoDeptInfoList = Lists.newArrayList();

        String xmlRequest = JaxbUtil.convertToXml(deptInfoTwoRequest);

        try {
            TwoDeptInfoResponse twoDeptInfoResponse = deptInfoTwoClient.getDeptInfoTwoList(xmlRequest);
            if(twoDeptInfoResponse!=null&&"0".equals(twoDeptInfoResponse.messageHeader.getCode())){
                twoDeptInfoList = twoDeptInfoResponse.twoDeptInfos;
            }else{
                log.error("getTwoDeptInfoListByTopDept:"+twoDeptInfoResponse.messageHeader.getDesc());
            }
        }catch (Exception e){
            log.error("getTwoDeptInfoListByTopDept+"+e.getLocalizedMessage());
            log.error("xmlRequest="+xmlRequest);
        }


        return twoDeptInfoList;
    }

    /**
     * 根据医院code查询对应医院的所有一级科室
     * @param hosInfo
     * @return
     */
    private List<TopDeptInfo> getTopDeptListByHosInfo(HosInfo hosInfo) {
        DeptInfoTopRequest deptInfoTopRequest = new DeptInfoTopRequest();
        deptInfoTopRequest.requestMessageHeader = new RequestMessageHeader(environment);
        deptInfoTopRequest.hosInfoR = new HosInfoR(hosInfo.getHosOrgCode());

        String sign = SignatureGenerator.generateSignature(deptInfoTopRequest);
        deptInfoTopRequest.requestMessageHeader.setSign(sign);

        String xmlRequest = JaxbUtil.convertToXml(deptInfoTopRequest);
        List<TopDeptInfo> topDeptInfoList = Lists.newArrayList();

        try {
            TopDeptInfoResponse topDeptInfoResponse = topDeptInfoTopClient.GetTopDeptInfo(xmlRequest);
            if(topDeptInfoResponse!=null&&"0".equals(topDeptInfoResponse.messageHeader.getCode())){
                topDeptInfoList = topDeptInfoResponse.lists;
            }else{
                log.error("getTopDeptListByHosInfo:code"+topDeptInfoResponse.messageHeader.getCode()+",desc:"+topDeptInfoResponse.messageHeader.getDesc());
            }
        }catch (Exception e){
            log.error("getTopDeptListByHosInfo:"+e.getLocalizedMessage());
            log.error("xmlRequest="+xmlRequest);
        }

        return topDeptInfoList;
    }


    /**
     * 查询所有的医院信息
     * @return
     */
    private List<HosInfo> getAllHosInfo(String hosOrgCode) {
        HosInfoRequest hosInfoRequest = new HosInfoRequest();
        HosInfoR hosInfoR = new HosInfoR();

        if(StringUtils.isNotBlank(hosOrgCode)){
            hosInfoR.setHosOrgCode(hosOrgCode);
        }

        hosInfoRequest.hosInfoR = hosInfoR;
        hosInfoRequest.requestMessageHeader = new RequestMessageHeader(environment);
        hosInfoRequest.requestMessageHeader.setSign(SignatureGenerator.generateSignature(hosInfoRequest));

        String xmlRequest = JaxbUtil.convertToXml(hosInfoRequest);
        List<HosInfo> hosInfoList = Lists.newArrayList();

        HosInfoResponse hosInfoResponse = hospitalInfoClient.getHospitalInfoList(xmlRequest);
        if(hosInfoResponse!=null&&"0".equals(hosInfoResponse.messageHeader.getCode())){
            hosInfoList = hosInfoResponse.hosInfoList;
        }else{
            log.error("getAllHosInfo:"+hosInfoResponse.messageHeader.getDesc());
        }
        return hosInfoList;
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
            log.error("", e);
        }
        return null;
    }



    @Autowired
    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }
}
