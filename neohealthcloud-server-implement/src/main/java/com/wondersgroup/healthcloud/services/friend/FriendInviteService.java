package com.wondersgroup.healthcloud.services.friend;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.common.utils.RandomUtil;
import com.wondersgroup.healthcloud.jpa.entity.friend.FriendInvite;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.friend.FriendInviteRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;
import com.wondersgroup.healthcloud.utils.sms.SMS;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Transactional
public class FriendInviteService {

	final static String prefix = "neoheathcloud:friendInvite:smscode:";

	@Autowired
	FriendInviteRepository friendInviteRepository;

	@Autowired
	RegisterInfoRepository registerInfoRepository;

	@Autowired
	GoldRecordService goldRecordService;

	@Autowired
	SMS sms;

	@Autowired
	JedisPool jedisPool;

	public List<FriendInvite> findByUserIdAndActived(String userId) {
		return friendInviteRepository.findByUserIdAndStatus(userId, 1);
	}

	public Map<String, Object> invite(String userId, String mobileNum) {
		Map<String, Object> map = new HashMap<>();
		RegisterInfo user = registerInfoRepository.findByMobile(mobileNum);
		if (user != null) {
			// 老用户走的流程
			boolean isGet = goldRecordService.isGet(user.getRegisterid(), GoldRecordTypeEnum.INVITATION_OLD);
			if (!isGet) {
				int goldNum = RandomUtil.randomInt(1, 50);
				GoldRecord record = goldRecordService.save(user.getRegisterid(), goldNum,
						GoldRecordTypeEnum.INVITATION_OLD);
				map.put("code", 0);
				map.put("data", record.getGoldNum());
				return map;
			} else {
				map.put("code", 1001);
				map.put("msg", "今日不能再领取更多红包了:(");
				return map;
			}
		} else {
			// 新用户走的流程
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

	public void sendCode(String mobile) {
		Jedis jedis = jedisPool.getResource();
		String content = "您的验证码为:%s，10分钟内有效。";
		String key = prefix + mobile;
		String code = generateCode();
		sms.send(mobile, String.format(content, code));
		jedis.set(key, code);
		jedis.expire(key, 60 * 10);
	}

	public boolean verifySmsCode(String mobile, String code) {
		Jedis jedis = jedisPool.getResource();
		String key = prefix + mobile;
		String redisCode = jedis.get(key);
		if (code.equals(redisCode)) {
			return true;
		}
		return false;
	}

	private String generateCode() {
		int code = (int) (Math.random() * 1000000);
		return code + "";
	}

}
