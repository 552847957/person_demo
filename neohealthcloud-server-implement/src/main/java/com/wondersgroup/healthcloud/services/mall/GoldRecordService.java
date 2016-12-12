package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoldRecordRepository;

@Service
@Transactional(readOnly = true)
public class GoldRecordService {

	@Autowired
	GoldRecordRepository goldRecordRepository;

	public Integer findRestGoldByUserId(String userId) {
		Integer restGold = 0;
		GoldRecord goldRecord = goldRecordRepository.findByUserIdRecently(userId);
		if (goldRecord != null) {
			restGold = goldRecord.getRestNum();
		}
		return restGold;
	}

	public List<GoldRecord> findByUserIdAndTypeAndCreateTime(String userId, GoldRecordTypeEnum type, Date date) {
		return goldRecordRepository.findByUserIdAndTypeAndCreateTime(userId, type.ordinal(), date);
	}

	/**
	 * 新增金币记录
	 * 
	 * @param userId
	 * @param goldNum
	 * @param type
	 * @return
	 */
	@Transactional(readOnly = false)
	public GoldRecord save(String userId, int goldNum, GoldRecordTypeEnum type) {
		int restGold = findRestGoldByUserId(userId);

		GoldRecord entity = new GoldRecord();
		entity.setUserId(userId);
		entity.setGoldNum(goldNum);
		entity.setType(type);
		entity.setId(IdGen.uuid());
		entity.setCreateTime(new Date());
		entity.setRestNum(restGold + goldNum);

		return goldRecordRepository.save(entity);
	}

	/**
	 * 判断今天是否已领取该类型奖励
	 * 
	 * @param userId
	 * @param type
	 * @return
	 */
	public boolean isGet(String userId, GoldRecordTypeEnum type) {
		List<GoldRecord> goldRecord = findByUserIdAndTypeAndCreateTime(userId, type, new Date());
		if (goldRecord != null && goldRecord.size() > 0) {
			return true;
		}
		return false;
	}

	public Page<GoldRecord> findByUserId(String userId, Pageable pageable) {
		return goldRecordRepository.findByUserId(userId, pageable);
	}
}
