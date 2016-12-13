package com.wondersgroup.healthcloud.jpa.repository.mall;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, String> {

	@Query(nativeQuery = true, value = "select * from exchange_order_tb where user_id = ?1 and goods_type = ?2 order by create_time desc limit 1")
	ExchangeOrder findByUserIdAndGoodsType(String userId, Integer goodsType);

}
