package com.wondersgroup.healthcloud.common.http.support.jackson;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wondersgroup.common.http.utils.JsonConverter;
import com.wondersgroup.healthcloud.common.http.annotations.JsonEncode;
import com.wondersgroup.healthcloud.common.http.dto.JsonEncodeResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.utils.wonderCloud.DoctorKeyMap;
import com.wondersgroup.healthcloud.utils.wonderCloud.PatientKeyMap;
import com.wondersgroup.healthcloud.utils.wonderCloud.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by nick on 2017/6/26.
 */
public class PostJsonProcessor implements HandlerMethodReturnValueHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ReturnValueEncodeFilter returnValueEncodeFilter;

    private static final String DOCTOR = "com.wondersgroup.healthcloud.3101://doctor";

    public PostJsonProcessor(ReturnValueEncodeFilter returnValueEncodeFilter){
        this.returnValueEncodeFilter = returnValueEncodeFilter;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        boolean hasJsonAnno= returnType.getMethodAnnotation(JsonEncode.class) != null;
        return hasJsonAnno;

    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        JsonEncode jsonEncode = returnType.getMethodAnnotation(JsonEncode.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String appVersion = request.getHeader("app-version");
        String appName = request.getHeader("app-name");
        if(jsonEncode.encode()){
            if(returnValueEncodeFilter.needEncode()){
                String publicKey = appName.equalsIgnoreCase(DOCTOR)?DoctorKeyMap.getPublicKey(appVersion): PatientKeyMap.getPublicKey(appVersion);
                ObjectNode objectNode = (ObjectNode) JsonConverter.toJsonNode(JsonConverter.toJson(returnValue));
                if(returnValue instanceof JsonResponseEntity){
                    JsonResponseEntity jsonResponseEntity = (JsonResponseEntity) returnValue;
                    JsonEncodeResponseEntity jsonEncodeResponseEntity =
                            new JsonEncodeResponseEntity(jsonResponseEntity);
                    jsonEncodeResponseEntity.setEncode(true);
                    String dataJson = JsonConverter.toJson(jsonEncodeResponseEntity.getData());
                    String encodeData = getEncodeData(dataJson, publicKey);
                    objectNode.put("data", encodeData);
                    objectNode.put("encode", jsonEncodeResponseEntity.isEncode());

                    returnValue = JsonConverter.toJson(objectNode);
                }else if(returnValue instanceof JsonListResponseEntity){
                    JsonListResponseEntity responseEntity = (JsonListResponseEntity) returnValue;
                    String dataJson = JsonConverter.toJson(responseEntity.getData());
                    String encodeData = getEncodeData(dataJson, publicKey);
                    objectNode.put("data", encodeData);
                    objectNode.put("encode", true);
                    returnValue = JsonConverter.toJson(objectNode);
                }
            }else{
                returnValue = JsonConverter.toJson(returnValue);
            }
        }
        writeMessage(returnValue, webRequest.getNativeResponse(HttpServletResponse.class));
    }

    private void writeMessage(Object returnValue,HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write((String)returnValue);
            writer.close();
        } catch (IOException e) {
            logger.error(Exceptions.getStackTraceAsString(e));
        }
    }

    private String getEncodeData(String dataJson, String publicKey) throws Exception {
        return RSAUtil.encryptByPublicKey(dataJson, publicKey);
    }
}
