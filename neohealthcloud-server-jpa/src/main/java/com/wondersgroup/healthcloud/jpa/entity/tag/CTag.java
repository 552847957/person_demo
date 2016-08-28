package com.wondersgroup.healthcloud.jpa.entity.tag;

import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by shenbin on 16/8/28.
 */
@Data
@Entity
@Table(name = "app_dic_tag")
public class CTag extends BaseEntity{

    private String tagname;

    private String tagmemo;

    private Integer tagsort;

    private String tagcolor;
}
