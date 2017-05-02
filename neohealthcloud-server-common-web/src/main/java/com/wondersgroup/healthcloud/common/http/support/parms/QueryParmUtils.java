package com.wondersgroup.healthcloud.common.http.support.parms;


import com.google.common.base.Charsets;
import okio.Okio;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


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
}
