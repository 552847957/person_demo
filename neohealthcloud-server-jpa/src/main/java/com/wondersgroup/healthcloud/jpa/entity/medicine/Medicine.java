package com.wondersgroup.healthcloud.jpa.entity.medicine;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import javax.persistence.*;

/**
 * Created by zhaozhenxing on 2017/04/15.
 */

@Data
@Entity
@Table(name = "app_tb_medicine_info")
public class Medicine {
    @Id
    @Column(name = "id")
    private Integer id;// id
    @Column(name = "type")
    private String type;// 类型(M-药,I-胰岛素)
    @Column(name = "brand")
    private String brand;// 品牌
    @Column(name = "name")
    private String name;// 名称
    @Column(name = "specification")
    private String specification;// 规格
    @Column(name = "unit")
    private String unit;// 单位
    @Column(name = "del_flag")
    private String delFlag;// 删除标志 0:未删除,1:已删除
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;// 创建时间
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;// 更新时间
}