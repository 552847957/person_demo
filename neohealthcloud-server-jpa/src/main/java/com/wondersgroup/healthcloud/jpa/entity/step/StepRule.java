package com.wondersgroup.healthcloud.jpa.entity.step;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "step_rule_tb")
public class StepRule {

	@Id
	private String id;

	private String content;

	private Integer type; // 0：计步活动；1：计步红包

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "update_time")
	private Date updateTime;
}
