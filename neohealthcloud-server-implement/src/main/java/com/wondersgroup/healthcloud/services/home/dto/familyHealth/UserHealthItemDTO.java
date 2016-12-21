package com.wondersgroup.healthcloud.services.home.dto.familyHealth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * 个人健康 异常项
 * Created by xianglihai on 2016/12/13.
 */
@Data
@JsonNaming
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserHealthItemDTO implements Comparable {
    private String name;
    private String data;
    private String hightAndLow;
    private Long testTime;


    @Override
    public int compareTo(Object o) {
        UserHealthItemDTO dto = (UserHealthItemDTO)o;
        return this.testTime.compareTo(dto.getTestTime());
    }
}
