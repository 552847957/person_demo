package com.wondersgroup.healthcloud.api.http.dto.moduleportal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.Column;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by xianglinhai on 2016/12/12.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModulePortalViewDTO {
    private Integer id;

    private String itemName;

    private String iconUrl;

    private String mainTitle;

    private String subTitle;

    private String jumpUrl;

    private String isVisible;

    private int sort;

   public ModulePortalViewDTO(){}

    public ModulePortalViewDTO(ModulePortal entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
