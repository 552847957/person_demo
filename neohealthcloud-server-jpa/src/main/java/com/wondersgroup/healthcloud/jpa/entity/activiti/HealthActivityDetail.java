package com.wondersgroup.healthcloud.jpa.entity.activiti;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import com.wondersgroup.healthcloud.jpa.entity.BaseEntity;

/**
 * Created by sunhaidi on 2016.8.12
 */
@Data
@Entity
@Table(name = "app_tb_healthactivity_detail")
public class HealthActivityDetail extends BaseEntity {

    private static final long serialVersionUID = -1088507013792313496L;
    private String             id;
    private String             registerid;         //'注册id',
    private String             evaluate;           // '评价1：好评 2：中评 3：差评',
    private String             signtime;           // '报名时间',
    private String             evaluatetime;       // '评价时间',
    private String             evalatememo;        //评价内容
    private String             investtime;         //满意度调查时间
    private String             investcontent;      //满意度调查内容
    private String             activityid;         //'活动id'
    private String             pftitle;

}
