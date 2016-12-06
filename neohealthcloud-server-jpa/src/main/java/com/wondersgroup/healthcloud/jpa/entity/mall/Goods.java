package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 商品
 * 
 * @author tanxueliang
 *
 */
@Data
@Entity
@Table(name = "goods_tb")
public class Goods {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id; // 商品ID

	private String name; // 商品名称

	private Integer type; // 商品类型 0：虚拟物品；1：单品

	private Integer picture; // 商品图片

	private String introduce; // 商品介绍

	private Integer price; // 兑换金币

	@Column(name = "sort_no")
	private Integer sortNo; // 排序

	private Integer num; // 商品总数

	@Column(name = "sales_num")
	private Integer salesNum; // 已售数量

	@Column(name = "stock_num")
	private Integer stockNum; // 库存数量

	private Integer status; // 商品状态

	@Column(name = "end_time")
	private Date endTime; // 截止时间

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

}
