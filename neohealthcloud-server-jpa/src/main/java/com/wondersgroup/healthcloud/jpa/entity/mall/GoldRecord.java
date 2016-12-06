package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "gold_record_tb")
public class GoldRecord {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "user_id")
	private String userId; // 用户ID

	private Integer type; // 金币记录类型 （0：计步奖励；1：每日计步；2：分享奖励；3：邀请好友；4：兑换商品）

	@Column(name = "gold_num")
	private Integer goldNum; // 金币数目 （负数代表消耗金币）

	@Column(name = "rest_num")
	private Integer restNum; // 剩余金币数目

	@Column(name = "create_time")
	private Date createTime; // 创建时间
	

}
