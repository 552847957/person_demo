package com.wondersgroup.healthcloud.jpa.entity.area;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * 地区字典表
 * Created by sunhaidi on 2016.8.26
 */
@Data
@Entity
@Table(name = "t_dic_area_new")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DicArea {

    private static final long serialVersionUID = 1L;
    @Id
    private String            id;
    private String            level;                //地址层级 例如 北京,上海, 河北省属于1级, 城市,市辖属于2级, 区属于3级, 以此类推
    private String            code;                 //地址代码
    private String            upper_code;           //上级代码
    private String            firstping;            //地址中文首字母
    private String            remarks;
    private String            explain_memo;         //地址名称
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "update_by")
    private String updateBy;
    @Column(name = "update_date")
    private Date updateDate;
    
    /**
     * 获取地址层级 例如 北京,上海, 河北省属于1级, 城市,市辖属于2级, 区属于3级, 以此类推
     */

}
