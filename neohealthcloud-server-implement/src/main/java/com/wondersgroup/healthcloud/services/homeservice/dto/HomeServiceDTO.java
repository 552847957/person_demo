package com.wondersgroup.healthcloud.services.homeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by xianglinhai on 2017/5/9.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeServiceDTO {
    private String id;
    private String mainTitle;  //主标题
    private String imgUrl; //图片地址
    private String hoplink; //跳转链接
    private int certified; //是否需要实名认证:0 不需要，1 需要
    private int serviceType;// 服务分类: 0 默认服务（APP端不允许删除）,1 基础服务，2 特色服务,3 医养云'
    private int allowClose;//允许关闭,0-不允许,1-允许
    private String delFlag;  //删除标志 0：不删除 1：已删除
    private int sort; // 排序
    private int isAdd; //是否在我的服务里添加过 0:未添加 1：已添加

    public HomeServiceDTO(){}

    public HomeServiceDTO(HomeServiceEntity entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
