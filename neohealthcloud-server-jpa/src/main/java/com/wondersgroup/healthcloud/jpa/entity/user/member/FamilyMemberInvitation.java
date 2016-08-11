package com.wondersgroup.healthcloud.jpa.entity.user.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 申请表`app_tb_family_member_invitation`
 * Created by sunhaidi on 2016.8.5
 */
@Data
@Entity
@Table(name = "app_tb_family_member_invitation")
public class FamilyMemberInvitation extends BaseEntity {
    @Id
    private String id;
    private String uid;         //申请人id
    @Column(name = "member_id")
    private String memberId;    //成员id
    private String relation;    //成员与申请人关系
    @Column(name = "relation_name")
    private String relationName; //关系名||当关系是"其他"时, 将关系名保存在这个字段
    private String access;      //权限
    private String status;      //`0`未处理, `1`已同意, `2`已拒绝
    private String memo;        //备注信息
}
