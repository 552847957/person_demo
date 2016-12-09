package com.wondersgroup.healthcloud.services.mall.dto;

import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.jpa.entity.mall.MallBanner;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MallBannerDto extends MallBanner {

	private Goods goods;

	private String goodsName;

}
