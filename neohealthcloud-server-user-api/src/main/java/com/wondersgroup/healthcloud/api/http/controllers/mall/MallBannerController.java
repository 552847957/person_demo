package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.services.mall.GoldRecordService;
import com.wondersgroup.healthcloud.services.mall.MallBannerService;

@RestController
@RequestMapping("/api/mall")
public class MallBannerController {

	@Autowired
	MallBannerService bannerService;

	@Autowired
	GoldRecordService goldRecordService;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public Object home(String userId) {
		JsonResponseEntity<Map<String, Object>> responseEntity = new JsonResponseEntity<>();
		Map<String, Object> map = new HashMap<>();

		List<Goods> banner = bannerService.findHomeBanner();
		Integer restGold = goldRecordService.findRestGoldByUserId(userId);
		map.put("banner", banner);
		map.put("restGold", restGold);
		
		responseEntity.setData(map);
		return responseEntity;
	}

}
