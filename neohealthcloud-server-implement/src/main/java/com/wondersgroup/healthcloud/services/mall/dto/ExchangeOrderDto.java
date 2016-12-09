package com.wondersgroup.healthcloud.services.mall.dto;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming
public class ExchangeOrderDto extends ExchangeOrder{
	
	private String goodsName;
	
	private Integer goodsType;
	
	private String userName;

}
