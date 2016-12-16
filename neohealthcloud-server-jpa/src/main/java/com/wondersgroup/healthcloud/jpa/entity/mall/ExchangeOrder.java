package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * 兑换订单
 * 
 * @author tanxueliang
 *
 */
@Data
@Entity
@Table(name = "exchange_order_tb")
@JsonNaming
public class ExchangeOrder {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "goods_id")
	private Integer goodsId; // 关联商品ID

	@Column(name = "goods_type")
	private Integer goodsType; // 商品类型

	@Column(name = "goods_name")
	private String goodsName; // 商品名称

	@Column(name = "gold_num")
	private Integer goldNum; // 兑换金币数

	@Column(name = "user_id")
	private String userId; // 关联用户ID

	@Column(name = "customer_name")
	private String customerName; // 客户名称

	private String tel; // 联系方式

	private String area; // 地区

	@Column(name = "area_code")
	private String areaCode; // 地区编码

	private String street; // 街道

	@Column(name = "street_code")
	private String streetCode; // 街道代码

	private String address; // 详细地址

	@Column(name = "tracking_number")
	private String trackingNumber; // 快递单号

	@Column(name = "express_company")
	private String expressCompany; // 快递公司

	private Integer status; // 状态 (0：未发货； 1：已发货)

	@Column(name = "operator_id")
	private String operatorId; // 操作员ID

	@Column(name = "end_time")
	private Date endTime; // 结束时间

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

}
