package com.wondersgroup.healthcloud.api.http.controllers.step;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.api.http.dto.step.StepHomeDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.common.utils.AppUrlH5Utils;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.friend.FriendInvite;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import com.wondersgroup.healthcloud.services.friend.FriendInviteService;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;
import com.wondersgroup.healthcloud.services.step.StepCountService;

@RestController
@RequestMapping("/api/stepcount")
public class StepController {

	@Autowired
	StepCountService stepCountService;

	@Autowired
	GoldRecordService goldRecordService;

	@Autowired
	FriendInviteService friendInviteService;

	@Autowired
	AppConfigService appConfigService;

	@Autowired
	AppUrlH5Utils appUrlH5Utils;

	/**
	 * 获取计步首页数据
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	@VersionRange
	public Object home(@RequestHeader(value = "main-area", required = true) String mainArea, String userId) {
		JsonResponseEntity<StepHomeDto> responseEntity = new JsonResponseEntity<>();
		int restGold = goldRecordService.findRestGoldByUserId(userId);
		int awardGold = stepCountService.findAwardGold(userId);

		// 判断是否在活动时间内
		boolean isAccess = false;
		boolean isActivityTime = stepCountService.isActivityTime(mainArea, new Date());
		if (isActivityTime) {
			isAccess = !goldRecordService.isGet(userId, GoldRecordTypeEnum.REWARDS);
		}

		StepHomeDto home = new StepHomeDto();
		home.setAwardGold(awardGold);
		home.setRestGold(restGold);
		home.setAccess(isAccess);
		home.setActivityTime(isActivityTime);
		home.setHelpUrl(appUrlH5Utils.buildStepHelpUrl());
		home.setInviteUrl(appUrlH5Utils.buildStepInviteUrl(userId));
		home.setRuleUrl(appUrlH5Utils.buildStepRuleUrl());
		home.setMallUrl(appUrlH5Utils.buildStepMallUrl(userId));
		AppConfig config = appConfigService.findSingleAppConfigByKeyWord(mainArea, null, "step.invite.logo-url", "1");
		home.setLogoUrl(config.getData());
		responseEntity.setData(home);
		return responseEntity;
	}

	/**
	 * 领取计步金币奖励
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/gold/award", method = RequestMethod.GET)
	@VersionRange
	public Object getAwardGold(String userId) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();

		boolean isGet = goldRecordService.isGet(userId, GoldRecordTypeEnum.REWARDS);
		if (isGet) {
			responseEntity.setCode(1000);
			responseEntity.setMsg("您今天已经领取过奖励啦~");
			return responseEntity;
		}

		int goldNum = stepCountService.findAwardGold(userId);
		if (goldNum > 0) {
			goldRecordService.save(userId, goldNum, GoldRecordTypeEnum.REWARDS);
		}
		return responseEntity;
	}

	/**
	 * 分享
	 * 
	 * @param userId
	 * @param shareType
	 * @return
	 */
	@RequestMapping(value = "/share", method = RequestMethod.GET)
	@VersionRange
	public Object getGoldRecord(String userId, int shareType) {
		JsonResponseEntity<GoldRecord> responseEntity = new JsonResponseEntity<>();

		GoldRecordTypeEnum type = GoldRecordTypeEnum.values()[shareType];
		boolean isGet = goldRecordService.isGet(userId, type);
		if (isGet) {
			return responseEntity;
		}

		int goldNum = 10;
		goldRecordService.save(userId, goldNum, type);
		return responseEntity;
	}

	@RequestMapping(value = "/invite", method = RequestMethod.GET)
	@VersionRange
	public Object invite(String userId) {
		JsonResponseEntity<Integer> responseEntity = new JsonResponseEntity<>();
		List<FriendInvite> list = friendInviteService.findByUserIdAndActived(userId);
		responseEntity.setData(list.size());
		return responseEntity;

	}

	/**
	 * 查询今日计步数据
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@VersionRange
	public Object today(String userId) {
		return stepCountService.findStepByUserIdAndDate(userId, new Date());
	}

	/**
	 * 查询历史计步数据
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@VersionRange
	public Object history(String userId) {
		return stepCountService.findHistoryStep(userId);
	}

	/**
	 * 更新今日计步数据
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@VersionRange
	public Object save(@RequestBody Map<String, Object> request) {
		return stepCountService.save(request);
	}

}
