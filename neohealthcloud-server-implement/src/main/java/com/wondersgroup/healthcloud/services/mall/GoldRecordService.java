package com.wondersgroup.healthcloud.services.mall;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecordComparator;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoldRecordRepository;

@Service
@Transactional(readOnly = true)
public class GoldRecordService {

	@Autowired
	GoldRecordRepository goldRecordRepository;

	public Integer findRestGoldByUserId(String userId) {
		Integer restGold = 0;
		List<GoldRecord> goldRecord = goldRecordRepository.findByUserIdRecently(userId);
		GoldRecordComparator comparator = new GoldRecordComparator();
		if (CollectionUtils.isNotEmpty(goldRecord)) {
		    if(goldRecord.get(0).getType()==3){
		        restGold=Collections.min(goldRecord,comparator).getRestNum();
		    }else{
		        restGold=Collections.max(goldRecord,comparator).getRestNum();
		    }
		}
		return restGold;
	}

	public List<GoldRecord> findByUserIdAndTypeAndCreateTime(String userId, GoldRecordTypeEnum type) {
		return goldRecordRepository.findByUserIdAndTypeAndCreateTime(userId, type.ordinal());
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
		List<GoldRecord> goldRecord = findByUserIdAndTypeAndCreateTime(userId, type);
		if (goldRecord != null && goldRecord.size() > 0) {
			return true;
		}
		return false;
	}

	public Page<GoldRecord> findByUserId(String userId, Pageable pageable) {
		return goldRecordRepository.findByUserId(userId, pageable);
	}
}
