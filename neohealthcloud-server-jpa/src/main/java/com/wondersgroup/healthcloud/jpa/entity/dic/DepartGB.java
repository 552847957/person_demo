package com.wondersgroup.healthcloud.jpa.entity.dic;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by shenbin on 16/8/9.
 */
@Data
@Entity
@Table(name = "t_dic_depart_gb")
public class DepartGB {

    @Id
    private String id;

    private String name;

    private String pid;



}
