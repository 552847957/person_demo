package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoldRecordRepository;

@Service
@Transactional(readOnly = true)
public class GoldRecordService {

	@Autowired
	GoldRecordRepository goldRecordRepository;

	public Integer findRestGoldByUserId(String userId) {
		Integer restGold = 0;

		GoldRecord goldRecord = findRecent(userId);
		if (goldRecord != null) {
			restGold = goldRecord.getRestNum();
		}

		return restGold;
	}

	public GoldRecord findRecent(String userId) {
		GoldRecord goldRecord = null;
		List<GoldRecord> list = goldRecordRepository.findByUserIdOrderByCreateTimeDesc(userId, new PageRequest(0, 1));
		if (list != null && list.size() > 0) {
			goldRecord = list.get(0);
		}
		return goldRecord;
	}

	public GoldRecord findRecentByType(String userId, int type) {
		GoldRecord goldRecord = null;
		Pageable pageable = new PageRequest(0, 1);
		List<GoldRecord> list = goldRecordRepository.findByUserIdAndTypeOrderByCreateTimeDesc(userId, type, pageable);
		if (list != null && list.size() > 0) {
			goldRecord = list.get(0);
		}
		return goldRecord;
	}

	public GoldRecord save(GoldRecord entity) {
		String userId = entity.getUserId();
		int restGold = findRestGoldByUserId(userId);

		entity.setId(IdGen.uuid());
		entity.setCreateTime(new Date());
		entity.setRestNum(restGold + entity.getGoldNum());

		return goldRecordRepository.save(entity);
	}
}
