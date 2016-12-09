package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoldRecord;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;

@RestController
@RequestMapping("/api/gold/record")
public class GoldRecordController {

	@Autowired
	GoldRecordService goldRecordService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list(String userId, int flag) {
		JsonListResponseEntity<GoldRecord> responseEntity = new JsonListResponseEntity<>();
		int restGold = goldRecordService.findRestGoldByUserId(userId);

		Map<String, Object> extras = new HashMap<>();
		extras.put("restGold", restGold);

		PageRequest pageable = new PageRequest(flag, 20, Direction.DESC, "createTime");
		Page<GoldRecord> page = goldRecordService.findByUserId(userId, pageable);

		flag = page.hasNext() ? flag + 1 : flag;
		responseEntity.setContent(page.getContent(), page.hasNext(), "createTime", flag + "");
		responseEntity.setExtras(extras);
		return responseEntity;
	}
}
