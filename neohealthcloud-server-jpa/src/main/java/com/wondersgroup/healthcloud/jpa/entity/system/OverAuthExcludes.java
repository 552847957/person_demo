package com.wondersgroup.healthcloud.jpa.entity.system;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Entity
@Data
@JsonNaming
@Table(name = "tb_overauth_excludes")
public class OverAuthExcludes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private  String excludesPath;//过滤路径
    
    private String type;//类型 1：用户端  2：医生端
}
