package com.wondersgroup.healthcloud.utils.urls;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * app h5 url build
 * Created by yanshuai on 16/03/11.
 */
@Component
public class H5Urls {

    @Autowired
    private Environment environment;

    private String baseUrl;

    private String baseUrl() {
        if (StringUtils.isEmpty(this.baseUrl)) {
            this.baseUrl = environment.getProperty("h5-web.connection.url");
        }
        return this.baseUrl;
    }
}
