package com.wondersgroup.healthcloud.api.http.controllers.mall.h5;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.services.mall.GoodsService;
import com.wondersgroup.healthcloud.services.mall.dto.GoodsDto;

@RestController
@RequestMapping("/api/h5/goods")
public class H5GoodsController {

	@Autowired
	GoodsService goodsService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list(int flag) {
		JsonListResponseEntity<Goods> responseEntity = new JsonListResponseEntity<>();
		PageRequest pageable = new PageRequest(flag, 20, Direction.ASC, "sortNo");
		int status = 1; // 上架
		Page<Goods> page = goodsService.findByStatus(status, pageable);

		flag = page.hasNext() ? flag + 1 : flag;
		responseEntity.setContent(page.getContent(), page.hasNext(), "sortNo", flag + "");
		return responseEntity;

	}

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public Object details(Integer goodsId, String userId) {
		JsonResponseEntity<GoodsDto> responseEntity = new JsonResponseEntity<>();
		Goods goods = goodsService.findById(goodsId);

		GoodsDto target = new GoodsDto();
		BeanUtils.copyProperties(goods, target);

		if (goods.getType() != 1) {
			// 判断用户今天是否已经兑换过此商品
			target.setHasExchange(goodsService.hasExchange(goodsId, userId));
		}

		responseEntity.setData(target);
		return responseEntity;
	}
}
