package com.wondersgroup.healthcloud.common.http.support.parms;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.wondersgroup.common.http.utils.JsonConverter;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;


/**
 * Created by nick on 2017/5/2.
 * 这个工具类是获取每个请求当中的请求数据
 * 并且从这些请求当中获取uid
 */
public class QueryParmUtils {

    /**
     * 获取get请求的query
     * @param request
     * @return
     */
    public static String getGetOrDeleteRequestQueryParam(HttpServletRequest request){
        return request.getQueryString();
    }

    /**
     * 获取post请求的body
     * @param request
     * @return
     * @throws IOException
     */
    public static String getPostRequestQueryParam(HttpServletRequest request) throws IOException {
        return Okio.buffer(Okio.source(request.getInputStream())).readString(Charsets.UTF_8);
    }

    public static String filterUid(String query){
        if(!StringUtils.isEmpty(query)){
            if(query.startsWith("{")){
                JsonNode jsonNode = JsonConverter.toJsonNode(query);
                return jsonNode.get("uid").asText();
            }else{
                Map<String, String> paramMap = Maps.newHashMap();
                String[] params = query.split("&");
                for(String sub: params){
                    String[] keyValues = sub.split("=");
                    if(keyValues.length==2){
                        paramMap.put(keyValues[0], keyValues[1]);
                    }
                }
                return paramMap.get("uid");
            }
        }
        return null;
    }
}
