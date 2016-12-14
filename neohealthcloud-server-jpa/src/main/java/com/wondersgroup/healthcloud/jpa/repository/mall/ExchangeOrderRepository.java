package com.wondersgroup.healthcloud.jpa.repository.mall;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, String> {

	@Query(nativeQuery = true, value = "select * from exchange_order_tb where user_id = ?1 and goods_type = ?2 order by create_time desc limit 1")
	ExchangeOrder findByUserIdAndGoodsType(String userId, Integer goodsType);

	@Query(nativeQuery = true, value = "select * from exchange_order_tb where user_id = ?1 and goods_id = ?2 and to_days(create_time) = to_days(now()) ")
	List<ExchangeOrder> findByUserIdAndGoodsId(String userId, Integer goodsId);

}
