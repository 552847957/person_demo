package com.wondersgroup.healthcloud.jpa.entity.diabetes;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by longshasha on 17/5/15.
 */
@Data
@Entity
@Table(name = "fam_doctor_tube_sign_user")
public class DoctorTubeSignUser {

    @Id
    private String id;

    private String avatar;//头像

    private String name;

    private Integer age;

    private String gender;

    private Date birth;//出生日期

    @Column(name = "card_type")
    private String cardType;//证件类型 01:身份证

    @Column(name = "card_number")
    private String cardNumber;//身份证号

    private String profession;//职业

    @Column(name = "employ_status")
    private String employStatus;//就业状况  31：学生 、70：无业人员、80：退（离）休人员、90：其他

    @Column(name = "moblile_phone")
    private String moblilePhone;//手机号码

    @Column(name = "fixed_phone")
    private String fixedPhone;//固定电话

    @Column(name = "contact_phone")
    private String contactPhone;//联系电话

    @Column(name = "hospitalCode")
    private String hospital_code;//医疗机构代码

    @Column(name = "tubeDoctorpersoncard")
    private String tube_doctor_personcard;//所属在管医生身份证号

    @Column(name = "tube_type")
    private Integer tubeType;//在管区域类型  0：C端用户街道判断 1：G端在管

    @Column(name = "sign_doctor_personcard")
    private String signDoctorPersoncard;//所属签约医生身份证号

    @Column(name = "signStatus")
    private String sign_status;//签约状态 1：已签约,0:未签约

    private String identifytype;//0：未实名认证 1：已实名认证C端用户

    @Column(name = "hyp_type")
    private String hypType;//高血压类型 0：非高血压 1：高血压

    @Column(name = "diabetes_type")
    private String diabetesType;//糖尿病类型 0:没有糖尿病、1:1型糖尿病,2:2型糖尿病；3:其它特殊类型糖尿病，4:妊娠糖尿病

    @Column(name = "apo_type")
    private String apoType;//脑卒中类型 0：没有脑卒中 1：脑卒中

    @Column(name = "is_risk")
    private String isRisk;//是否高危 0:不是高危(根据用户端危险评估判断，有高糖脑的属于高危) 1:高危

    @Column(name = "del_flag")
    private String delFlag = "0";//删除标记0：正常 1：删除

    @Column(name = "source_id")
    private String sourceId;


    private String create_by;

    @Column(name = "create_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createDate;

    private String update_by;

    @Column(name = "update_date")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateDate;



}
