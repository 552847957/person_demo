package com.wondersgroup.healthcloud.jpa.entity.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 匿名账户表
 * Created by sunhaidi on 2016.8.5
 */
@Data
@Entity
@Table(name = "app_tb_family_member_invitation")
public class AnonymousAccount extends BaseEntity {
    @Id
    private String id;
    private String username;
    private String password;
    private String creator;
    private String name;
    private String idcard;
}
