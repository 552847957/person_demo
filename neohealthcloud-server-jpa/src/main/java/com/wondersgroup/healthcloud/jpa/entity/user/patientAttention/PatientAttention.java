package com.wondersgroup.healthcloud.jpa.entity.user.patientAttention;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 16/8/17.
 */
@Data
@Entity
@Table(name = "app_tb_patient_attention")
public class PatientAttention extends BaseEntity {

    @Column(name = "register_id")
    private String registerId;

    @Column(name = "attention_id")
    private String attentionId;

    @Column(name = "attention_starttime")
    private Date attentionStarttime;

    @Column(name = "attention_endtime")
    private Date attentionEndtime;


}
