package com.wondersgroup.healthcloud.common.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * app h5 url build
 * Created by longshasha on 16/08/26.
 */
@Component
public class JailPropertiesUtils {



    @Value("${gw-web.signed.connection.url}")
    private String gwWebSignedUrl;//签约配置地址

    public String getGwWebSignedUrl(){
        return gwWebSignedUrl;
    }

}
