package com.wondersgroup.healthcloud.api.http.dto.cloudtopline;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * Created by xianglinhai on 2016/12/12.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudTopLineViewDTO {

    private Integer id;

    private String name;

    private String iconUrl;

    private String title;

    private String jumpUrl;

    private Integer type;




    public CloudTopLineViewDTO(){}

    public CloudTopLineViewDTO(CloudTopLine entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
