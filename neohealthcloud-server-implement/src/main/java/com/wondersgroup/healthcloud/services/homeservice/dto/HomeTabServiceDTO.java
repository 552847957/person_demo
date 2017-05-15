package com.wondersgroup.healthcloud.services.homeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeTabServiceEntity;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by xianglinhai on 2017/5/15.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HomeTabServiceDTO {
    private String id;
    private String imgUrl; //图片地址
    private String hoplink; //跳转链接
    private int sort; // 排序

    public HomeTabServiceDTO(){}

    public HomeTabServiceDTO(HomeTabServiceEntity entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
