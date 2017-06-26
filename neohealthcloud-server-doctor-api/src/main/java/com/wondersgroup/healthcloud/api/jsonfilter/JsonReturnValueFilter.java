package com.wondersgroup.healthcloud.api.jsonfilter;

import com.wondersgroup.healthcloud.common.http.support.jackson.ReturnValueEncodeFilter;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * Created by nick on 2017/6/26.
 */
@Configuration
public class JsonReturnValueFilter implements ReturnValueEncodeFilter {

    @Autowired
    private AppConfigService appConfigService;
    private static final String MAIN_AREA = "3101";
    private static final String ENABLE_ENCODE = "1";

    @Override
    public boolean needEncode() {
        AppConfig appConfig = appConfigService.findSingleAppConfigByKeyWord(MAIN_AREA, null,
                "is_encode", "2");
        if(appConfig!=null){
            String is_encode = appConfig.getData();
            if(is_encode.equalsIgnoreCase(ENABLE_ENCODE)){
                return true;
            }
        }
        return false;
    }
}
