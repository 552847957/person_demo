package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
public class ExchangeOrder {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "goods_id")
	private Integer goodsId; // 关联商品ID

	@Column(name = "user_id")
	private String userId; // 关联用户ID

	@Column(name = "customer_name")
	private String customerName; // 客户名称

	private String tel; // 联系方式

	private String area; // 地区

	private String address; // 详细地址

	private Integer status; // 状态 (0：未发货； 1：已发货)

	@Column(name = "operator_id")
	private String operatorId; // 操作员ID

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

}
