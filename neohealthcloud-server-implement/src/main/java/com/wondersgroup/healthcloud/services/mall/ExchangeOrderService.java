package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsItem;
import com.wondersgroup.healthcloud.jpa.enums.GoldRecordTypeEnum;
import com.wondersgroup.healthcloud.jpa.repository.mall.ExchangeOrderRepository;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsItemRepository;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.mall.dto.ExchangeOrderDto;

import scala.collection.mutable.StringBuilder;

@Service
@Transactional
public class ExchangeOrderService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ExchangeOrderRepository exchangeOrderRepository;

	@Autowired
	GoodsRepository goodsRepository;

	@Autowired
	RegisterInfoRepository registerInfoRepository;

	@Autowired
	GoldRecordService goldRecordService;

	@Autowired
	GoodsItemRepository goodsItemRepository;

	public Page<ExchangeOrderDto> list(Map map, int page, int size) {
		String goodsName = (String) map.get("goodsName");
		Integer goodsType = (Integer) map.get("goodsType");
		String userName = (String) map.get("userName");
		Integer orderStatus = (Integer) map.get("orderStatus");
		String orderId = (String) map.get("orderId");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");

		String sql = "select  a.*, if(c.identifytype=1,c.name,c.nickname)  userName " + "from exchange_order_tb a";

		sql += " left join  app_tb_register_info c on a.user_id = c.`registerid`";
		if (StringUtils.isNotBlank(userName)) {
			sql += " and ((c.identifytype = 0 and c.nickname like '%" + userName
					+ "%') or (c.identifytype = 1 and c.name like '%" + userName + "%'))";
		}

		sql += " where 1=1 ";
		if (goodsType != null) {
			sql += " and a.goods_type =" + goodsType;
		}
		if (StringUtils.isNotBlank(goodsName)) {
			sql += " and a.goods_name like '%" + goodsName + "%'";
		}
		if (StringUtils.isNotBlank(orderId)) {
			sql += " and a.id like  '%" + orderId + "%'";
		}
		if (orderStatus != null) {
			sql += " and a.status = " + orderStatus;
		}
		if (StringUtils.isNotBlank(startTime)) {
			sql += " and date_format(a.create_time, '%Y-%m-%d') >= '" + startTime + "'";
		}
		if (StringUtils.isNotBlank(endTime)) {
			sql += " and '" + endTime + "' >= date_format(a.create_time, '%Y-%m-%d') ";
		}

		System.out.println(sql);
		int start = page > 0 ? (page - 1) * size : 0;
		String querySql = sql + " limit " + start + "," + (start + size);
		List<ExchangeOrderDto> list = jdbcTemplate.query(querySql,
				new BeanPropertyRowMapper<ExchangeOrderDto>(ExchangeOrderDto.class));

		String countSql = "select count(1) " + sql.substring(sql.indexOf("from"));
		int total = jdbcTemplate.queryForObject(countSql, Integer.class);

		return new PageImpl<>(list, new PageRequest(page, size), total);
	}

	public void send(ExchangeOrder order) {
		ExchangeOrder tbOrder = exchangeOrderRepository.findOne(order.getId());
		tbOrder.setTrackingNumber(order.getTrackingNumber());
		tbOrder.setExpressCompany(order.getExpressCompany());
		tbOrder.setStatus(1);
		tbOrder.setUpdateTime(new Date());
		exchangeOrderRepository.save(tbOrder);
	}

	public Page<ExchangeOrderDto> findByUserId(String userId, int flag) {
		int size = 20;
		int start = flag > 0 ? (flag - 1) * size : 0;
		int end = start + size;
		String sql = "from exchange_order_tb a left join goods_tb b on a.goods_id = b.id where a.user_id = ?";
		String query = "select a.*, b.picture " + sql + " order by a.create_time desc limit ?, ?";

		List<ExchangeOrderDto> list = jdbcTemplate.query(query, new Object[] { userId, start, end },
				new BeanPropertyRowMapper<ExchangeOrderDto>(ExchangeOrderDto.class));

		int total = jdbcTemplate.queryForObject("select count(1)" + sql, new Object[] { userId }, Integer.class);
		return new PageImpl<>(list, new PageRequest(flag, size), total);
	}

	public ExchangeOrderDto orderDetails(String orderId) {
		ExchangeOrder order = exchangeOrderRepository.findOne(orderId);
		ExchangeOrderDto exchangeOrderDto = new ExchangeOrderDto();
		BeanUtils.copyProperties(order, exchangeOrderDto);

		Goods goods = goodsRepository.findOne(order.getGoodsId());
		if (goods != null) {
			exchangeOrderDto.setPicture(goods.getPicture());
		}

		if (order.getGoodsType() == 0) {
			GoodsItem item = goodsItemRepository.findByOrderId(orderId);
			if (item != null) {
				exchangeOrderDto.setTicketCode(item.getCode());
			}
		}
		return exchangeOrderDto;
	}

	public void exchange(ExchangeOrder order) {
		Goods goods = goodsRepository.findOne(order.getGoodsId());
		Date date = new Date();
		Integer orderType = goods.getType();

		int restGold = goldRecordService.findRestGoldByUserId(order.getUserId());
		if (0 >= goods.getStockNum()) {
			throw new CommonException(1001, "商品已兑完");
		}

		if (goods.getPrice() > restGold) {
			throw new CommonException(1002, "金币不足");
		}

		if (goods.getStatus() != 1) {
			throw new CommonException(1003, "商品已下架");
		}

		order.setId(generateOrderId(orderType));
		order.setGoodsType(goods.getType());
		order.setGoodsName(goods.getName());
		order.setGoldNum(goods.getPrice());
		order.setEndTime(goods.getEndTime());
		order.setCreateTime(date);
		order.setUpdateTime(date);
		order.setStatus(orderType == 1 ? 0 : 1);
		exchangeOrderRepository.save(order);

		int stockNum = goods.getStockNum();
		goods.setStockNum(stockNum - 1);
		goods.setSalesNum(goods.getSalesNum() + 1);
		goods.setUpdateTime(date);
		goodsRepository.save(goods);

		// 虚拟商品分配券码
		if (orderType == 0) {
			goodsItem(goods.getId(), order.getUserId(), order.getId());
		}

		int goldNum = -(goods.getPrice());
		goldRecordService.save(order.getUserId(), goldNum, GoldRecordTypeEnum.EXCHANGE);

	}

	private String generateOrderId(int orderType) {
		StringBuilder sb = new StringBuilder();
		sb.append("0");
		sb.append(orderType + 1);

		Date date = new Date();
		sb.append(DateUtils.format(date, "MMddHHmmssSSS"));
		return sb.toString();
	}

	private void goodsItem(Integer goodsId, String userId, String orderId) {
		GoodsItem goodsItem = goodsItemRepository.findByGoodsIdAndStatus(goodsId, 0);
		goodsItem.setUserId(userId);
		goodsItem.setOrderId(orderId);
		goodsItem.setStatus(1);
		goodsItem.setUpdateTime(new Date());
		goodsItemRepository.save(goodsItem);
	}

}
