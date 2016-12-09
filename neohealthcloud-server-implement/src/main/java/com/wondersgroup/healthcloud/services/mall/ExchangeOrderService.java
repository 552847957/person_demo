package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;
import com.wondersgroup.healthcloud.jpa.repository.mall.ExchangeOrderRepository;
import com.wondersgroup.healthcloud.services.mall.dto.ExchangeOrderDto;

@Service
@Transactional
public class ExchangeOrderService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	ExchangeOrderRepository exchangeOrderRepository;

	public Page<ExchangeOrderDto> list(Map map, int page, int size) {
		String goodsName = (String) map.get("goodsName");
		Integer goodsType = (Integer) map.get("goodsType");
		String userName = (String) map.get("userName");
		Integer orderStatus = (Integer) map.get("orderStatus");
		String orderId = (String) map.get("orderId");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");

		String sql = "select  a.*, b.`name` as goodsName, b.type as goodsType, if(c.identifytype=1,c.name,c.nickname)  userName "
				+ "from exchange_order_tb a";

		sql += " left join goods_tb b on a.goods_id = b.id";
		if (goodsType != null) {
			sql += " and b.type =" + goodsType;
		}
		if (StringUtils.isNotBlank(goodsName)) {
			sql += " and b.name like '%" + goodsName + "%'";
		}

		sql += " left join  app_tb_register_info c on a.user_id = c.`registerid`";
		if (StringUtils.isNotBlank(userName)) {
			sql += " and ((c.identifytype = 0 and c.nickname like '%" + userName
					+ "%') or (c.identifytype = 1 and c.name like '%" + userName + "%'))";
		}

		sql += " where 1=1 ";
		if (StringUtils.isNotBlank(orderId)) {
			sql += " and a.id like  '%" + orderId + "%'";
		}
		if (orderStatus != null) {
			sql += " and a.status = " + orderStatus;
		}
		if (StringUtils.isNotBlank(startTime)) {
			sql += " and date_format(a.create_time, 'yyyy-MM-dd') >= '" + startTime + "'";
		}
		if (StringUtils.isNotBlank(endTime)) {
			sql += " and '" + endTime + "' > date_format(a.create_time, 'yyyy-MM-dd') ";
		}

		int start = page > 0 ? (page - 1) * size : 0;
		String querySql = sql + " limit " + start + "," + (start + size);
		System.out.println(querySql);
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
		String sql = "from exchange_order_tb a left join goods_tb b on a.goods_id = b.id and a.user_id = ?";
		String query = "select a.*, b.name as goodsName, b.picture, b.type as goodsType " + sql + " order by a.create_time desc limit ?, ?";

		List<ExchangeOrderDto> list = jdbcTemplate.query(query, new Object[] { userId, start, end },
				new BeanPropertyRowMapper<ExchangeOrderDto>(ExchangeOrderDto.class));

		int total = jdbcTemplate.queryForObject("select count(1)" + sql, new Object[] { userId }, Integer.class);
		return new PageImpl<>(list, new PageRequest(flag, size), total);
	}

}
