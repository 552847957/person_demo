package com.wondersgroup.healthcloud.jpa.entity.mall;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;

import lombok.Data;

@JsonNaming
@Data
@Entity
@Table(name = "gold_record_tb")
public class GoldRecord {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "user_id")
	private String userId; // 用户ID

	private Integer type; // 金币记录类型 （0：计步奖励；1：每日计步；2：邀请好友；3：兑换商品,
							// 4：QQ分享；5：微博分享；6微信分享；7：朋友圈分享；8：老用户领取邀请奖励）

	@Column(name = "gold_num")
	private Integer goldNum; // 金币数目 （负数代表消耗金币）

	@Column(name = "rest_num")
	private Integer restNum; // 剩余金币数目

	@JsonFormat(pattern = "yyyy-MM-dd")
	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Transient
	private String typeName;

	public void setType(GoldRecordTypeEnum type) {
		this.type = type.ordinal();
	}

	public String getTypeName() {
		switch (type) {
		case 0:
			typeName = "计步奖励";
			break;
		case 1:
			typeName = "每日计步";
			break;
		case 2:
			typeName = "邀请好友";
			break;
		case 3:
			typeName = "兑换商品";
			break;
		case 4:
			typeName = "分享奖励";
			break;
		case 5:
			typeName = "分享奖励";
			break;
		case 6:
			typeName = "分享奖励";
			break;
		case 7:
			typeName = "分享奖励";
			break;
		case 8:
			typeName = "邀请好友";
			break;
		default:
			typeName = "";
			break;
		}
		return typeName;
	}
}
