package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;
import com.wondersgroup.healthcloud.services.mall.ExchangeOrderService;
import com.wondersgroup.healthcloud.services.mall.dto.ExchangeOrderDto;

@RestController
@RequestMapping("/api/exchangeorder")
public class ExchangeOrderController {

	@Autowired
	ExchangeOrderService exchangeOrderService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Object list(@RequestBody Pager pager) {
		Map map = pager.getParameter();
		Page<ExchangeOrderDto> page = exchangeOrderService.list(map, pager.getNumber(), pager.getSize());
		pager.setData(page.getContent());
		pager.setTotalElements(Integer.valueOf(page.getTotalElements() + ""));
		pager.setTotalPages(page.getTotalPages());
		return pager;
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public Object send(@RequestBody ExchangeOrder orderId) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		exchangeOrderService.send(orderId);
		return responseEntity;
	}

}
