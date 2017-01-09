package com.wondersgroup.healthcloud.jpa.entity.spread;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by nick on 2016/12/23.
 * @author nick
 */
@Data
@Entity
@Table(name = "app_tb_local_spread")
public class Evangelist {
    @Id
    private String id;
    @Column(name = "spread_code")
    private String spreadCode;
    private String name;
    @Column(name = "create_time")
    private Date createTime;
}
