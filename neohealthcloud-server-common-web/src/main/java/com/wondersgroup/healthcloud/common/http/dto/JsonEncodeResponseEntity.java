package com.wondersgroup.healthcloud.common.http.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by nick on 2017/6/26.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonEncodeResponseEntity<T> extends JsonResponseEntity {

    private boolean encode;

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public JsonEncodeResponseEntity(){
        super();
    }

    public JsonEncodeResponseEntity(JsonResponseEntity jsonResponseEntity){
        setCode(jsonResponseEntity.getCode());
        setData(jsonResponseEntity.getData());
    }
}
