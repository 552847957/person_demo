package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

/**
 * 虚拟商品明细
 * 
 * @author tanxueliang
 *
 */
@JsonNaming
@Data
@Entity
@Table(name = "goods_item_tb")
@JsonInclude(Include.NON_NULL)
public class GoodsItem {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "goods_id")
	private Integer goodsId; // 商品ID

	private String code; // 券码

	private Integer status; // 状态 （0：未兑换；1：已兑换）

	@Column(name = "user_id")
	private String userId; // 兑换用户ID

	@Column(name = "order_id")
	private String orderId; // 兑换订单ID

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

	@Transient
	private String name;

	@Transient
	@JsonIgnore
	private String nickname;

	public String getName() {
		if (StringUtils.isBlank(name)) {
			name = this.nickname;
		}
		return name;
	}

}
