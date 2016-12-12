package com.wondersgroup.healthcloud.services.mall.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.mall.ExchangeOrder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming
@JsonInclude(Include.NON_NULL)
public class ExchangeOrderDto extends ExchangeOrder {

	private Integer goodsType; 
	
	private String userName;

	private String picture;
	
	private String ticketCode;

}
