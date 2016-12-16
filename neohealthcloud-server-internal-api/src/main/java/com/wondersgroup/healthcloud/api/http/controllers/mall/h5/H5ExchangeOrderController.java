package com.wondersgroup.healthcloud.api.http.controllers.mall.h5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;
import com.wondersgroup.healthcloud.services.mall.ExchangeOrderService;
import com.wondersgroup.healthcloud.services.mall.dto.ExchangeOrderDto;

@RestController
@RequestMapping("/api/h5/exchange/order")
public class H5ExchangeOrderController {

	@Autowired
	ExchangeOrderService exchangeOrderService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object findByUserId(String userId, int flag) {
		JsonListResponseEntity<ExchangeOrderDto> responseEntity = new JsonListResponseEntity<>();
		Page<ExchangeOrderDto> page = exchangeOrderService.findByUserId(userId, flag);

		flag = page.hasNext() ? flag + 1 : flag;
		responseEntity.setContent(page.getContent(), page.hasNext(), "createTime", flag + "");
		return responseEntity;
	}

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public Object details(String orderId) {
		JsonResponseEntity<ExchangeOrderDto> responseEntity = new JsonResponseEntity<>();
		ExchangeOrderDto orderDto = exchangeOrderService.orderDetails(orderId);
		responseEntity.setData(orderDto);
		return responseEntity;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Object exchange(@RequestBody ExchangeOrder order) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		exchangeOrderService.exchange(order);
		return responseEntity;
	}

	@RequestMapping(value = "/address", method = RequestMethod.GET)
	public Object address(String userId, Integer goodsType) {
		JsonResponseEntity<ExchangeOrder> responseEntity = new JsonResponseEntity<>();
		ExchangeOrder order = exchangeOrderService.address(userId, goodsType);
		responseEntity.setData(order);
		return responseEntity;
	}

}
