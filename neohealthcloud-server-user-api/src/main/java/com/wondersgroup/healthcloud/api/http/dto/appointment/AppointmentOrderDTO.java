package com.wondersgroup.healthcloud.api.http.dto.appointment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.services.appointment.dto.OrderDto;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.IdcardUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Created by longshasha on 16/5/24.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppointmentOrderDTO {
    private static final Logger log = Logger.getLogger(AppointmentOrderDTO.class);

    private String id;//预约单Id

    @JsonProperty("hospital_name")
    private String hospitalName;//医院名称

    @JsonProperty("department_name")
    private String departmentName;//科室名称

    @JsonProperty("doctor_name")
    private String doctorName;//医生名称

    @JsonProperty("duty_name")
    private String dutyName;//医生职称

    @JsonProperty("schedule_date")
    private String scheduleDate;//就诊时间

    /**
     * 周
     */
    private String week;

    /**
     * 时间段
     */
    private String time;

    private String fee;//费用

    /**
     * 门诊类型
     */
    @JsonProperty("visit_level")
    private String visitLevelCode;

    @JsonProperty("type")
    private String orderType;//预约类型 1:医生,2:科室

    /**
     * 预约单Id
     */
    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("patient_name")
    private String patientName;//患者姓名

    @JsonProperty("patient_idcard")
    private String patientIdcard;//患者身份证

    @JsonProperty("patient_mobile")
    private String patientMobile;//患者手机号

    @JsonProperty("medi_card_id")
    private String mediCardId;//社保卡

    @JsonProperty("order_time")
    private String orderTime;//提交订单时间

    /**
     * 预约状态
     * 1:预约成功,2:就诊成功,3:用户取消,4:爽约,5:系统取消
     */
    private String status;

    /**
     * 是否可以取消(已过就诊日期)
     */
    @JsonProperty("can_cancel")
    private Boolean canCancel;//是否可以取消

    /**
     * 取消预约的备注
     * 不迟于2016年11月20日16点50分前取消，逾期取消失败。
     */
    @JsonProperty("cancel_desc")
    private String cancelDesc;

    /**
     * 取消时间
     */
    @JsonProperty("cancel_time")
    private String cancelTime;


    public AppointmentOrderDTO(OrderDto order) {
        if(order!=null){
            this.id = order.getId();
            this.hospitalName = order.getHospitalName();
            this.departmentName = order.getDepartmentName();
            this.doctorName = order.getDoctorName();
            this.dutyName = order.getDutyName();
            this.scheduleDate = DateFormatter.scheduleDateFormat(order.getScheduleDate());
            this.week = DateUtils.getWeekOfDate(order.getScheduleDate());
            this.time = DateFormatter.hourDateFormat(order.getStartTime())+"-"+DateFormatter.hourDateFormat(order.getEndTime());
            if(StringUtils.isBlank(order.getVisitLevelCode())){
                this.visitLevelCode = "其他";
            }else if("1".equals(order.getVisitLevelCode())){
                this.visitLevelCode = "专家门诊";
            }else if("2".equals(order.getVisitLevelCode())){
                this.visitLevelCode = "专病门诊";
            }else if("3".equals(order.getVisitLevelCode())){
                this.visitLevelCode = "普通门诊";
            }
            this.orderType = order.getOrderType();

            this.orderId = order.getHosNumSourceId();//之前数据库取错名字了 应该取 hosNumSourceId
            this.patientName = order.getUserName();//患者姓名

            this.patientIdcard = IdcardUtils.maskIdcard(order.getUserCardId());//患者身份证

            this.patientMobile = IdcardUtils.maskMobile(order.getUserPhone());//患者手机号

            if(StringUtils.isNotBlank(order.getMediCardId())){
                this.mediCardId = IdcardUtils.maskIdcard(order.getMediCardId());//社保卡
            }

            this.orderTime = DateFormatter.dateTimeFormat(order.getCreateDate());

            //todo
            /**
             * 数据订单状态   1：已预约； 2：已支付；3：已退号； 4：已取号； 7：停诊未通知 8：退号中 ; 9：停诊已通知'
             * 本项目订单状态  1:预约成功,2:就诊成功,3:用户取消,4:爽约,5:系统取消
             */
            this.canCancel = false;
            String orderStatus = order.getOrderStatus();
            if("4".equals(orderStatus)){
                this.status = "2";
             //状态8 退号中说是防黄牛做的 号源不会立即释放
            }else if("3".equals(orderStatus) || "8".equals(orderStatus) ){
                this.status = "3";
                if(order.getCancelTime()!=null)
                    this.cancelTime = DateFormatter.dateTimeFormat(order.getCancelTime());
            }else if("7".equals(orderStatus) || "9".equals(orderStatus)){
                this.status = "5";
            }
            if("2".equals(order.getScheduleStatus())){
                this.status = "5";
            }
            if("1".equals(orderStatus) && DateUtils.compareDate(new Date(),order.getScheduleDate())<0){
                this.canCancel = true;
                this.status = "1";
                /**
                 * 取消预约的备注
                 * 不迟于2016年11月20日16点50分前取消，逾期取消失败。
                 */
                try {
                    int closeDays = Integer.valueOf(order.getCloseDays());
                    int closeTimeHour = Integer.valueOf(order.getCloseTimeHour());
                    String cancelDate = DateFormatter.scheduleDateFormat(DateUtils.addDay(order.getScheduleDate(), -closeDays));
                    this.cancelDesc = "不迟于"+cancelDate+closeTimeHour+"点前取消，逾期取消失败。";
                }catch (Exception e){
                    log.error("取消预约的备注数据转换错误:orderId="+this.id+","+e.getLocalizedMessage());
                }
            }else if("1".equals(orderStatus) && DateUtils.compareDate(new Date(),order.getScheduleDate())>0){
                this.status = "1";
                try {
                    int closeDays = Integer.valueOf(order.getCloseDays());
                    int closeTimeHour = Integer.valueOf(order.getCloseTimeHour());
                    String cancelDate = DateFormatter.scheduleDateFormat(DateUtils.addDay(order.getScheduleDate(), -closeDays));
                    this.cancelDesc = "不迟于"+cancelDate+closeTimeHour+"点前取消，逾期取消失败。";
                }catch (Exception e){
                    log.error("取消预约的备注数据转换错误:orderId="+this.id+","+e.getLocalizedMessage());
                }
            }

            this.fee = order.getVisitCost();
            try {
                String vistCost = order.getVisitCost().replace("元","");
                this.fee = new BigDecimal(vistCost).stripTrailingZeros().toEngineeringString();
            }catch (Exception e){

            }


        }


    }
}
