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

	private String detailedAddress;

	public String getDetailedAddress() {
		StringBuilder sb = new StringBuilder();
		switch (goodsType) {
		case 0:
			break;
		case 1:
			sb.append("上海市");
			sb.append(getArea());
			sb.append(getAddress());
			break;
		case 2:
			sb.append(getStreet());
			sb.append(getAddress());
			break;
		default:
			break;
		}
		return sb.toString();
	}

}
