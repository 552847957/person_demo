package com.wondersgroup.healthcloud.services.mall.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonNaming
@JsonInclude(Include.NON_NULL)
public class GoodsDto extends Goods {

	boolean hasExchange;

}
