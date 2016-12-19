package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * 商品
 * 
 * @author tanxueliang
 *
 */
@JsonNaming
@Data
@Entity
@Table(name = "goods_tb")
@JsonInclude(Include.NON_NULL)
public class Goods {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id; // 商品ID

	private String name; // 商品名称

	private Integer type; // 商品类型 0：虚拟物品；1：单品； 2：服务包
	
	@Column(name = "order_type")
	private Integer orderType; //单纯是为了后台排序业务，单品商品排在最前面，0：；单品1：其他

	private String area; // 区域

	@Column(name = "hospital_id")
	private String hospitalId; // 医院代码

	@Column(name = "hospital_name")
	private String hospitalName;

	private String picture; // 商品图片

	private String introduce; // 商品介绍

	private Integer price; // 兑换金币

	@Column(name = "sort_no")
	private Integer sortNo; // 排序

	private Integer num; // 商品总数

	@Column(name = "sales_num")
	private Integer salesNum; // 已售数量

	@Column(name = "stock_num")
	private Integer stockNum; // 库存数量

	private Integer status; // 商品状态(0：未上架；1：上架)

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "end_time")
	private Date endTime; // 截止时间

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

}
