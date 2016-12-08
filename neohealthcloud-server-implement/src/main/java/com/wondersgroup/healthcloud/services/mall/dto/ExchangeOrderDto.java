package com.wondersgroup.healthcloud.services.mall.dto;

import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;

import lombok.Data;

@Data
public class ExchangeOrderDto extends ExchangeOrder{
	
	private String goodsName;
	
	private Integer goodsType;
	
	private String userName;

}
