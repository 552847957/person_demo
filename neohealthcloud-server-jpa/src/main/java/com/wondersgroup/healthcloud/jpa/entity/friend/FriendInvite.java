package com.wondersgroup.healthcloud.jpa.entity.friend;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "friend_invite_tb")
public class FriendInvite {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "user_id")
	private String userId; // 邀请方用户ID

	@Column(name = "mobile_num")
	private String mobileNum; // 受邀请手机号码

	private Integer status; // 状态 0：未激活； 1：激活

	@Column(name = "create_time")
	private Date createTime; // 创建时间

	@Column(name = "update_time")
	private Date updateTime; // 最后更新时间

}
