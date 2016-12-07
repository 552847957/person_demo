package com.wondersgroup.healthcloud.api.http.dto.family;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberOrderDTO {
    private String uid;
    private String name;
    private String imgUrl;
    
    public FamilyMemberOrderDTO(String uid, String name, String imgUrl) {
        this.uid = uid;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
