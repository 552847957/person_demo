package com.wondersgroup.healthcloud.jpa.enums;

public enum GoldRecordTypeEnum {

	INITIALIZATION(0), // 注册奖励
	REWARDS(1), // 每日计步
	INVITATION(2), // 邀请
	EXCHANGE(3), // 兑换商品
	SHARE_QQ(4), // QQ分享
	SHARE_WEIBO(5), // 微博分享
	SHARE_WEIXIN(6), // 微信分享
	SHARE_FRIEND(7), // 朋友圈分享
	INVITATION_OLD(8); // 老用户领取邀请

	Integer value;

	GoldRecordTypeEnum(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
