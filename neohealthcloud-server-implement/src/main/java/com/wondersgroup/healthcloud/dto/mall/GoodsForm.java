package com.wondersgroup.healthcloud.dto.mall;

import java.util.Set;

import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;

import lombok.Data;

@Data
public class GoodsForm {

	Goods goods;
	Set<String> items;

}
