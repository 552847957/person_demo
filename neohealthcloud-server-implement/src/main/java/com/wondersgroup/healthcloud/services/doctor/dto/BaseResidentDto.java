package com.wondersgroup.healthcloud.services.doctor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 居民信息基础数据<br/>
 * 医生端传递给app的数据,牵涉到居民信息的DTO类,需要继承自此基类,以保持字段统一<br/>
 * Created by limenghua on 2017/6/5.
 *
 * @author limenghua
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResidentDto {

    private String registerId;//用户id
    private String avatar;//居民头像
    private String name;//姓名
    private String gender;//性别，1：男，2：女
    private Integer age;//年龄
    private Boolean hypType;//标签:是否高血压 true:是 false:否
    private Boolean diabetesType;//标签:是否糖尿病 true:是 false:否
    private Boolean apoType;//标签:是否脑卒中 true:是 false:否
    private Boolean isRisk;//标签:是否高危人群 true:是 false:否
    private Boolean identifytype;//标签:是否实名认证用户 true:是 false:否
    private String address;// 地址
}
