package com.wondersgroup.healthcloud.jpa.entity.friend;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "friend_relationship_tb")
public class FriendRelationship {

	@Id
	@Column(length = 32)
	private String id;

	@Column(name = "user_id")
	private String userId; // 用户ID

	@Column(name = "friend_user_id")
	private String friendUserId; // 好友用户ID

	@Column(name = "create_time")
	private Date createTime; // 创建时间

}
