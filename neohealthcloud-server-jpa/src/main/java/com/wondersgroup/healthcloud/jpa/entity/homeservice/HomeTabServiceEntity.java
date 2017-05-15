package com.wondersgroup.healthcloud.jpa.entity.homeservice;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2017/5/15.
 */
@Data
@Entity
@Table(name = "app_tb_tabservice")
public class HomeTabServiceEntity {
    @Id
    private String id;
    @Column(name = "img_url")
    private String  imgUrl; //图片地址
    @Column(name = "hoplink")
    private String  hoplink; //跳转链接
    @Column(name = "tab_type")
    private int  tabType; // 图标分类: 0 背景图片 1 非高亮图标,2 高亮图标
    @Column(name = "del_flag")
    private  String delFlag; // 删除标志 0：不删除 1：已删除
    @Column(name = "sort")
    private int sort; //排序
    @Column(name = "create_time")
    private Date createTime; //创建时间
    @Column(name = "update_time")
    private Date updateTime; // 更新时间
    @Column(name = "version")
    private String version; //版本号
}
