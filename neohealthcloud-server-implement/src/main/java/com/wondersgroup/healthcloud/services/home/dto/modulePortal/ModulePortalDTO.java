package com.wondersgroup.healthcloud.services.home.dto.modulePortal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 模块入口 （模块入口）
 * Created by xianglinhai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModulePortalDTO {
    private String id;
    private String itemName;
    private String iconUrl;
    private String mainTitle;
    private String subTitle;
    private String jumpUrl;
    private Integer sort;
    public ModulePortalDTO(){}

    public ModulePortalDTO(ModulePortal entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
