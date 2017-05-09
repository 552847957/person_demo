package com.wondersgroup.healthcloud.jpa.entity.homeservice;

import lombok.Data;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by Administrator on 2017/5/9.
 */
@Data
@Entity
@Table(name = "app_tb_neoservice")
public class HomeServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "main_title")
     private String mainTitle; //主标题
    @Column(name = "img_url")
    private String  imgUrl; //图片地址
    @Column(name = "hoplink")
    private String  hoplink; //跳转链接
    @Column(name = "certified")
    private int certified; //是否需要实名认证:0 不需要，1 需要
    @Column(name = "service_type")
    private int  serviceType; //服务分类: 0 默认服务（APP端不允许删除）,1 基础服务，2 特色服务,3 医养云
    @Column(name = "allow_close")
    private int   allowClose ; //允许关闭,0-不允许,1-允许
    @Column(name = "del_flag")
    private  String delFlag; // 删除标志 0：不删除 1：已删除
    @Column(name = "sort")
    private int sort; //排序
    @Column(name = "create_time")
    private Date createTime; //创建时间
    @Column(name = "update_time")
    private Date updateTime; // 更新时间
    @Column(name = "remark")
    private String remark; // 备注
    @Column(name = "version")
    private String version; //版本号
}
