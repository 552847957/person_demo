package com.wondersgroup.healthcloud.jpa.entity.mall;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
@Entity
@Table(name = "goods_hospital_tb")
@JsonNaming
public class GoodsHospital {

	@Id
	private String id;

	@Column(name = "area_code")
	private String areaCode;

	@Column(name = "hospital_name")
	private String hospitalName;

	@Column(name = "del_flag")
	private Integer delFlag; // 0： 未删除；1：删除

}
