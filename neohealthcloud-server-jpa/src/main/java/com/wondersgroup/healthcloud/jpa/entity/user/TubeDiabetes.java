package com.wondersgroup.healthcloud.jpa.entity.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by limenghua on 2017/5/17.
 * G端在管用户信息
 * 详情见数据库注释说明
 */
@Data
@Entity
@Table(name = "g_tube_diabetes_tb")
public class TubeDiabetes {
    @Id
    private String id;
    private String bkbh;
    private Timestamp ywscsj;
    private String bklx;
    private String xgbz;
    private String zjhm;// 证件号码
    private String zjlx;
    private String xm;
    private String xb;
    private Timestamp csrq;
    private String brgddh;
    private String brsjhm;
    private String lxrdh;
    private String mqzybm;
    private String cyzqdm;
    private String jzdShe;
    private String jzdShi;
    private String jzdXia;// 现居住地-县(区)
    private String jzdXng;// 现居住地-乡（镇、街道）
    private String jzdVlg;// 现居住地-居委
    private String jzdCun;// 现居住地-村（路、街、弄）
    private String jzdMph;// 现居住地-门牌号(包括“室”)
    private String jzdBcxx;
    private String hjdShe;
    private String hjdShi;
    private String hjdXia;
    private String hjdXng;
    private String hjdVlg;
    private String hjdCun;
    private String hjdMph;
    private String hjdBcxx;
    private String gwys;
    private String ttjycfx;
    private String tnbfx;
    private Timestamp qzrq;
    private String lxdShe;
    private String lxdShi;
    private String lxdXia;
    private String lxdXng;
    private String lxdVlg;
    private String lxdCun;
    private String lxdMph;
    private String lxdBcxx;
    private String ywglsqdm;
    private String sfyzzk;
    private String hcbz;
    private String bgz;
    private String yljgdm;
    private Timestamp bgrq;
    private String mqzrys;
    private String xgz;
    private String xgjgdm;
    private Timestamp xgrq;
    private String qxbm;
    private Timestamp update_time;
}
