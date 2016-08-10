package com.wondersgroup.healthcloud.jpa.entity.user.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 家庭成员表
 * Created by sunhaidi on 2016.8.5
 */
@Data
@Entity
@Table(name = "app_tb_family_member")
public class FamilyMember extends BaseEntity {
    @Id
    private String  id;
    private String  uid;
    @Column(name = "pair_id")
    private String  pairId;      //一组关系`pair_id`相同|
    @Column(name = "member_id")
    private String  memberId;    //成员id
    private String  access;      //权限,类似于linux的权限, 目前只有一位, 表示健康档案的查阅权限',
    private String  status;      //'0未处理, 1已同意, 2已拒绝'
    private String  memo;        //成员备注
    private String  relation;    //成员与申请人关系',
    @Column(name = "relation_name")
    private String  relationName; //关系名,当关系是"其他"时, 将关系名保存在这个字段',
    @Column(name = "is_anonymous")
    private Integer isAnonymous; //是否匿名账户 默认0

}
