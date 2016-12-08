package com.wondersgroup.healthcloud.services.mall.dto;

import java.util.Map;

import lombok.Data;

@Data
public class GoodsSearchForm {

	private String name;

	private Integer type;

	private Integer status;

	private Integer flag;

	private Integer pageSize = 10;

	private String property = "stockNum";

	private String direction = "ASC";

	public GoodsSearchForm(Map params, int pageNo, int pageSize) {
		this.flag = pageNo;
		this.pageSize = pageSize;

		this.name = (String) params.get("name");
		this.type = (Integer) params.get("type");
		this.status = (Integer) params.get("status");
		this.property = (String) params.get("property");
		this.direction = (String) params.get("direction");

		if (this.pageSize == null) {
			this.pageSize = 10;
		}

		if (this.property == null) {
			this.property = "stockNum";
		}

		if (this.direction == null) {
			this.direction = "ASC";
		}
	}

}
