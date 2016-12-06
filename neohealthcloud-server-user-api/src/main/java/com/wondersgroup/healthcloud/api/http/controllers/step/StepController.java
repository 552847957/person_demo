package com.wondersgroup.healthcloud.api.http.controllers.step;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.api.http.dto.step.StepHomeDto;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;
import com.wondersgroup.healthcloud.services.step.StepCountService;

@RestController
@RequestMapping("/api/stepcount")
public class StepController {

	@Autowired
	StepCountService stepCountService;

	@Autowired
	GoldRecordService goldRecordService;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	@VersionRange
	public Object home(String userId) {
		JsonResponseEntity<StepHomeDto> responseEntity = new JsonResponseEntity<>();

		GoldRecord goldRecord = goldRecordService.findRecentByType(userId, 1);
		int restGold = goldRecordService.findRestGoldByUserId(userId);
		int awardGold = stepCountService.findAwardGold(userId);
		StepHomeDto home = new StepHomeDto();
		home.setAwardGold(awardGold);
		home.setRestGold(restGold);
		if (goldRecord == null) {
			home.setGet(false);
		} else {
			home.setGet(home.isToday(goldRecord.getCreateTime()));
		}
		// TODO 需要设置帮助链接、规则链接

		responseEntity.setData(home);
		return responseEntity;
	}

	@RequestMapping(value = "/gold/award", method = RequestMethod.GET)
	@VersionRange
	public Object getAwardGold(String userId, int goldNum) {
		GoldRecord goldRecord = goldRecordService.findRecentByType(userId, 1);
		// TODO 需要判断今日是否已经领取
		
		GoldRecord record = new GoldRecord();
		record.setGoldNum(goldNum);
		record.setUserId(userId);
		record.setType(1);

		goldRecordService.save(record);
		return new JsonResponseEntity<>(0, null);
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@VersionRange
	public Object getGoldRecord(String userId) {
		GoldRecord goldRecord = goldRecordService.findRecentByType(userId, 1);
		return goldRecord;
	}

	@RequestMapping(method = RequestMethod.GET)
	@VersionRange
	public Object today(String userId) {
		return stepCountService.findStepByUserIdAndDate(userId, new Date());
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@VersionRange
	public Object history(String userId) {
		return stepCountService.findHistoryStep(userId);
	}

	@RequestMapping(method = RequestMethod.POST)
	@VersionRange
	public Object save(@RequestBody Map<String, Object> request) {
		return stepCountService.save(request);
	}

}
