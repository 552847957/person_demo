package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 商城横幅
 * 
 * @author tanxueliang
 *
 */
@Data
@Entity
@Table(name = "mall_banner_tb")
public class MallBanner {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "goods_id")
	private Integer goodsId; // 商品ID

	@Column(name = "sort_no")
	private Integer sortNo; // 排序

	@Column(name = "start_time")
	private Date startTime; // 开始时间

	@Column(name = "end_time")
	private Date endTime; // 结束时间

	private Integer status; // 状态 0：下线；1：上线

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 更新时间

}
