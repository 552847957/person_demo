package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

	@Autowired
	GoodsService goodsService;

	public Page<ExchangeOrderDto> list(Map map, int page, int size) {
		String goodsName = (String) map.get("goodsName");
		Integer goodsType = (Integer) map.get("goodsType");
		String customerName = (String) map.get("customerName");
		Integer orderStatus = (Integer) map.get("orderStatus");
		String orderId = (String) map.get("orderId");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");

		String sql = "select  a.* " + "from exchange_order_tb a";

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
		if (StringUtils.isNotBlank(customerName)) {
			sql += " and a.customer_name like '%" + customerName + "%' ";
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

		int start = page * size;
		String querySql = sql + " limit " + start + "," + size;
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
		int size = 10;
		int start = flag * size;
		String sql = "from exchange_order_tb a left join goods_tb b on a.goods_id = b.id where a.user_id = ?";
		String query = "select a.*, b.picture " + sql + " order by a.create_time desc limit ?, ?";

		List<ExchangeOrderDto> list = jdbcTemplate.query(query, new Object[] { userId, start, size },
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
		// 服务包没有库存限制
		if (orderType != 2 && 0 >= goods.getStockNum()) {
			throw new CommonException(1001, "商品已兑完");
		}

		if (goods.getPrice() > restGold) {
			throw new CommonException(1002, "金币不足");
		}

		if (isSoldOut(goods)) {
			throw new CommonException(1003, "商品已下架");
		}

		if (orderType != 1) {
			boolean hasExchange = goodsService.hasExchange(order.getGoodsId(), order.getUserId());
			if (hasExchange) {
				throw new CommonException(1004, "您已经兑换过此商品了:(");
			}
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
		
		if(orderType != 2){
			int stockNum = goods.getStockNum();
			goods.setStockNum(stockNum - 1);
			if(stockNum - 1 <= 0){
				goods.setSortNo(99999);
			}
		}
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
		
		if(orderType == 0){
			sb.append("02");
		}else if(orderType == 1) {
			sb.append("01");
		}else if(orderType == 2) {
			sb.append("03");
		}
		
		Date date = new Date();
		sb.append(DateUtils.format(date, "MMddHHmmssSSS"));
		
		Random random = new Random();
		int randomCode = random.nextInt(90) + 10;
		sb.append(randomCode);
		
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

	public ExchangeOrder address(String userId, Integer goodsType) {
		return exchangeOrderRepository.findByUserIdAndGoodsType(userId, goodsType);
	}

	/**
	 * 判断商品是否下架
	 * 
	 * @param goods
	 * @return true:已下架;false:未下架
	 */
	private boolean isSoldOut(Goods goods) {
		int status = goods.getStatus();
		if (status == 0) {
			return true;
		}

		Date endTime = goods.getEndTime();
		if (endTime != null) {
			return endTime.compareTo(new Date()) <= 0;
		}
		return false;
	}

}
