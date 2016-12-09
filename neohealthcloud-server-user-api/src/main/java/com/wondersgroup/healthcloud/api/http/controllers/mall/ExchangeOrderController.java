package com.wondersgroup.healthcloud.api.http.controllers.mall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.mall.ExchangeOrderService;
import com.wondersgroup.healthcloud.services.mall.dto.ExchangeOrderDto;

@RestController
@RequestMapping("/api/exchange/order")
public class ExchangeOrderController {

	@Autowired
	ExchangeOrderService exchangeOrderService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@VersionRange
	public Object findByUserId(String userId, int flag) {
		JsonListResponseEntity<ExchangeOrderDto> responseEntity = new JsonListResponseEntity<>();
		Page<ExchangeOrderDto> page = exchangeOrderService.findByUserId(userId, flag);

		flag = page.hasNext() ? flag + 1 : flag;
		responseEntity.setContent(page.getContent(), page.hasNext(), "createTime", flag + "");
		return responseEntity;
	}

}
