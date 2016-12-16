package com.wondersgroup.healthcloud.services.friend;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.friend.FriendInvite;
import com.wondersgroup.healthcloud.jpa.entity.friend.FriendRelationship;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.friend.FriendInviteRepository;
import com.wondersgroup.healthcloud.jpa.repository.friend.FriendRelationshipRepository;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;

@Service
@Transactional(readOnly = true)
public class FriendRelationshipService {

	@Autowired
	FriendRelationshipRepository friendRelationshipRepository;

	@Autowired
	FriendInviteRepository friendInviteRepository;

	@Autowired
	GoldRecordService goldRecordService;

	@Transactional(readOnly = false)
	public void login(String mobile, String userId) {
		FriendInvite invite = friendInviteRepository.findByMobileNum(mobile);
		if (invite == null || invite.getStatus() == 1) {
			return;
		}

		// 激活邀请
		invite.setStatus(1);
		invite.setUpdateTime(new Date());
		friendInviteRepository.save(invite);

		// 添加好友关系
		FriendRelationship relationship = new FriendRelationship();
		relationship.setId(IdGen.uuid());
		relationship.setFriendUserId(userId);
		relationship.setUserId(invite.getUserId());
		relationship.setCreateTime(new Date());
		friendRelationshipRepository.save(relationship);

		// 用户一天只能收取五次邀请金币
		List<GoldRecord> records = goldRecordService.findByUserIdAndTypeAndCreateTime(userId,
				GoldRecordTypeEnum.INVITATION, new Date());
		if (records.size() >= 5) {
			return;
		}

		int goldNum = 100;
		goldRecordService.save(invite.getUserId(), goldNum, GoldRecordTypeEnum.INVITATION);
	}

	@Transactional(readOnly = false)
	public void register(String userId) {
		goldRecordService.save(userId, 100, GoldRecordTypeEnum.INITIALIZATION);
	}

}
