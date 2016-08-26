package com.wondersgroup.healthcloud.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.JsonNodeResponseWrapper;
import com.wondersgroup.healthcloud.utils.InterfaceEnCode;

/**
 * Created by longshasha on 16/6/24.
 */
public class FamilyDoctorUtil {


    private HttpRequestExecutorManager httpRequestExecutorManager;

    public void setHttpRequestExecutorManager(HttpRequestExecutorManager httpRequestExecutorManager) {
        this.httpRequestExecutorManager = httpRequestExecutorManager;
    }

    /**
     * 根据身份证号获取家庭医生信息
     * @param personcard
     * @return
     */
    public JsonNode getFamilyDoctorByUserPersoncard(String baseUrl,String personcard){
        String url = baseUrl + "/api/sign/signdoctor";
        String[] form = new String[]{"personcard", personcard};
        String[] header = new String[]{"access-token", InterfaceEnCode.getAccessToken()};
        Request request = new RequestBuilder().get().url(url).params(form).headers(header).build();
        JsonNodeResponseWrapper response = (JsonNodeResponseWrapper) httpRequestExecutorManager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode result = response.convertBody();
        return result;
    }
}
