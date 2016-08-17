package com.wondersgroup.healthcloud.jpa.entity.foodStore;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by longshasha on 16/8/10.
 */
@Data
@Entity
@Table(name = "app_food_store_category")
public class FoodStoreCategory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_name")
    private String categoryName;// 食物库分类

    private String tags;//标签

    private String icon;

    private Integer rank;

    @Column(name = "is_show")
    private Integer isShow;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "create_time")
    private Date createTime;
}
