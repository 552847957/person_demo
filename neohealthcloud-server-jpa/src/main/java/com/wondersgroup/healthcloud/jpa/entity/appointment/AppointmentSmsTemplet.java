package com.wondersgroup.healthcloud.jpa.entity.appointment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/12/23.
 * 预约挂号短信模板
 */
@Data
@Entity
@Table(name = "app_tb_appointment_sms_templet")
public class AppointmentSmsTemplet {

    @Id
    @Column(name = "hospital_code")
    private String hospitalCode;//医院代码

    @Column(name = "hospital_name")
    private String hospitalName;//医院名称

    @Column(name = "order_doctor")
    private String orderDoctor;//预约专家

    @Column(name = "order_disease")
    private String orderDisease;//预约专病

    @Column(name = "order_common")
    private String orderCommon;//预约普通

    @Column(name = "cancel_desc")
    private String cancelDesc;//取消提示

    @Column(name = "cancel_doctor")
    private String cancelDoctor;//取消专家

    @Column(name = "cancel_disease")
    private String cancelDisease;//取消专病

    @Column(name = "cancel_common")
    private String cancelCommon;//取消普通


    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "update_date")
    private Date updateDate;
    @Column(name = "update_by")
    private String updateBy;

}
