package com.wondersgroup.healthcloud.jpa.entity.help;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by shenbin on 16/8/12.
 */
@Data
@Entity
@Table(name = "app_tb_helpcenter")
public class HelpCenter extends BaseEntity {

    private String title;

    private String content;

    private int sort;

    @Column(name = "is_visable")
    private String isVisable;
}
