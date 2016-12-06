package com.wondersgroup.healthcloud.jpa.entity.bbs;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 圈子分类
 * Created by ys on 2016/08/11.
 * @author ys
 */
@Entity
@Data
@Table(name = "tb_bbs_circle_category")
public class CircleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer rank;//排序

    @Column(name = "del_flag")
    private String delFlag="0";

    @Column(name = "create_time")
    private Date createTime;


}
