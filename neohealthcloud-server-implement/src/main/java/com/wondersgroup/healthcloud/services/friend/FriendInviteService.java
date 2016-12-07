package com.wondersgroup.healthcloud.services.friend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.friend.FriendInvite;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.friend.FriendInviteRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;

@Service
@Transactional
public class FriendInviteService {

	@Autowired
	FriendInviteRepository friendInviteRepository;

	@Autowired
	RegisterInfoRepository registerInfoRepository;

	@Autowired
	GoldRecordService goldRecordService;

	public List<FriendInvite> findByUserIdAndActived(String userId) {
		return friendInviteRepository.findByUserIdAndStatus(userId, 1);
	}

	public Map<String, Object> invite(String userId, String mobileNum) {
		Map<String, Object> map = new HashMap<>();
		RegisterInfo user = registerInfoRepository.findByMobile(mobileNum);
		if (user != null) {
			boolean isGet = goldRecordService.isGet(user.getRegisterid(), GoldRecordTypeEnum.INVITATION_OLD);
			if (!isGet) {
				GoldRecord record = goldRecordService.save(user.getRegisterid(), 100,
						GoldRecordTypeEnum.INVITATION_OLD);
				map.put("code", 0);
				map.put("data", record.getGoldNum());
				return map;
			} else {
				map.put("code", 1001);
				map.put("msg", "今日不能再领取更多红包了:(");
				return map;
			}
		}

		FriendInvite friendInvite = friendInviteRepository.findByMobileNum(mobileNum);
		if (friendInvite != null) {
			map.put("code", 1002);
			map.put("msg", "使用以下手机登陆领取更多红包");
			return map;
		}

		friendInvite = new FriendInvite();
		friendInvite.setId(IdGen.uuid());
		friendInvite.setMobileNum(mobileNum);
		friendInvite.setStatus(0);
		friendInvite.setUserId(userId);
		friendInvite.setCreateTime(new Date());
		friendInvite.setUpdateTime(new Date());
		friendInviteRepository.save(friendInvite);

		map.put("code", 0);
		map.put("data", 100);
		return map;
	}

}
